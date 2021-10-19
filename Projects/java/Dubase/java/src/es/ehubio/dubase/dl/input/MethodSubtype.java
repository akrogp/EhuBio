package es.ehubio.dubase.dl.input;

public enum MethodSubtype {
	UNKNOWN("Unknown"),
	LABEL_FREE("Label-free"),
	SILAC("SILAC"),
	ITRAQ("iTRAQ"),
	TMT("TMT"),
	MS("MS-based"),
	WESTERN("Western-based");
	
	private MethodSubtype(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	private final String name;
}
