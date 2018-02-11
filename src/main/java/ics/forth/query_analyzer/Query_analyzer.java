package ics.forth.query_analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;

/**
 * Todo: Check if service is <=1 Analyze Query for Services and stores their
 * information (variables) .
 *
 * @author Thanos Yannakis (yannakis@ics.forth.gr)
 */
public class Query_analyzer extends Var_Analyzer {
	private Query q; // The whole Query
	private Set<Service_analyzer> services; // The set of Services in the Query
	// private List<Node> totalSubs,totalPreds,totalObjs; //TotalVariables in Query
	private String queryPrologue, queryEpilogue;

	public Query_analyzer(Query q) {
		super();
		this.q = q;
		setPrologue();
		setEpilogue();
	}

	public Query getQ() {
		return q;
	}

	public Set<Service_analyzer> getServices() {
		return services;
	}

	/**
	 * Get a Query Q and analyze the Services S in Q as well as the variables v in S
	 */
	@Override
	public void analyze() {
		services = new HashSet<>();
		ElementWalker.walk(q.getQueryPattern(), new ElementVisitorBase() { // search in every each BGP
			@Override
			public void visit(ElementService el) { // Check if BGP is also a Service and analyze
				List<Node> subjects = new ArrayList<>(); // here we store the variables of each service as s,p,o
				List<Node> predicates = new ArrayList<>();
				List<Node> objects = new ArrayList<>();
				//Create a Service handler
				Service_analyzer serv = new Service_analyzer(el, subjects, predicates, objects, services.size()); 
				services.add(serv); // Add to Services
				ElementWalker.walk(el.getElement(), new ElementVisitorBase() { // check for each BGP in Service
					@Override
					public void visit(ElementPathBlock el) {
						serv.setPattern(el.getPattern()); // get Service pattern
						ListIterator<TriplePath> triples = el.getPattern().iterator(); // get each triple in pattern
						while (triples.hasNext()) { // analyze triples for variables
							TriplePath tp = triples.next();
							Node node;
							if ((node = tp.getSubject()).isVariable()) {
								subjects.add(node);
							}
							if ((node = tp.getPredicate()).isVariable()) {
								predicates.add(node);
							}
							if ((node = tp.getObject()).isVariable()) {
								objects.add(node);
							}
						}
						addSub(subjects);
						addPred(predicates);
						addObj(objects);
					}
				});
			}
		});

	}

	public String getQueryPrologue() {
		return queryPrologue;
	}

	public String getQueryEpilogue() {
		return queryEpilogue;
	}

	private void setPrologue() {
		String testS = q.toString();
		queryPrologue = testS.substring(0, testS.indexOf("WHERE")) + "WHERE {\n ";
	}

	private void setEpilogue() {
		String testS = q.toString().toLowerCase();
		queryEpilogue = testS.substring(testS.lastIndexOf("}"), testS.length() - 1);
		//
	}
}
