package es.ehubio.db.fasta;

public interface HeaderParser {
	public boolean parse( String header );
	public String getAccession();
	public String getDescription();
	public String getProteinName();
	public String getGeneName();
	public String getGeneAccession();
	public String getHeader();
}
