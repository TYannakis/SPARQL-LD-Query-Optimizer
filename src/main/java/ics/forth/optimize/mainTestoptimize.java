package ics.forth.optimize;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import ics.forth.formulas.Formula;
import ics.forth.formulas.Formula_4;
import ics.forth.formulas.Formula_5;
import ics.forth.formulas.Formula_6;
import ics.forth.query_analyzer.Query_analyzer;
import ics.forth.query_analyzer.Service_analyzer;
import ics.forth.utils.ReadFile;
import ics.forth.utils.Write2Excel;

public class mainTestoptimize {

	public static void main(String[] args) {
			//Jena functions
			Model model = ModelFactory.createDefaultModel();
			Query query=QueryFactory.create(new ReadFile().getQuery()); // The query to run
			
			//execQuery(query, model);
			
			
			//QueryExecution qexec = QueryExecutionFactory.create(query, model);
			System.out.println("QUERY TO BE EXECUTED\n"+query + "\nEND OF QUERY ");
			Query_analyzer qAnalyzer = new Query_analyzer(query);
			qAnalyzer.analyze();
			Query_Reorderer qReorder=new Query_Reorderer(query, qAnalyzer);

			Formula form;
			//Formula 4
			form=execFormula("Formula4",qAnalyzer);
			System.out.println(qReorder.order2query(form.getOrders().get(0)));
			System.out.println(qReorder.order2query(form.getOrders().get(1)));
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
			long startTime = get2mil(System.nanoTime());
			Formula form;
			long stopTime = get2mil(System.nanoTime());
			long elapsedTime = stopTime - startTime;
			switch(formula.toLowerCase().trim()) {
				case "formula4":
					form=new Formula_4(qAnalyzed);
					break;
				case "formula5":
					form=new Formula_5(qAnalyzed);
					break;
				case "formula6":
					form=new Formula_6(qAnalyzed);
					break;
				default:
					form=new Formula_4(qAnalyzed);
			}
			System.out.println("Formula 4: "+elapsedTime);
			System.out.println(form.getOrders().toString().replaceAll(",", "_"));
			System.out.println(form.getCosts() + " size" +form.getCosts().size());
			return form;
		}
		

		
		private static void printQueryStats(Query_analyzer qAnalyzer) {
			for (Service_analyzer service_analyzer : qAnalyzer.getServices()) {
				System.out.println(service_analyzer.getUri());
				System.out.println(service_analyzer.getPattern());
				System.out.println(service_analyzer.getTotalSubs() + " " + service_analyzer.getTotalPreds() + " " + service_analyzer.getTotalObjs());
			}
		}

}
