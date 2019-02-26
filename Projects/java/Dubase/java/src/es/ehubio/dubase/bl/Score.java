package es.ehubio.dubase.bl;

/**
 * Order of elements must be coherent with the DB!
 */
public enum Score {
	UNKOWN("unknown"),
	FOLD_CHANGE("fold change"),
	P_VALUE("p-value"),
	TOTAL_PEPTS("total peptides"),
	UNIQ_PEPTS("unique peptides"),
	MOL_WEIGHT("molecular weight"),
	SEQ_COVERAGE("sequence coverage"),
	LFQ_INTENSITY("LFQ intensity");
	
	private Score(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	private final String name;
}
