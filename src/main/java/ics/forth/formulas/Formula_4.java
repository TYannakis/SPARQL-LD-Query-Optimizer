package ics.forth.formulas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import ics.forth.query_analyzer.Query_analyzer;
import ics.forth.query_analyzer.Service_analyzer;

/**
 * Implements Formula 4/ VC count
 * Todo: 
 * 		a)remove permutation, and have a more effiecent algorithm (get most restrictive first and so on)
 * 		b) remove check from getCosts/Orders
 * @author  Thanos Yannakis (yannakis@ics.forth.gr)
 *
 */
public class Formula_4 extends Formula {
	private Query_analyzer query;
	private Set<Service_analyzer> services;
	private List<Double> costs_f4;
	private List<List<Integer>> orders;
	public Formula_4(Query_analyzer q) {
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
		double cost=0;
		int numOfservices=services.size();
		Double[] services_cost_array=new Double[numOfservices];
		//Calculate its service var(g_i)
		for (Service_analyzer service : services) { 
			cost=calculateCost(service);
			services_cost_array[service.getOrder()]=cost;
		}
		List<Double> services_cost= Arrays.asList(services_cost_array);
		costs_f4 = new ArrayList<>();
		//get all possible orders
		orders=getPermutation(numOfservices);
		//for its order calculate the cost serv_i_cost * weight(i)
		for (List<Integer> order : orders) {
			cost=0;
			int i=1;
			for (Integer num : order) {
				cost+=services_cost.get(num)*getWeightOrder(numOfservices,i++);
			}
			costs_f4.add(cost);
		}

	}
	
	public List<Double> getCosts() {
			return costs_f4;
	}
	
	public List<List<Integer>> getOrders() {
			return orders;
	}

	private double calculateCost(Service_analyzer service) {
		//OLD
		//return ((double)service.getTotalVar())/query.getTotalVar();
		//NEW
		return ((double)service.getTotalVar());
	}
	
	/**
	 * returns the best order of Services
	 */
	public List<Integer> getBestOrder() {
		return orders.get(getMinimum(costs_f4)); 
		
	}
	public Double getBestCost() {
		return costs_f4.get(getMinimum(costs_f4));
		
	}
	
//	private boolean isCalculated(){
//		return costs_f4!=null;
//	}
	
	

}
