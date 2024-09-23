package panalyzer.model;

public enum ProteinType {
	CONCLUSIVE("conclusive"),
	INDISTINGUISHABLE("indistinguishable"),
	AMIBIGUOUS("ambiguous"),
	NON_CONCLUSIVE("non-conclusive");

	public final String label;
	
	private ProteinType(String label) {
		this.label = label;
	}
}
