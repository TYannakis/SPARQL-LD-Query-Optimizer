package ics.forth.plugins;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

import ics.forth.utils.ReadFile;

public class GetRdfFromEndpoint {
	
    public static void main(String[] args) {
    	String file="getquery";
        String ontology_service = "http://bnb.data.bl.uk/sparql"; //"http://83.212.169.44:8891/sparql"
//        final String username = "dba";
//        final String password = "dba";
//        int offset=0;
        
//           String endpointsSparql =
//                "		select * where {\n"
//                + "		?s ?p ?o \n"
//                + "		} limit 200000 offset "+offset+"\n";
//        	String endpointsSparql =
//                  "		select * where {\n"
//                  + "		?s a ?o \n"
//                  + "		} limit 100";
        ReadFile rf= new ReadFile(file);
        rf.readfile();
    	Query query=QueryFactory.create(rf.getQuery()); 
        QueryExecution x = QueryExecutionFactory.sparqlService(ontology_service, query);
        ResultSet results = x.execSelect();
//        ArrayList<String> content=new ArrayList<>();
//        String temp="";
//        while(results.hasNext()){
//            QuerySolution qs=results.next();
////            System.out.println(qs.toString());
//            temp=transform(qs.toString());
//            content.add(temp);
//            //System.out.println(qs.get("?s"));
//            //System.out.println(qs.get("?p"));
//            //System.out.println(qs.get("?o"));
//            
////            System.out.println(temp);
//        }
        ResultSetFormatter.out(System.out, results);

            
        
    }
}
