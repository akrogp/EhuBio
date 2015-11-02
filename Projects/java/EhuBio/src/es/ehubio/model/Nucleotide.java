package es.ehubio.model;

public enum Nucleotide {
	ADENINE("Adenine",'A',false,true,true,true),
	CYTOSINE("Cytosine",'C',true,false,true,true),
	GUANINE("Guanine",'G',false,true,true,true),
	THYMINE("Thymine",'T',true,false,true,false),
	URACIL("Uracil",'U',true,false,false,true);

	public final String name;
	public final char symbol;
	public final boolean isPyrimidine;
	public final boolean isPurine;
	public final boolean isDNA;
	public final boolean isRNA;
	
	Nucleotide(String name, char symbol,
		boolean isPyrimidine, boolean isPurine, boolean isDNA, boolean isRNA ) {
		this.name = name;
		this.symbol = symbol;
		this.isPyrimidine = isPyrimidine;
		this.isPurine = isPurine;
		this.isDNA = isDNA;
		this.isRNA = isRNA;
	}
}
