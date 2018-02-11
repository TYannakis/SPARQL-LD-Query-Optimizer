package ics.forth.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Write2Excel {
	private FileInputStream excelFile;
	private XSSFWorkbook workbook;
	private XSSFSheet datatypeSheet;
	private int numRows=0;
	private int numCols=0;
	private String queryName;
	private List<Integer> curOrder;
	public Write2Excel(String name) {
		queryName=name;
		File f=new File(Resources.OUTPUT_TESTS_EXCEL);
		try {
			if(!f.exists()) {
				 workbook = new XSSFWorkbook();
				 datatypeSheet = workbook.createSheet("Formulas");
			}else {
					excelFile = new FileInputStream(new File(Resources.OUTPUT_TESTS_EXCEL));
					workbook = new XSSFWorkbook(excelFile);
					datatypeSheet = workbook.getSheetAt(Resources.SHEET_INDEX);
					numRows = datatypeSheet.getPhysicalNumberOfRows()+1;
			}
			//add query name
			createRowName(datatypeSheet.createRow(numRows++),queryName,getQueryStyle());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	/**
	 * Write the order and cost results to excel
	 * @param costs
	 * @param orders
	 * @throws InvalidFormatException 
	 */
	public void append(String formulaName,List<Double> costs, List<List<Integer>> orders)  {
		Row row1 = datatypeSheet.createRow(numRows++);
		//add Query and Formula number 
		createRowName(row1,formulaName);
		row1 = datatypeSheet.createRow(numRows++);
		Row row2 = datatypeSheet.createRow(numRows++);
		//get our results ordered
		Map<String,Double> ordersNcosts=getOrderedIndexes(costs,  orders);
		//Start writing to new rows	
		//add names
		createRowName(row1,Resources.COL_NAME_ORDERS);
		createRowName(row2,Resources.COL_NAME_COST);
		int colNum=1;
		for (String order : ordersNcosts.keySet()) {
			Cell cell = row1.createCell(colNum);	
			cell.setCellValue((String)order);
			cell.setCellStyle(getStyle());
			
			cell = row2.createCell(colNum++);
			cell.setCellValue((Double)ordersNcosts.get(order));
			cell.setCellStyle(getStyle());
		}
		checkNumOfCols(orders.size());
	}
	/**
	 * Adds average execution times
	 * @param times
	 */
	public void appendTimes(List<Double> times) {
		Row row = datatypeSheet.createRow(numRows++);
		createRowName(row,Resources.COL_NAME_TIME);
		AtomicInteger colNum=new AtomicInteger(1);
		if(curOrder!=null) {
			//add each average time for every order
			curOrder.forEach(order -> {
				Cell cell =row.createCell(colNum.getAndIncrement());
				cell.setCellValue((Double)times.get(order));	
			});
		}else {
			times.forEach(time -> {
				Cell cell =row.createCell(colNum.getAndIncrement());
				cell.setCellValue((Double)time);	
			});
		}
		
		
		checkNumOfCols(times.size());
		
	}
	/**
	 * find maximum used columns in sheet
	 * @param numCols
	 */
	private void checkNumOfCols(int numCols) {
		if(this.numCols<numCols)
			this.numCols=numCols;
	}
	private void createRowName(Row row, String name ) {
		int colNum=0;
		Cell cell =row.createCell(colNum);
		cell.setCellValue((String)name);	
		cell.setCellStyle(getBoldStyle());
	}
	private void createRowName(Row row, String name, XSSFCellStyle style ) {
		int colNum=0;
		Cell cell =row.createCell(colNum);
		cell.setCellValue((String)name);	
		cell.setCellStyle(style);
	}
	/**
	 * @return bold style with bottom right border
	 */
	private XSSFCellStyle getBoldStyle() {
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);  
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		return style;
	}
	/**
	 * @return style with bottom right border
	 */
	private XSSFCellStyle getStyle() {
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		style.setFont(font); 
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		return style;
	}
	private XSSFCellStyle getQueryStyle() {
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.BLUE.index);
		font.setBold(true);
		style.setFont(font); 
		style.setAlignment(CellStyle.ALIGN_LEFT);
		return style;
	}
//	private void addFormulaNquery(String formulaName, Row row1) {
//		Cell cell = row1.createCell(0);
//		cell.setCellValue((String)formulaName);
//		
//	}
	/**
	 * autosize columns, close and then write to it
	 */
	public void finish()  {
		try {
			autosize();
			if(excelFile!=null) {
				excelFile.close();
			}
			FileOutputStream outputStream = new FileOutputStream(Resources.OUTPUT_TESTS_EXCEL);
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * AutosizeColumns
	 */
	private void autosize() {
		IntStream.range(0, this.numCols).forEach(i -> datatypeSheet.autoSizeColumn(i));
	}
	/**
	 * Get orders sorted depending on costs (from lowest to highest)
	 * @param costs
	 * @param orders
	 * @return
	 */
	public Map<String,Double> getOrderedIndexes(List<Double> costs, List<List<Integer>> orders) {
		List<Integer> index=IntStream.range(0, orders.size()).boxed().collect(Collectors.toList());
		Map<String,Double> ordersNcosts= new TreeMap<>();
		Map<Integer,Double> costsNindexes= new TreeMap<>();
		for (int i = 0; i < orders.size(); i++) {
			ordersNcosts.put(orders.get(i).toString().replaceAll(",", "_").trim(), costs.get(i));
			costsNindexes.put(index.get(i), costs.get(i));
		}
		curOrder=costsNindexes.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.map(x -> x.getKey())
				.collect(Collectors.toList());
		return ordersNcosts.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, 
	                      (e1, e2) -> e1, LinkedHashMap::new));
	}
}
