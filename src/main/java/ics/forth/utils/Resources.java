package ics.forth.utils;
/**
 * 
 * @author Thanos Yannakis (yannakis@ics.forth.gr)
 *
 */
public class Resources {
	public static final String INPUT_FILE="query.txt";
	public static final String PREFIX="prefix";
	public static final String USE_FORMULA="5"; // 4,5,6,7,none
	
	//FORMULA 6+ WEIGHTS
	public static final double W_S=0.243; // weight for subject as depicted in Formula 6
	public static final double W_P=0.486; // weight for predicate as depicted in Formula 6
	public static final double W_O=0.271; // weight for object as depicted in Formula 6
	
	//FORMULA 7 JOIN WEIGHTS
	public static final double J_Tc=0.55; // weight for chain join as depicted in Formula 7
	public static final double J_Ts=0.75; // weight for star join as depicted in Formula 7
	public static final double J_Tu=1; // weight for unusual join as depicted in Formula 7
	public static final double J_Tn=0; // no join in Formula 7
	
	//EXCEL
	public static final String OUTPUT_TESTS_EXCEL="test_results.xlsx";
	public static final int SHEET_INDEX=0;
	public static final String COL_NAME_TIME="Time(ms)";
	public static final String COL_NAME_COST="Cost";
	public static final String COL_NAME_ORDERS="Service Orders";
	
	//OPTIONS
	public static final int TEST_REPETITIONS=1;
	public static final boolean NO_OPTIMIZATIONS=false;
	
	public static final boolean EXPORT2EXCEL=true; 
	
	public static final boolean COUNT_COST=true;
	public static final boolean USE_FORMULA_4=true; 
	public static final boolean USE_FORMULA_5=true; 
	public static final boolean USE_FORMULA_6=true; 
	
	public static final boolean COUNT_TIME=true; 
	
	//DEBUGGING
	public static int SERVICES_RUN=0;
	public static boolean ENABLE_REMOVER=false;
	public static int REMOVE_ORDER=2;
}
