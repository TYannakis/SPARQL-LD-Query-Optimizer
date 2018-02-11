package ics.forth.plugins;

import ics.forth.utils.ReadFile;
import ics.forth.utils.WriteFile;

/**
 * Creates a smaller rdf file by getting random lines from a larger one
 * Can be configured to return specific number of lines
 * @author Thanos Yannakis (yannakis@ics.forth.gr)
 *
 */
public class CreateFileFromRandomFileLines {
	private static String largeRDF="test";
	private static String smallRDF="test1000";
	public static void main(String[] args) {
		//Read larger file
		ReadFile rf=new ReadFile(largeRDF);
		WriteFile wf=new WriteFile(smallRDF);
		//Write returned random lines to new file
		wf.write(rf.getRandomLines(1000));
	}
}
