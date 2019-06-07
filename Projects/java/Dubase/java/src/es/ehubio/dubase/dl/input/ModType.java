package es.ehubio.dubase.dl.input;

/**
 * Order of elements must be coherent with the DB!
 */
public enum ModType {
	UNKNOWN("Unknown"),
	GLYGLY("GlyGly (K)");
	
	private ModType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	private final String name;
}
