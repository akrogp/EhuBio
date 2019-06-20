package es.ehubio.dubase.dl.input;

// Important: keep same order than db so that ordinal matches DB id
public enum FileType {
	UNKNOWN("Unknown type"),
	UGO_CSV("Internal CSV format of Ugo's lab"),
	FASTA("Fasta database used for MS/MS search");
	
	private FileType(String description) {
		this.description = description;
	}
	
	public final String description;
}