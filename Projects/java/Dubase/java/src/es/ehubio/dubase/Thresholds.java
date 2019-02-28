package es.ehubio.dubase;

public final class Thresholds {
	// Linear
	public static final double FOLD_CHANGE = 2;	
	public static final double P_VALUE = 0.05;
	public static final int MAX_IMPUTATIONS = 1;
	
	// Log
	public static final double LOG2_FOLD_CHANGE = Math.log(FOLD_CHANGE)/Math.log(2);
	public static final double LOG10_P_VALUE = -Math.log10(P_VALUE);
}
