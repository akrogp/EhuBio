package es.ehubio.dubase.dl.input;

/**
 * Order of elements must be coherent with the DB!
 */
public enum ScoreType {
	UNKOWN("unknown"),
	FOLD_CHANGE("fold change"),
	P_VALUE("p-value"),
	TOTAL_PEPTS("total peptides"),
	UNIQ_PEPTS("unique peptides"),
	MOL_WEIGHT("molecular weight"),
	SEQ_COVERAGE("sequence coverage"),
	LFQ_INTENSITY("LFQ intensity");
	
	private ScoreType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	private final String name;
}
