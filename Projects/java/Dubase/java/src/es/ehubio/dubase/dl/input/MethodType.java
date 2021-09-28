package es.ehubio.dubase.dl.input;

public enum MethodType {
	UNKNOWN("Unknown"),
	MANUAL("Manual"),
	PROTEOMICS("Proteomics"),
	PEPTIDOMICS("Peptidomics"),
	UBIQUITOMICS("Ubiquitomics");
	
	private MethodType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	private final String name;
}
