package ics.forth.formulas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import ics.forth.query_analyzer.Query_analyzer;
import ics.forth.query_analyzer.Service_analyzer;

public class FormulaTest extends Formula {
	private Query_analyzer query;
	private Set<Service_analyzer> services;
	private List<Double> costs_f4;
	private List<List<Integer>> orders;
	public FormulaTest(Query_analyzer q) {
		this.query=q;
		services = q.getServices();
		init();
	}
	/**
	 * calculate vartotal
	 */
	private void init() {
		costs_f4=new ArrayList<>();
		orders=getPermutation(services.size());
		for (int i = 0; i < orders.size(); i++) {
			costs_f4.add(0.0);
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
}
