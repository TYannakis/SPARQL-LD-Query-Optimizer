package ics.forth.optimize;

import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.PathBlock;

import ics.forth.formulas.Formula;
import ics.forth.query_analyzer.Query_analyzer;
import ics.forth.query_analyzer.Service_analyzer;

/**
 * Rorders the Query pattern depending on the order of Services given as input
 * @author Thanos Yannakis (yannakis@ics.forth.gr)
 *
 */
public class Query_Reorderer {
	private Query startQuery;
	private Query_analyzer qAnalyzer;
	public Query_Reorderer(Query query, Query_analyzer qAnalyzer) {
		this.startQuery=query;
		this.qAnalyzer=qAnalyzer;
		if(query.getQueryType()!=Query.QueryTypeSelect) {
			System.err.println("Not Query Select");
		}
	}
	/**
	 * reorders the query startQuery depending on the order of Services given and return the new query
	 * @param orders order of Services 
	 * @return the new query
	 */
	public Query order2query(List<Integer> orders) {
		//get all services in the starting order
		List<Service_analyzer> services= Formula.servicesInorder(qAnalyzer.getServices());	
		StringBuilder qbuild= new StringBuilder();
		//Append the prefixes and Select .. Where{
		qbuild.append(qAnalyzer.getQueryPrologue());
		//Append to string the services in the order given and then add a newline
		orders.forEach(order -> qbuild.append(services.get(order).getServPattern()).append("\n"));
		//Append the ending } and possible limits/offsets after the bracket
		qbuild.append(qAnalyzer.getQueryFilter());
		qbuild.append(qAnalyzer.getQueryEpilogue());
		
		//System.out.println("||"+qbuild.toString()+"||");
		//Query qbuilt= QueryFactory.create(qbuild.toString());
		//System.out.println("||"+qbuilt.toString()+"||");
		
		return QueryFactory.create(qbuild.toString());
		
		
	}
	public Query getStartQuery() {
		return startQuery;
	}
	
}
