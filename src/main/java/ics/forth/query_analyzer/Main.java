package ics.forth.query_analyzer;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;


import ics.forth.formulas.Formula;
import ics.forth.formulas.GreedyVC;
import ics.forth.formulas.GreedyUVC;
import ics.forth.formulas.GreedyWUVC;
import ics.forth.formulas.GreedyJWUVC;
import ics.forth.formulas.JWUVC;
import ics.forth.formulas.VC;
import ics.forth.formulas.UVC;
import ics.forth.formulas.WUVC;
import ics.forth.optimize.Query_Reorderer;
import ics.forth.utils.QueryExecuter;
import ics.forth.utils.ReadFile;
import ics.forth.utils.Resources;
import ics.forth.utils.Write2Excel;
import notused.FormulaTest;

/**
 * Finds and evaluates Query Service patterns cost estimations .
 *
 * @author Thanos Yannakis (yannakis@ics.forth.gr)
 */
public class Main {
	/**
	 * Execute all formulas, export costs and real query execution times for each order
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		//Jena functions
		//Model model = ModelFactory.createDefaultModel();
		Query query=QueryFactory.create(new ReadFile().getQuery()); // The query to run
		System.out.println(query);
		//Get the handler for the query analyzer
		Query_analyzer qAnalyzer = new Query_analyzer(query);
		qAnalyzer.analyze();
		printQueryStats(qAnalyzer);
		//Get the handler for query service reorder
		Query_Reorderer qReorder=new Query_Reorderer(query, qAnalyzer);
		//get the handler for query execution
		QueryExecuter qExec= new QueryExecuter(Resources.TEST_REPETITIONS);
		List<Double> exec_times;
		//Create excel output 
		Write2Excel excel=new Write2Excel("Results");
		Formula form;
		//Formula VC
		form=execFormula("VC", qAnalyzer);
//		exec_times=null;
		exec_times=getExecTimes(form, qReorder, qExec);
		toExcel(form,excel, exec_times);
		//Formula UVC
		form=execFormula("UVC",qAnalyzer);
		exec_times=null;
//		exec_times=getExecTimes(form, qReorder, qExec);
		toExcel(form,excel, exec_times);
		//Formula WUVC
		form=execFormula("WUVC",qAnalyzer);
		exec_times=null;
		toExcel(form,excel, exec_times);
		//Formula JWUVC
		form=execFormula("JWUVC",qAnalyzer);
		exec_times=null;
		toExcel(form,excel, exec_times);
		//Formula JWUVC with greedy algorithm
		form=execFormula("greedy_JWUVC",qAnalyzer);
		exec_times=null;
		toExcel(form,excel, exec_times);
		excel.finish();
		System.out.println("Finished");
		
	}
	/**
	 * Executes Reordered queries and returns avg times or each order  
	 * @param form
	 * @param qReorder
	 * @param qExec
	 * @return
	 */
	public static List<Double> getExecTimes(Formula form, Query_Reorderer qReorder, QueryExecuter qExec){
		if(!Resources.COUNT_TIME || form==null) {
			return null;
		}
//		if(form==null) {
//			List<Double> time= new ArrayList<>();
//			time.add(qExec.plainExecQuery(qReorder.getStartQuery()));
//			return  time;
//		
//		}
		return qExec.execQueriesInSpecificOrder(form.getOrders(), qReorder);
//		return null;
	}
	
	/**
	 * write cost and average executions times to excel
	 * @param formula
	 * @param excel
	 * @param times
	 */
	private static void toExcel(Formula formula, Write2Excel excel, List<Double> times) {
		if(!Resources.EXPORT2EXCEL) {
			return;
		}
		if(Resources.COUNT_COST && formula!=null) {
			excel.append(formula.getClass().getSimpleName(), formula.getCosts(), formula.getOrders());
		}
		if(Resources.COUNT_TIME && times!=null) {
			excel.appendTimes(times);
		}
		
	}
	/**
	 * executes queries in the orders given by the formula
	 * @param formula formula results (order and costs)
	 * @param qReorder
	 */
	private static void execQueriesInSpecificOrder(Formula formula, Query_Reorderer qReorder) {
		Model model = ModelFactory.createDefaultModel();
		formula.getOrders().forEach(order -> execQuery(qReorder.order2query(order),model));
	}
	
	/**
	 * get to millisec
	 * @param nano
	 * @return
	 */
	private static long get2mil(long nano) {
		Double dmil=nano/1e6;
		return dmil.longValue();
	}
	
	private static void execQuery(Query query) {
		Model model = ModelFactory.createDefaultModel();
		execQuery(query,model);
	}
	/**
	 * executes query and return the results
	 * @param query
	 * @param model
	 */
	private static void execQuery(Query query, Model model) {
		try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qe.execSelect();
			// Output query results
			ResultSetFormatter.out(System.out, results, query);
			// ResultSetFormatter.consume(results);
			System.out.println("\n----------------------------------------------\n\n");
		} catch (Exception e) {
			System.out.println("Main Error " + e + " __ " + e.toString());
			// e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Execute the specific formula 
	 * @param formula formula to be executed (4-6)
	 * @param qAnalyzed input query analyzed
	 * @return formula results
	 */
	private static Formula execFormula(String formula, Query_analyzer qAnalyzed) {
		if(!Resources.COUNT_COST) {
			return null;
		}
		long startTime = get2mil(System.nanoTime());
		Formula form=null;
		switch(formula.toLowerCase().trim()) {
			case "VC":
					form=new VC(qAnalyzed);
				break;
			case "UVC":
					form=new UVC(qAnalyzed);
				break;
			case "WUVC":
					form=new WUVC(qAnalyzed);
				break;
			case "JWUVC":
					form=new JWUVC(qAnalyzed);
				break;
			case "greedy_VC":
					form=new GreedyVC(qAnalyzed);
				break;
			case "greeedy_UVC":
				form=new GreedyUVC(qAnalyzed);
				break;
			case "greeedy_WUVC":
				form=new GreedyWUVC(qAnalyzed);
			break;
			case "greeedy_JWUVC":
				form=new GreedyJWUVC(qAnalyzed);
			break;
			default:
				form=new GreedyJWUVC(qAnalyzed);
		}
		long stopTime = get2mil(System.nanoTime());
		long elapsedTime = stopTime - startTime;
		if(form!=null) {
			System.out.println(formula.toLowerCase().trim()+" "+elapsedTime);
			System.out.println(form.getOrders().toString().replaceAll(",", "_"));
			System.out.println(form.getCosts() + " size" +form.getCosts().size());
		}
		return form;
	}
	
	/**
	 * output to excel
	 * @param formula formula results
	 * @param excel excel handler
	 */
	private static void toExcel(Formula formula, Write2Excel excel) {
		excel.append(formula.getClass().getSimpleName(), formula.getCosts(), formula.getOrders());
	}
	
	
	private static void printQueryStats(Query_analyzer qAnalyzer) {
		for (Service_analyzer service_analyzer : qAnalyzer.getServices()) {
			System.out.println(service_analyzer.getUri());
			System.out.println(service_analyzer.getPattern());
			System.out.println(service_analyzer.getTotalSubs() + " " + service_analyzer.getTotalPreds() + " " + service_analyzer.getTotalObjs());
		}
	}
}
