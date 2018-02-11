package ics.forth.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import ics.forth.optimize.Query_Reorderer;
/**
 * Execute reordered queries and retrieve average execution times
 * @author Thanos Yannakis (yannakis@ics.forth.gr)
 *
 */
public class QueryExecuter {
	private List<Double> times;
	private Model model;
	private int repetitions;
	public QueryExecuter(int repetitions) {
		model = ModelFactory.createDefaultModel();
		this.repetitions=repetitions;
	}
	/**
	 * Executes each reordered query and return average time
	 * @param orders
	 * @param qReorder
	 * @return
	 */
	public List<Double> execQueriesInSpecificOrder(List<List<Integer>> orders, Query_Reorderer qReorder){
		times=new ArrayList<>();
		orders.forEach(order -> {
			System.out.println(order);
			if(Resources.ENABLE_REMOVER && (order.get(0)==Resources.REMOVE_ORDER || order.get(0)==4)) {
				times.add(999999999.0);
			}else {
				times.add(execQuery(qReorder.order2query(order)));
			}
		});
		return times;
	}
	/**
	 * Ececute query x times and export an average time
	 * @param query
	 * @return
	 */
	private Double execQuery(Query query) {
		AtomicInteger j=new AtomicInteger(0);
		List<Long> temp_times=new ArrayList<>();
		IntStream.range(0,repetitions).forEach(
				i -> {
					Resources.SERVICES_RUN=0;
					System.out.println(j.getAndIncrement());
					long startTime = get2mil(System.nanoTime());
					try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
						if (query.isSelectType()) {
							ResultSet results = qe.execSelect();
							// Output query results
	//						ResultSetFormatter.out(System.out, results, query);
							 //dont show results
							ResultSetFormatter.consume(results);
						}else {
							checkQType(qe,query,false);
						}
						temp_times.add(get2mil(System.nanoTime()) - startTime);
						System.out.println(get2mil(System.nanoTime()) - startTime);
						System.out.println("\n----------------------------------------------\n\n");
					} catch (Exception e) {
						System.out.println("Main Error " + e + " __ " + e.toString());
						 e.printStackTrace();
					}
					//calculate time
					
					
					
				}
		);
		return temp_times.stream().mapToDouble(d -> d).average().getAsDouble();
		
	}
	/**
	 * Just executes Query, displays results and returns execution time 
	 * @param query
	 * @return
	 */
	public double plainExecQuery(Query query) {
		long startTime = get2mil(System.nanoTime());
		try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {

			//check if is Select 
			if (query.isSelectType()) {
				ResultSet results = qe.execSelect();
				// Output query results
				ResultSetFormatter.out(System.out, results, query);
				 //dont show results
				//ResultSetFormatter.consume(results);
			}else {
				checkQType(qe,query,true);
			}
			System.out.println("\n----------------------------------------------\n\n");
		} catch (Exception e) {
			System.out.println("Main Error " + e + " __ " + e.toString());
			// e.printStackTrace();
		}
		//calculate time
		return get2mil(System.nanoTime()) - startTime;
	}
	
	private long get2mil(long nano) {
		Double dmil=nano/1e6;
		return dmil.longValue();
	}
	
	private void checkQType(QueryExecution qe, Query q, boolean isPrinted) {
		Model model=null;
		 if (q.isConstructType()) {
	        model = qe.execConstruct();
//	        result = new SPARQLResult(model);
	    }else if (q.isDescribeType()) {
	        model = qe.execDescribe();
//	        result = new SPARQLResult(model);
	    }
	    else if (q.isAskType()) {
	        boolean b = qe.execAsk();
	        System.out.println(b);
//	        result = new SPARQLResult(b);
	    }
		 if(isPrinted)
			 model.write(System.out, "TURTLE");
	}
	
}
