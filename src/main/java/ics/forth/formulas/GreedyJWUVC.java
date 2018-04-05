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

/**
 * implements formula 7 with greedy algorithm/ JWUVC
 * 
 * @author Thanos Yannakis (yannakis@ics.forth.gr)
 *
 */
public class GreedyJWUVC extends Formula {
	private Set<Service_analyzer> services;
	private List<Double> costs_f6;
	private List<List<Integer>> orders;
	private Set<Node> tempBindedVars;

	public GreedyJWUVC(Query_analyzer q) {
		services = q.getServices();
		init();
	}

	/**
	 * Initialize the total cost for each service graph pattern
	 */
	private void init() {
		calculateCosts();
	}

	/**
	 * Calculates the cost for each order of Services
	 */
	private void calculateCosts() {
		int numOfservices = services.size();
		// Order Service pattern
		List<Service_analyzer> servOrd = servicesInorder(services);
		costs_f6 = new ArrayList<>();
		// get all possible orders
		
		
		orders = new ArrayList<>();
		List<Integer> newOrder= new ArrayList<>();
		
		Set<Integer> usedServ= new HashSet<>();
		Set<Integer> unusedServ= getOrder(numOfservices);
		
		
		Set<Node> bindings = new HashSet<>();
		while(unusedServ.size()>0){
			System.out.println("---Size: " + unusedServ.size());
			tempBindedVars = new HashSet<>();
			double min = 999;
			int index=100;
			for(int s:unusedServ) {
				System.out.println("---Index: "+s);
				Set<Node> boundedBinds=new HashSet<>(bindings);
				Service_analyzer sa=servOrd.get(s);
				double tempCost=calculateCost(sa, boundedBinds);
				if(min>tempCost){
					min=tempCost;
					index=s;
					tempBindedVars=addBinds(sa.getSubs(), 
							sa.getPreds(), sa.getObjs());
				}
			}
			System.out.println("---MIN: " + index + "  with: " + min);
			System.out.println("---tempBinds "+tempBindedVars);
			bindings.addAll(tempBindedVars);
			System.out.println("---All Binds " +bindings);
			newOrder.add(index);
			unusedServ.remove(index);
			usedServ.add(index);
		}
		orders.add(newOrder);
		costs_f6.add(0.0);
	}
	
	private Set<Integer> getOrder(int servOrd){
		Set<Integer> l=new HashSet<>();
		
		for(int i=0;i<servOrd;i++) {
			l.add(i);
		}
		return l;
	}
	
	// private List<Service_analyzer> servicesInorder(int size) {
	// return services.stream().sorted((o1,o2) -> Integer.compare(o1.getOrder(),
	// o2.getOrder())).collect(Collectors.toList());
	// }

	public List<Double> getCosts() {
		return costs_f6;
	}

	public List<List<Integer>> getOrders() {
		return orders;
	}
	
//	private List<Double> getJoins(List<Node> vars_s, List<Node> vars_p,List<Node> vars_o){
//		List<Double> joins= new ArrayList<>();
//		int jsubs=countDuplicates(vars_s);
//		joins.add(1+jsubs*Resources.J_Ts);
//		int jpreds=countDuplicates(vars_p);
//		joins.add(1+jpreds*Resources.J_Tu);
//		int jobjs=countDuplicates(vars_o);
//		joins.add(1+jobjs*Resources.J_Tu);
//		return joins;
//	}
	
	
	
	private double getStarJoins(List<Node> vars_s, List<Node> vars_o){
		double starJoinWeight=0.0;
		int jsubs=countDuplicates(vars_s);
		int jobjs=countDuplicates(vars_o);
		starJoinWeight=(jsubs+jobjs)*Resources.J_Ts;
		System.out.println("||||STARj " +(jsubs+jobjs));
		return starJoinWeight;
	}
	
	private double getChainJoins(List<Node> vars_s, List<Node> vars_o) {
		int chainjoins=countDuplicates(vars_s,vars_o);
		System.out.println("||||CHAINj " +(chainjoins));
		return chainjoins*Resources.J_Tc;
	}
	
	private double getUnusualJoins(List<Node> vars_s,List<Node> vars_p, List<Node> vars_o) {
		int unusualjoins=countDuplicates(vars_s,vars_p) + countDuplicates(vars_p,vars_o)+ countDuplicates(vars_p);
		System.out.println("||||CHAINj " +(unusualjoins));
		return unusualjoins*Resources.J_Tu;
	}
	
//	private double getChainJ(List<Node> vars_s, List<Node> vars_p,List<Node> vars_o) {
//		int chainjoins=countDuplicates(vars_s,vars_o);
//		int unusualjoins=countDuplicates(vars_s,vars_p) + countDuplicates(vars_p,vars_o);
//		return 1+ (chainjoins*Resources.J_Tc)+ (unusualjoins*Resources.J_Tu);
//	}
	
	private int countDuplicates(List<Node> vars) {
		Map<Node,Integer> histogram= new HashMap<>();
		vars.forEach(s-> {
			histogram.put(s, (histogram.containsKey(s) ? histogram.get(s) : -1) +1);
			}
		);
		AtomicInteger dups= new AtomicInteger(0);
//		System.out.println(vars + " ______");
//		System.out.println(histogram.entrySet()+" -------");
		histogram.keySet().forEach(s -> {
			if(histogram.get(s) > 0)  dups.set(dups.get() + histogram.get(s)); 
		});
		//System.out.println("------" + dups.get());
		return dups.get();
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
	
	private double JoinsWeight(List<Node> vars_s, List<Node> vars_p, List<Node> vars_o) {
//		System.out.println("|||| Star: "+getStarJoins(vars_s, vars_o)/Resources.J_Ts + " Chain: "+getChainJoins(vars_s, vars_o)/Resources.J_Tc  + " Unusual: "+ getUnusualJoins(vars_s, vars_p, vars_o)/Resources.J_Tu);
		return 1 + getStarJoins(vars_s, vars_o) + getChainJoins(vars_s, vars_o) + getUnusualJoins(vars_s, vars_p, vars_o);
	}
	
	/**
	 * return the service cost according to the bindings as a weighted sum
	 * 
	 * @param service
	 *            the current service
	 * @param bindings
	 *            the bindings used before this Service is used
	 * @return the weighted sum of each service
	 */
	private double calculateCost(Service_analyzer service, Set<Node> bindings) {
		Double weightedJoinedSum = getUnboundVarsCost(service.getSubs(), 
				service.getPreds(), service.getObjs(), bindings)/
				JoinsWeight(
						service.getSubs(), 
						service.getPreds(), 
						service.getObjs());
		return weightedJoinedSum;
	}

	/**
	 * Calculates the number of bound subs, preds and objects and return their
	 * weighted sum
	 * 
	 * @param vars_s
	 * @param vars_p
	 * @param vars_o
	 * @param bindings
	 * @return the weighted sum of all variables
	 */
	private double getUnboundVarsCost(List<Node> vars_s, List<Node> vars_p, 
			List<Node> vars_o, Set<Node> bindings) {
		int totalSubs, totalPreds, totalObjs;
		Set<Node> varsTotal = new HashSet<>();
		totalSubs = calculateVars(vars_s, varsTotal, bindings);
		totalObjs = calculateVars(vars_o, varsTotal, bindings);
		totalPreds = calculateVars(vars_p, varsTotal, bindings);
		return calculateTripleWeights(totalSubs, totalPreds, totalObjs);
	}

	/**
	 * Creates a collection with all variables that have been currently calculated
	 * and adds the bound variables to bindings
	 * 
	 * @param vars
	 *            A list of Sevice variables,can be subs or preds or objects
	 * @param varsTotal
	 *            a list of all variables that have been currently calculated
	 * @param bindings
	 * @return the number of unbounded variables
	 */
	private int calculateVars(List<Node> vars, Set<Node> varsTotal, Set<Node> bindings) {
		varsTotal.addAll(Var_Analyzer.getUniqueNodes(vars));
		varsTotal.removeAll(bindings);
		bindings.addAll(varsTotal);
		return varsTotal.size();
	}
	
	private Set<Node> addBinds(List<Node> vars_s, List<Node> vars_p, 
			List<Node> vars_o) {
		Set<Node> tempBinds= new HashSet<>();
		tempBinds.addAll(vars_s);
		tempBinds.addAll(vars_o);
		tempBinds.addAll(vars_p);
		return  tempBinds;
	}
	/**
	 * returns the weighted sum of subs, preds and objs
	 * 
	 * @param totalSubs
	 * @param totalPreds
	 * @param totalObjs
	 * @return
	 */
	private double calculateTripleWeights(int totalSubs, int totalPreds, int totalObjs) {
		return totalSubs * Resources.W_S + totalPreds * Resources.W_P + 
				totalObjs * Resources.W_O;
	}

	/**
	 * returns the best order of Services
	 */
	public List<Integer> getBestOrder() {
		return orders.get(getMinimum(costs_f6));

	}

	public Double getBestCost() {
		return costs_f6.get(getMinimum(costs_f6));

	}

}
