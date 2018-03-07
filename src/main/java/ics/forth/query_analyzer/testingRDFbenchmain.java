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
import ics.forth.formulas.Formula7greedy;
import ics.forth.formulas.Formula7test;
import ics.forth.formulas.FormulaTest;
import ics.forth.formulas.Formula_4;
import ics.forth.formulas.Formula_5;
import ics.forth.formulas.Formula_6;
import ics.forth.optimize.Query_Reorderer;
import ics.forth.utils.QueryExecuter;
import ics.forth.utils.ReadFile;
import ics.forth.utils.Resources;
import ics.forth.utils.Write2Excel;

public class testingRDFbenchmain {

	public static void main(String[] args) {
		//Jena functions
				//Model model = ModelFactory.createDefaultModel();
				
				List<String> queries= new ArrayList<String>();
				queries.add("S2");
				queries.add("S3");
				queries.add("S4");
				queries.add("S5");
				queries.add("S10");
				queries.add("S11");
				queries.add("S12");
				queries.add("S13");
				queries.add("S14");
				queries.add("C1");
				queries.add("C2");
				queries.add("C5");
				queries.add("C6");
				queries.add("C7");
				queries.add("C8");
				queries.add("C9");
				queries.add("C10");
				for(String qq: queries) {
				Query query=QueryFactory.create(new ReadFile(qq+".txt").getQuery()); // The query to run
//				QueryExecuter qExec2= new QueryExecuter(1);
//				System.out.println(qExec2.plainExecQuery(query));
				//execQuery(query, model);
				System.out.println("----------QUERY\n" +query + "\n------- ");
				//Get the handler for the query analyzer
				Query_analyzer qAnalyzer = new Query_analyzer(query);
				qAnalyzer.analyze();
				System.out.println("---------STATS");
				printQueryStats(qAnalyzer);
				System.out.println("---------");
				//Get the handler for query service reorder
				Query_Reorderer qReorder=new Query_Reorderer(query, qAnalyzer);
				//get the handler for query execution
				QueryExecuter qExec= new QueryExecuter(Resources.TEST_REPETITIONS);
				List<Double> exec_times;
				
				Write2Excel excel=new Write2Excel(qq);
				Formula form;
				//Formula 4
				form=execFormula("Timers", qAnalyzer);
//				exec_times=null;
				exec_times=null;//getExecTimes(form, qReorder, qExec);
				if(exec_times!=null) {
					toExcelTime(form,excel, exec_times);
				}
				
				form=execFormula("formula7greedy", qAnalyzer);
//				exec_times=null;
				exec_times=null;
				toExcel(form,excel, exec_times);
				
				/*
				form=execFormula("formula4", qAnalyzer);
//				exec_times=null;
				exec_times=null;
				toExcel(form,excel, exec_times);

				form=execFormula("formula5", qAnalyzer);
//				exec_times=null;
				exec_times=null;
				toExcel(form,excel, exec_times);

				form=execFormula("formula6", qAnalyzer);
//				exec_times=null;
				exec_times=null;
				toExcel(form,excel, exec_times);

				form=execFormula("formula7", qAnalyzer);
//				exec_times=null;
				exec_times=null;
				toExcel(form,excel, exec_times);
				//finish writing to exc
				*/
				excel.finish();
				}
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
	private static void toExcelTime(Formula formula, Write2Excel excel, List<Double> times) {
		if(!Resources.EXPORT2EXCEL) {
			return;
		}
		excel.append2(formula.getClass().getSimpleName(), times, formula.getOrders());
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
			case "formula4":
				if(Resources.USE_FORMULA_4)
					form=new Formula_4(qAnalyzed);
				break;
			case "formula5":
				if(Resources.USE_FORMULA_5)
					form=new Formula_5(qAnalyzed);
				break;
			case "formula6":
				if(Resources.USE_FORMULA_6)
					form=new Formula_6(qAnalyzed);
				break;
			case "formula7":
				if(Resources.USE_FORMULA_7)
					form=new Formula7test(qAnalyzed);
				break;
			case "formula7greedy":
					form=new Formula7greedy(qAnalyzed);
				break;
				
			default:
				form=new FormulaTest(qAnalyzed);
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
