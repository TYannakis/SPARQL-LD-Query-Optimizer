package ics.forth.query_analyzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.any23.Any23;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.HTTPDocumentSource;
import org.apache.any23.source.StringDocumentSource;
import org.apache.any23.writer.NTriplesWriter;
import org.apache.any23.writer.RDFXMLWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.ccil.cowan.tagsoup.XMLWriter;
import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;



public class mainParallel {

	public static void main(String[] args) throws URISyntaxException, IOException {
		String iri="http://cold32.com/about-the-author-and-contact.html";
		iri="http://geoffreyphilp.blogspot.gr/2017/10/introduction-of-marlon-james-at-cornell.html?ref=source";
		iri="https://tapwage.com/job/7784322-industrial-design-lead-shanghai";
		Any23 runner=new Any23();
        runner.setHTTPUserAgent("test-user-agent");  
        ByteArrayOutputStream out= new ByteArrayOutputStream();
		TripleHandler handler=new RDFXMLWriter(out);
		ExtractionParameters extrParam= ExtractionParameters.newDefault();
		System.out.println(extrParam.isFix() + " " + extrParam.isValidate());
		extrParam.setFlag("any23.microdata.strict", true);
		
		try {
            runner.extract(extrParam,iri, handler);
        } catch (IOException  ex) {
            System.err.println("_IOExceptionERROR "+ex);
        } catch (ExtractionException ex) {
        	System.err.println("_ExtractionException "+ex);
        }finally{
        	System.out.println("__"+out);
        	 try{handler.close();}
    		 catch(TripleHandlerException ex){
    			 System.err.println("_HandlerERROR "+ex);}
        }
		
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//		TripleHandler handler = new NTriplesWriter(out);
//		try {
//			DocumentSource ds=getFixedElement(iri);
////			System.out.println(ds.openInputStream());
//			runner.extract(ds, handler);
//		} catch (ExtractionException | SAXException e) {
//			System.err.println("_ExtractionException "+e);
//			e.printStackTrace();
//		} finally {
//			 //String n3 = out.toString("UTF-8");
//			 //System.out.println("__ "+n3);
//			try{handler.close();}
//   		 	catch(TripleHandlerException ex){
//   			 System.err.println("_HandlerERROR "+ex);}
//        }	
		/*DocumentSource source = new HTTPDocumentSource(
				runner.getHTTPClient(),
		         "http://www.rentalinrome.com/semanticloft/semanticloft.htm"
		      );
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TripleHandler handler = new NTriplesWriter(out);
		try {
			runner.extract(source, handler);
		} catch (ExtractionException e) {
			System.err.println("_ExtractionException "+e);
			e.printStackTrace();
		} finally {
			 String n3 = out.toString("UTF-8");
			 System.out.println("__ "+n3);
			try{handler.close();}
   		 	catch(TripleHandlerException ex){
   			 System.err.println("_HandlerERROR "+ex);}
        }*/
		 String n3 = out.toString("UTF-8");
		 System.out.println(n3);
	}
	public static DocumentSource getFixedElement(String iri) throws SAXException, IOException {
		// create TagSoup reader
		XMLReader reader = SAXParserImpl.newInstance(null).getXMLReader();

		// use its writer to serialize SAX to string
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLWriter writer = new XMLWriter(new OutputStreamWriter(baos));
		reader.setContentHandler( writer);

		// prepare input for TagSoup
		URL url = new URL(iri);
		Reader urlReader = new InputStreamReader(url.openStream());

		// actually read url and serialize repaired DOM tree to string
		reader.parse(new InputSource(urlReader));

		// use results as Any23 input
		String input = new String(baos.toByteArray(), Charset.forName("UTF-8"));
		//System.out.println(input);
		return new StringDocumentSource(input, iri);
	}
}
