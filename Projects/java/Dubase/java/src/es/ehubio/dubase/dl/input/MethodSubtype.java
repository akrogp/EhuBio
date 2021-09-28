package es.ehubio.dubase.dl.input;

public enum MethodSubtype {
	UNKNOWN("Unknown"),
	LABEL_FREE("Label-free"),
	SILAC("SILAC"),
	ITRAQ("iTRAQ"),
	TMT("TMT");
	
	private MethodSubtype(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	private final String name;
}
