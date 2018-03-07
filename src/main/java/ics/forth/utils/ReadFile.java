package ics.forth.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Reads files
 * @author Thanos Yannakis (yannakis@ics.forth.gr)
 *
 */
public class ReadFile {
	private String query;
	private String file;
	
	public ReadFile() {
		read();

	}
	
	public ReadFile(String file) {
		this.file=file;
		readfile(file);
	}
	
	public void read() {
		readfile(Resources.INPUT_FILE);

	}
	
	public void readfile(String file) {
		File importfile = new File(file);
		try (BufferedReader br = new BufferedReader(new FileReader(importfile))) { // Read file
			StringBuilder query_builder = new StringBuilder();
			String line;
			while ((line=br.readLine()) != null) {
				/*
				 * if (!line.trim().toLowerCase().startsWith(Resources.prefix)) {*/
				query_builder.append(line);  
			}
			query = query_builder.toString(); 
		} catch (IOException ex) {
			System.out.println("Didnt find the File");
		}
	}
	public void readfile() {
		readfile(file);
	}
	
	/**
	 * Returns a list of random lines
	 * @param file	file Name
	 * @param lines lines to be returned
	 * @return
	 */
	public List<String> getRandomLines(String file, int lines) {
		int totalLines=getTotallines(file);
		if(totalLines<lines) {
			lines=totalLines;
		}
		Set<Integer> uniquelines=getRandomNumbers(lines,totalLines);

		List<String> strLines=null;
		//Get random lines from file depending on the random numbers we got 
		AtomicInteger index = new AtomicInteger();	
		try (Stream<String> stream = Files.lines(Paths.get(file))) {
			strLines=stream.filter(line -> uniquelines.contains(index.getAndIncrement())).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strLines;

	}
	public List<String> getRandomLines(int lines) {
		return getRandomLines(file,lines);
	}
	/**
	 * Returns a list of random numbers depending on file number of lines and preffered lines
	 * @param lines
	 * @param totalLines
	 * @return
	 */
	
	private Set<Integer> getRandomNumbers(int lines, int totalLines){
		List<Integer> uniquelines=IntStream.range(0,totalLines).boxed().collect(Collectors.toList());
		Collections.shuffle(uniquelines);
		uniquelines= uniquelines.subList(0, lines);
		return new HashSet<>(uniquelines);
	}
	
	private int getTotallines(String file) {
		int totalLines=0;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) { // Read file
			//String line;
			while (br.readLine() != null) {
				totalLines++;
			}
		} catch (IOException ex) {
			System.out.println("Didnt find the File");
		}
		return totalLines;
	}

	public String getQuery() {
		return query;
	}
	
}
