package ics.forth.formulas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;

import ics.forth.query_analyzer.Query_analyzer;
import ics.forth.query_analyzer.Service_analyzer;

/**
 * implements formula 5/ VCB
 * @author Thanos Yannakis (yannakis@ics.forth.gr)
 *
 */
public class Formula_5 extends Formula {
	private Query_analyzer query;
	private Set<Service_analyzer> services;
	private List<Double> costs_f5;
	private List<List<Integer>> orders;
	public Formula_5(Query_analyzer q) {
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
		for (List<Integer> order : orders) {
			System.out.println("|||||ORDER5 "+order);
			//initialize empty bindings, totalcost of each run and iteration position
			double cost=0;
			int i=1;
			Set<Node> bindings= new HashSet<>();
			//for each possible order calculate cost, storing the bindings used and multiplying with weight
			for (Integer num : order) {
				double costTem=calculateCost(servOrd.get(num),bindings);
				cost+=costTem*getWeightOrder(numOfservices,i++);
				System.out.println("|||||||5| " + costTem + " " +getWeightOrder(numOfservices,i-1) );
			}
			costs_f5.add(cost);
		}
	}
	
//	private List<Service_analyzer> servicesInorder(int size) {
//		return services.stream().sorted((o1,o2) -> Integer.compare(o1.getOrder(), o2.getOrder())).collect(Collectors.toList());
//	}
	
	public List<Double> getCosts() {
			return costs_f5;
	}
	
	public List<List<Integer>> getOrders() {
			return orders;
	}
	/**
	 * return the service cost according to the bindings used previously
	 * @param service the current service
	 * @param bindings the bindings used before this Service is used
	 * @return
	 */
	private double calculateCost(Service_analyzer service, Set<Node> bindings) {
		Set<Node> vars=service.getUniqueVars();
		vars.removeAll(bindings);
		bindings.addAll(vars);
		//OLD COST
		//return ((double)vars.size())/query.getTotalVar();
		//NEW
		return ((double)vars.size());
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
