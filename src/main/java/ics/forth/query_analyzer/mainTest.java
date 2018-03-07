package ics.forth.query_analyzer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.jena.query.ARQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import ics.forth.utils.QueryExecuter;
import ics.forth.utils.ReadFile;

public class mainTest {
	private List<List<Integer>> permutationList;
	public static void main(String[] args) {
		System.out.println("Starting...");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println("Started at: "+dtf.format(now)); 	
		//ARQ.init();
		Query query=QueryFactory.create(new ReadFile("C5.txt").getQuery()); // The query to run
		//execQuery(query, model);
		System.out.println(query);
		QueryExecuter qExec= new QueryExecuter(1);
		System.out.println(qExec.plainExecQuery(query));
		/*
		mainTest t=new mainTest();
		
		Set<A> aa=new HashSet<>();
		A test= new A(2);test.setL(1, 2, 3); aa.add(test);
		test= new A(0);test.setL(1, 7, 8); aa.add(test);
		test= new A(3);test.setL(7, 7, 4); aa.add(test);
		test= new A(1);test.setL(4, 1, 1); aa.add(test);
		
		
		
		//aa.sort(Comparator.comparing(A::getA));
		List<A> aaa=aa.stream().sorted((o1,o2) -> Integer.compare(o1.getA(), o2.getA())).collect(Collectors.toList());
		for (int k = 0; k < aaa.size(); k++) {
			System.out.println(aaa.get(k).getA());
			System.out.println(aaa.get(k).getL());
		}
		
		Set<Integer> s=new HashSet<>();
		for (A a : aaa) {
			System.out.println("New ITER");
			t.calculateCost(a, s);
		}
		
		System.out.println("binds   "+s);
		System.out.println("Check if ssets were altered");
		for (int k = 0; k < aaa.size(); k++) {
			System.out.println(aaa.get(k).getA());
			System.out.println(aaa.get(k).getL());
		}
		List<Integer> l1=Stream.of(1,2,3,4).collect(Collectors.toList());
		System.out.println("MAGICC "+l1.stream().mapToDouble(d -> d).average().getAsDouble());
		
		
		List<List<Integer>> l=t.getPermutation(4);
		for (List<Integer> list : l) {
			System.out.println(list);
			//list.stream().reduce(i,j) -> list.get(i) > list.get(j) ? j :i);
			int minIdx = IntStream.range(0,list.size())
		            .reduce((i,j) -> list.get(i) > list.get(j) ? j : i)
		            .getAsInt();  // or throw
			System.out.println(minIdx);
		}
		List<Double> l1=Stream.of(1.0,2.0,3.0,0.0).collect(Collectors.toList());
		int minIdx = IntStream.range(0,l1.size())
	            .reduce((i,j) -> l1.get(i) > l1.get(j) ? j : i)
	            .getAsInt();  // or throw
		System.out.println(l1);
		System.out.println(minIdx);*/

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
	
	public List<List<Integer>> getPermutation(int numOfservices) {
		permutationList=new ArrayList<>();
		Set<Integer> servs=IntStream.range(0, numOfservices).boxed().collect(Collectors.toSet());
		permutation(servs, new Stack<Integer>(), numOfservices);
		return permutationList;
		
	}
	
	private void calculateCost(A service, Set<Integer> bindings) {
		Set<Integer> vars=service.getL();
		vars.removeAll(bindings);
		bindings.addAll(vars);
		System.out.println(vars.size() + " " + bindings.size());
	}
	
	private void permutation(Set<Integer> servs, Stack<Integer> permutationStack, int numOfservices) {
		if(permutationStack.size()==numOfservices) {
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
	
}

class A {
	private int a;
	private List<Integer> l;
	A(int a){
		this.a=a;
	}
	void setL(int a,int b,int c) {
		l= new ArrayList<>();
		l.add(a);l.add(b);l.add(c);
	}
	Set<Integer> getL(){return l.stream().collect(Collectors.toSet());}
	int getA() {return a;}
}

