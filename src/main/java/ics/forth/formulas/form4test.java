package ics.forth.formulas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.jena.graph.Node;

import ics.forth.query_analyzer.Query_analyzer;
import ics.forth.query_analyzer.Service_analyzer;
import ics.forth.query_analyzer.Var_Analyzer;
import ics.forth.utils.Resources;

/**
 * implements formula 6/ WVCB
 * 
 * @author Thanos Yannakis (yannakis@ics.forth.gr)
 *
 */
public class form4test extends Formula {
	private Set<Service_analyzer> services;
	private List<Double> costs_f6;
	private List<List<Integer>> orders;
	private double minCost=9999.0;
	private List<Service_analyzer> servOrd;

	public form4test(Query_analyzer q) {
		services = q.getServices();
		init();
	}

	/**
	 * Initialize the total cost for each service graph pattern
	 */
	protected void init() {
		Set<Integer> servs = IntStream.range(0, services.size()).boxed().collect(Collectors.toSet());
		servOrd = servicesInorder(services);
		permutation(servs, new Stack<Integer>(), services.size());
	}

	/**
	 * returns all possible orders from a list
	 * 
	 * @param servs
	 * @param permutationStack
	 * @param numOfservices
	 */
	private void permutation(Set<Integer> servs, Stack<Integer> permutationStack, int numOfservices) {
		if (permutationStack.size() == numOfservices) {
			calculateCosts(new ArrayList<Integer>(permutationStack));
		}
		Integer[] items = servs.toArray(new Integer[0]);
		for (Integer i : items) {
			/* add current item */
			permutationStack.push(i);

			/* remove item from available item set */
			servs.remove(i);

			/* pass it on for next permutation */
			permutation(servs, permutationStack, numOfservices);

			/* pop and put the removed item back */
			servs.add(permutationStack.pop());
		}
	}
	
//	private void init() {
//		calculateCosts();
//	}

	/**
	 * Calculates the cost for each order of Services
	 */
	private void calculateCosts(ArrayList<Integer> order) {
		// Order Service pattern
		// get all possible orders
//		orders = getPermutation(numOfservices);
		int ii = 1;

		// initialize empty bindings, totalcost of each run and iteration position
		double cost = 0;
		Set<Node> bindings = new HashSet<>();
		// for each possible order calculate cost, storing the bindings used and then
		// multiplying with the appropriate weight
		for (Integer num : order) {
			cost += calculateCost(servOrd.get(num), bindings) * 
					getWeightOrder(services.size(), ii++);
		}
		if(minCost > cost)
		{
			minCost=cost;
		}
//			costs_f6.add(cost);
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
		Double weightedSum = getUnboundVarsCost(service.getSubs(), 
				service.getPreds(), service.getObjs(), bindings);
		return weightedSum;
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
