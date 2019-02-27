package es.ehubio.dubase;

public final class Constants {
	// Linear
	public static double FOLD_CHANGE = 2;	
	public static double P_VALUE = 0.05;
	
	// Log
	public static double LOG2_FOLD_CHANGE = Math.log(FOLD_CHANGE)/Math.log(2);
	public static double LOG10_P_VALUE = -Math.log10(P_VALUE);
}
