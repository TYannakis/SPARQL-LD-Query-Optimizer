package ics.forth.formulas;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ics.forth.query_analyzer.Service_analyzer;

/**
 * Responsible for getting statistics by returning all possible graph pattern
 * ordering from a Query
 * 
 * @author Thanos Yannakis
 */
public abstract class Formula {
	private List<List<Integer>> permutationList;

	/**
	 * return weight for the order of Services
	 * 
	 * @param numOfservices
	 *            number of services in Query
	 * @param i
	 *            for number of service [1,2,...)
	 * @return
	 */
	protected double getWeightOrder(int numOfservices, int i) {
		return ((double) numOfservices - i + 1) / numOfservices;
	}

	/**
	 * 
	 * @param numOfservices
	 *            number of services in Query
	 * @return all possible orders of graph patterns in a Query
	 */
	protected List<List<Integer>> getPermutation(int numOfservices) {
		if (permutationList != null) {
			return permutationList;
		}
		permutationList = new ArrayList<>();
		Set<Integer> servs = IntStream.range(0, numOfservices).boxed().collect(Collectors.toSet());
		permutation(servs, new Stack<Integer>(), numOfservices);
		return permutationList;

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
			permutationList.add(new ArrayList<>(permutationStack));
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
	
	/**
	 * Order Services by index
	 * @param services All service graph patterns
	 * @return services in order
	 */
	public static List<Service_analyzer> servicesInorder(Set<Service_analyzer> services) {
		return services.stream().sorted((o1, o2) -> 
				Integer.compare(o1.getOrder(), o2.getOrder()))
				.collect(Collectors.toList());
	}

	/**
	 * return minimum Index of list
	 * 
	 * @param list
	 * @return
	 */
	protected static int getMinimum(List<Double> list) {

		return IntStream.range(0, list.size()).reduce((i, j) -> 
				list.get(i) > list.get(j) ? j : i).getAsInt();

	}

	public abstract List<Double> getCosts();

	public abstract List<List<Integer>> getOrders();

	public abstract List<Integer> getBestOrder();

	public abstract Double getBestCost();

}
