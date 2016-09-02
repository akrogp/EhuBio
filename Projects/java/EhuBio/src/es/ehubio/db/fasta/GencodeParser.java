package es.ehubio.db.fasta;

public class GencodeParser implements HeaderParser {

	@Override
	public boolean parse(String header) {
		String[] fields = header.split("\\|");
		if( fields.length < 8 )
			return false;
		if( !fields[0].startsWith("ENSP") )
			return false;
		accession = fields[0];
		if( !fields[1].startsWith("ENST") )
			return false;
		if( !fields[2].startsWith("ENSG") )
			return false;
		name = fields[5];	// Transcript name
		geneName = fields[6];
		this.header = header;
		description = "";
		return true;
	}

	@Override
	public String getAccession() {
		return accession;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getProteinName() {
		return name;
	}

	@Override
	public String getGeneName() {
		return geneName;
	}
	
	@Override
	public String getHeader() {		
		return header;
	}

	private String accession;
	private String name;
	private String description;
	private String geneName;
	private String header;
}
