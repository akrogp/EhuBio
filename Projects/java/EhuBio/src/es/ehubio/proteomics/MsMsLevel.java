package es.ehubio.proteomics;

public enum MsMsLevel {
	SPECTRUM("spectrum"),
	PSM("PSM"),
	PEPTIDE("peptide"),
	PROTEIN("protein"),
	TRANSCRIPT("transcript"),
	GENE("gene"),
	AMBIGUITYGROUP("group");
	private MsMsLevel( String name ) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	private final String name;
}
