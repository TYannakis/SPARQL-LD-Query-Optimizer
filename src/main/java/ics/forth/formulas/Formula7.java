package ics.forth.formulas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.graph.Node;

import ics.forth.query_analyzer.Query_analyzer;
import ics.forth.query_analyzer.Service_analyzer;
import ics.forth.query_analyzer.Var_Analyzer;
import ics.forth.utils.Resources;

public class Formula7 extends Formula {
	private Query_analyzer query;
	private Set<Service_analyzer> services;
	private List<Double> costs_f5;
	private List<List<Integer>> orders;
	public Formula7(Query_analyzer q) {
		this.query=q;
		services = q.getServices();
		init();
	}
	/**
	 * calculate vartotal
	 */
	private void init() {
		calculateCosts();
	}
	/**
	 * Calculates the cost for each order of Services
	 */
	private void calculateCosts() {
		int numOfservices=services.size();
		//get services in their starting order
		List<Service_analyzer> servOrd=servicesInorder(services);
		costs_f5 = new ArrayList<>();
		//get all possible orders
		orders=getPermutation(numOfservices);
		int ii=1;
		for (List<Integer> order : orders) {
			//initialize empty bindings, totalcost of each run and iteration position
			double cost=0;
			ii=1;
			Set<Node> bindings= new HashSet<>();
			//for each possible order calculate cost, storing the 
			//bindings used and multiplying with weight
			for (Integer num : order) {
				double costTem=calculateCost(servOrd.get(num),bindings);
				cost+=costTem*
						getWeightOrder(numOfservices,ii++);
			}
			costs_f5.add(cost);
		}
	}
	
//	private List<Service_analyzer> servicesInorder(int size) {
//		return services.stream().sorted((o1,o2) -> Integer.compare(o1.getOrder(), 
	//o2.getOrder())).collect(Collectors.toList());
//	}
	
	public List<Double> getCosts() {
			return costs_f5;
	}
	
	public List<List<Integer>> getOrders() {
			return orders;
	}
	/**
	 * return the service cost according to the bindings as a weighted sum
	 * @param service the current service
	 * @param bindings the bindings used before this Service is used
	 * @return
	 */
	private double calculateCost(Service_analyzer service, Set<Node> bindings) {
		Double weightedJoinedSum=getUnboundVarsCost(
				service.getSubs(),service.getPreds(),service.getObjs(),bindings);
		return weightedJoinedSum;
	}
	
	private List<Double> getJoins(List<Node> vars_s, List<Node> vars_p,List<Node> vars_o){
		List<Double> joins= new ArrayList<>();
		int jsubs=countDuplicates(vars_s);
		joins.add(1+jsubs*Resources.J_Ts);
		int jpreds=countDuplicates(vars_p);
		joins.add(1+jpreds*Resources.J_Tu);
		int jobjs=countDuplicates(vars_o);
		joins.add(1+jobjs*Resources.J_Tu);
		return joins;
	}
	
	private int countDuplicates(List<Node> vars) {
		Map<Node,Integer> histogram= new HashMap<>();
		vars.forEach(s-> {
			histogram.put(s, (histogram.containsKey(s) ? histogram.get(s) : -1) +1);
			}
		);
		AtomicInteger dups= new AtomicInteger(0);
		histogram.entrySet().forEach(s -> {
			if(histogram.get(s) > 0)  dups.set(dups.get() + histogram.get(s)); 
		});
		return dups.get();
	}
	/**
	 * Calculates the number of bound subs, preds and objects and return their weighted sum
	 * @param vars_s
	 * @param vars_p
	 * @param vars_o
	 * @param bindings
	 * @return
	 */
	private double getUnboundVarsCost(List<Node> vars_s,List<Node> vars_p,List<Node> vars_o, Set<Node> bindings ){
		double totalSubs,totalPreds,totalObjs;
		Set<Node> varsTotal= new HashSet<>();
		List<Double> joins = getJoins(vars_s,vars_p,vars_o);
		totalSubs=calculateVars(vars_s,varsTotal,bindings)/joins.get(0);
		totalObjs=calculateVars(vars_o,varsTotal,bindings)/joins.get(1);
		totalPreds=calculateVars(vars_p,varsTotal,bindings)/joins.get(2);
		
		Double jWeight= getChainJ(vars_s,vars_p,vars_o);
		return calculateTripleWeights(totalSubs,totalPreds,totalObjs)/jWeight;
	}
	
	
	private double getChainJ(List<Node> vars_s, List<Node> vars_p,List<Node> vars_o) {
		int chainjoins=countDuplicates(vars_s,vars_o);
		int unusualjoins=countDuplicates(vars_s,vars_p) + countDuplicates(vars_p,vars_o);
		return 1+ (chainjoins*Resources.J_Tc)+ (unusualjoins*Resources.J_Tu);
	}
	
	private int countDuplicates(List<Node> vars_a, List<Node> vars_b) {
		Map<Node,Integer> histogram= new HashMap<>();
		vars_a.forEach(s-> {
			histogram.put(s, (histogram.containsKey(s) ? histogram.get(s) : 0) +1);
			}
		);
		AtomicInteger dups= new AtomicInteger(0);
		vars_b.forEach(b -> {
			if(histogram.containsKey(b) && histogram.get(b)>0) {
				dups.incrementAndGet();
				histogram.put(b, histogram.get(b)-1);
			}
		});
		return dups.get();
	}
	/**
	 * Creates a collection with all variables that have been currently calculated and adds the bound variables to bindings 
	 * @param vars	A list of Sevice variables,can be subs or preds or objects
	 * @param varsTotal a list of all variables that have been currently calculated
	 * @param bindings
	 * @return
	 */
	private int calculateVars(List<Node> vars, Set<Node> varsTotal, Set<Node> bindings ) {
		varsTotal.addAll(Var_Analyzer.getUniqueNodes(vars));
		varsTotal.removeAll(bindings);
		bindings.addAll(varsTotal);
		return varsTotal.size();
	}
	/**
	 * returns the weighted sum of subs, preds and objs
	 * @param totalSubs
	 * @param totalPreds
	 * @param totalObjs
	 * @return
	 */
	private double calculateTripleWeights(double totalSubs, double totalPreds, double totalObjs) {
		return totalSubs*Resources.W_S + totalPreds*Resources.W_P+ totalObjs*Resources.W_O;
	}
	
	/**
	 * returns the best order of Services
	 */
	public List<Integer> getBestOrder() {
		return orders.get(getMinimum(costs_f5)); 
		
	}
	public Double getBestCost() {
		return costs_f5.get(getMinimum(costs_f5));
		
	}
}
