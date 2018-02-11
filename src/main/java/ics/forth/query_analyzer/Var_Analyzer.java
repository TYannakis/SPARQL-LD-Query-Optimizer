package ics.forth.query_analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.graph.Node;

/**
 * Responsible to organize pattern variables
 * 
 * @author Thanos Yannakis (yannakis@ics.forth.gr)
 *
 */
public abstract class Var_Analyzer {
	private List<Node> subs, preds, objs; // TotalVariables in Query

	public Var_Analyzer() {
		subs = new ArrayList<>();
		preds = new ArrayList<>();
		objs = new ArrayList<>();
	}

	public Var_Analyzer(List<Node> s, List<Node> p, List<Node> o) {
		subs = s;
		preds = p;
		objs = o;
	}

	public void addSub(List<Node> n) {
		subs.addAll(n);
	}

	public void addPred(List<Node> n) {
		preds.addAll(n);
	}

	public void addObj(List<Node> n) {
		objs.addAll(n);
	}

	public List<Node> getSubs() {
		return subs;
	}

	public List<Node> getPreds() {
		return preds;
	}

	public List<Node> getObjs() {
		return objs;
	}

	public int getTotalSubs() {
		return subs.size();
	}

	public int getTotalPreds() {
		return preds.size();
	}

	public int getTotalObjs() {
		return objs.size();
	}

	/**
	 * returns the vars from the BGP
	 * 
	 * @return
	 */
	public List<Node> getVars() {
		return Stream.concat(Stream.concat(subs.stream(), preds.stream()), objs.stream()).collect(Collectors.toList());
	}

	public Set<Node> getUniqueVars() {
		return getUniqueNodes(getVars());
	}

	/**
	 * returns the number of vars from the BGP
	 * 
	 * @return
	 */
	public int getTotalVar() {
		return getTotalSubs() + getTotalPreds() + getTotalObjs();
	}

	/**
	 * get total number of unique vars
	 * 
	 * @return
	 */
	public int getUniqueTotalVar() {
		return getUniqueNodes(getVars()).size();
	}

	/**
	 * return unique variables
	 * 
	 * @param vars
	 * @return
	 */
	public static Set<Node> getUniqueNodes(List<Node> vars) {
		Set<Node> uvars = new HashSet<Node>(vars);
		return uvars;
	}

	public abstract void analyze();
}
