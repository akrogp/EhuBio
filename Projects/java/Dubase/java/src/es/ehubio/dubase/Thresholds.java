package es.ehubio.dubase;

public final class Thresholds {
	// Linear
	public static final double FOLD_CHANGE = 2;	
	public static final double P_VALUE = 0.05;
	public static final int MAX_IMPUTATIONS = 1;
	
	// Log
	public static final double LOG2_FOLD_CHANGE = Math.log(FOLD_CHANGE)/Math.log(2);
	public static final double LOG10_P_VALUE = -Math.log10(P_VALUE);
	
	// Instance
	private double foldChange = FOLD_CHANGE, log2FoldChange = LOG2_FOLD_CHANGE;
	private double pValue = P_VALUE, log10PValue = LOG10_P_VALUE;
	
	public double getFoldChange() {
		return foldChange;
	}
	public void setFoldChange(double foldChange) {
		this.foldChange = foldChange;
		this.log2FoldChange = Math.log(foldChange)/Math.log(2);
	}
	
	public double getLog2FoldChange() {
		return log2FoldChange;
	}
	public void setLog2FoldChange(double log2FoldChange) {
		this.log2FoldChange = log2FoldChange;
		this.foldChange = Math.pow(2, foldChange);
	}
	
	public double getpValue() {
		return pValue;
	}
	public void setpValue(double pValue) {
		this.pValue = pValue;
		this.log10PValue = -Math.log10(pValue);
	}
	
	public double getLog10PValue() {
		return log10PValue;
	}
	public void setLog10PValue(double log10pValue) {
		log10PValue = log10pValue;
		this.pValue = Math.pow(10, -log10pValue);
	}
}
