package ics.forth.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ics.forth.utils.Resources;
import ics.forth.utils.Write2Excel;

/**
 * internal Excel fixer for times
 * @author Thanos Yannakis (yannakis@ics.forth.gr)
 *
 */
public class excelFix {
	public static String filename="test_results - Copy.xlsx";
	private FileInputStream excelFile;
	private XSSFWorkbook workbook;
	private XSSFSheet datatypeSheet;
	private int numRows=0;
	private int numCols=0;
	private List<List<Integer>> orders;
	private List<Double> times;
	private int servNum=4;
//	private List<List<Integer>> permutationList;
	excelFix(){
		File f=new File(filename);
		try {
			if(f.exists()) {
				 	excelFile = new FileInputStream(f);
					workbook = new XSSFWorkbook(excelFile);
					datatypeSheet = workbook.getSheetAt(Resources.SHEET_INDEX);
					numRows = 0;
					readCols();
					
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
	}
	
	private void readCols() {
		Iterator<Row> iterator = datatypeSheet.iterator();
		int cols=0;
        while (iterator.hasNext()) {
        	Row currentRow = iterator.next();
        	if(cols==1) {
        		cols++;
        		continue;}
        	else if(cols==0) {
        		readOrders(currentRow);
        	}else if(cols==2) {
        		readTime(currentRow);
        	}
        	cols++;
        	
            

        }
        servNum=orders.get(0).size();

	}
	private void readOrders(Row currentRow) {
		orders= new ArrayList<>();
		Iterator<Cell> cellIterator = currentRow.iterator();
        int i=0;
        while (cellIterator.hasNext()) {
        	Cell currentCell = cellIterator.next();
        	if(i++==0) {continue;}
            if (currentCell.getCellTypeEnum() == CellType.STRING) {
                System.out.println(currentCell.getStringCellValue() + "--");
            	orders.add(addOrder(currentCell.getStringCellValue()));
            }
        }
	}
	
	private List<Integer> addOrder(String cell) {
		String str=cell.substring(1,cell.length()-1).trim();
		String[] ordersSplited= str.split("_ ");
		return Arrays.stream(ordersSplited).map(Integer::parseInt).collect(Collectors.toList());
	}
	
	private void readTime(Row currentRow) {
		System.out.println("INNNN");
		times= new ArrayList<>();
		Iterator<Cell> cellIterator = currentRow.iterator();

        int i=0;
        while (cellIterator.hasNext()) {
        	Cell currentCell = cellIterator.next();
        	if(i++==0) {continue;}
            
            if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
            	times.add(currentCell.getNumericCellValue());
            }

        }
	}
	
	protected static List<List<Integer>> getPermutation(int numOfservices) {
		if(permutationList!=null) {
			return permutationList;
		}
		permutationList=new ArrayList<>();
		Set<Integer> servs=IntStream.range(0, numOfservices).boxed().collect(Collectors.toSet());
		permutation(servs, new Stack<Integer>(), numOfservices);
		return permutationList;
		
	}
	private static List<List<Integer>> permutationList;
	/**
	 * returns all possible orders from a list
	 * @param servs
	 * @param permutationStack
	 * @param numOfservices
	 */
	private static void permutation(Set<Integer> servs, Stack<Integer> permutationStack, int numOfservices) {
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
	
	public static void main(String[] args) {
		excelFix ef=new excelFix();
		System.out.println(ef.orders);
		System.out.println(ef.times);
		List<List<Integer>> perms=getPermutation(ef.servNum);
		List<Integer> indexes=new ArrayList<>();
		System.out.println(perms+"\n"+ perms.size()+"\n\n\n");
		ef.orders.stream().forEach(o -> 
			IntStream.range(0, perms.size()).filter(i -> perms.get(i).equals(o)).findAny().ifPresent(s -> indexes.add(s))
		);
		System.out.println(indexes);
		Map<Integer, Double> costNindex=new TreeMap<>();
//		System.out.println(indexes.get(6) + "  --  " + ef.times.get(indexes.get(6)) );
//		indexes.forEach(in -> costNindex.put(in, ef.times.get(in)));
		List<Double> newTimes= new ArrayList<>();
		for(Integer in:indexes) {
			System.out.println(in +"   "+ ef.times.get(in) );
			newTimes.add(ef.times.get(in));
		}
		System.out.println(costNindex.values());
		Write2Excel we = new Write2Excel(filename);
//		we.appendTimes(costNindex.values().stream().collect(Collectors.toList()));
		
		we.appendTimes(newTimes);
		we.finish();
	}
}
