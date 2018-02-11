package test;

import java.io.ByteArrayOutputStream;

import org.apache.any23.Any23;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.source.HTTPDocumentSource;
import org.apache.any23.writer.RDFXMLWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;

public class main {

	public static void main(String[] args) throws TripleHandlerException {
		Any23 a = new Any23();
		a.setHTTPUserAgent("test-user-agent");
		ByteArrayOutputStream out= new ByteArrayOutputStream();
        TripleHandler handler;
        handler=new RDFXMLWriter(out);
        try {
            a.extract(ExtractionParameters.newDefault(),new HTTPDocumentSource(a.getHTTPClient(),"http://users.ics.forth.gr/~yannakis/"), handler);

        }catch(Exception e){
            System.out.println(e);
        }finally{ handler.close();}
        System.out.println(out.toString());

	}

}
