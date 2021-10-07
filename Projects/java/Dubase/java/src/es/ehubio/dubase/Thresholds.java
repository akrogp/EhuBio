package es.ehubio.dubase;

public final class Thresholds {
	// Linear
	public static final double FOLD_CHANGE = 2;	
	public static final double P_VALUE = 0.05;
	public static final int MAX_IMPUTATIONS = 1;
	public static final int MIN_PEPTIDES = 2;
	
	// Log
	public static final double LOG2_FOLD_CHANGE = Math.log(FOLD_CHANGE)/Math.log(2);
	public static final double LOG10_P_VALUE = -Math.log10(P_VALUE);
	
	// Instance
	private double foldChange = FOLD_CHANGE, log2FoldChange = LOG2_FOLD_CHANGE;
	private double pValue = P_VALUE, log10PValue = LOG10_P_VALUE;
	private boolean up = true;
	private boolean down = false;
	private int minPeptides = MIN_PEPTIDES;
	
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
		this.foldChange = Math.pow(2, log2FoldChange);
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
	public boolean isUp() {
		return up;
	}
	public void setUp(boolean up) {
		this.up = up;
	}
	public boolean isDown() {
		return down;
	}
	public void setDown(boolean down) {
		this.down = down;
	}
	public int getMinPeptides() {
		return minPeptides;
	}
	public void setMinPeptides(int minPeptides) {
		this.minPeptides = minPeptides;
	}
}
