package panalyzer.model;

public enum PeptideType {
	UNIQUE("unique"),
	DISCRIMINATING("discriminating"),
	NON_DISCRIMINATING("non-discriminating");

	public final String label;
	
	private PeptideType(String label) {
		this.label = label;
	}
}
