package ics.forth.query_analyzer;

import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.PathBlock;
import org.apache.jena.sparql.syntax.ElementService;

/**
 * Store Service pattern elements (pattern,variables and service call operator)
 * 
 * @author Thanos Yannakis (yannakis@ics.forth.gr)
 *
 */
public class Service_analyzer extends Var_Analyzer {
	// private List<Node> vars_s,vars_p,vars_o; //Service vars
	private final String uri; // Service URI
	private PathBlock pattern; // Service block pattern
	private ElementService servPattern; // service pattern
	private int order;

	public Service_analyzer(String uri, PathBlock pattern, int order) {
		super();
		this.pattern = pattern;
		this.uri = uri;
		this.order = order;
	}

	public Service_analyzer(String uri, int order) {
		super();
		this.uri = uri;
		this.order = order;
	}

	public Service_analyzer(String uri, List<Node> s, List<Node> p, List<Node> o, int order) {
		super(s, p, o);
		this.uri = uri;
		this.order = order;
	}

	public Service_analyzer(ElementService servPattern, List<Node> s, List<Node> p, List<Node> o, int order) {
		super(s, p, o);
		this.order = order;
		this.servPattern = servPattern;
		this.uri = servPattern.getServiceNode().toString();
	}

	public int getOrder() {
		return order;
	}

	public ElementService getServPattern() {
		return servPattern;
	}

	public PathBlock getPattern() {
		return pattern;
	}

	public void setPattern(PathBlock pattern) {
		this.pattern = pattern;
	}

	public void setServPattern(ElementService servPattern) {
		this.servPattern = servPattern;
	}

	public String getUri() {
		return uri;
	}

	@Override
	public void analyze() {
	}

}
