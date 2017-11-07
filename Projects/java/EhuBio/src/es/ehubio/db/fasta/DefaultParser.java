package es.ehubio.db.fasta;

public class DefaultParser implements HeaderParser {

	@Override
	public boolean parse(String header) {
		this.header = header;
		int i = header.indexOf(' ');
		if( i == -1 ) {
			name = header;
			description = null;
		} else {
			name = header.substring(0, i);
			description = header.substring(i+1, header.length());
		}
		return true;
	}

	@Override
	public String getAccession() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getProteinName() {
		return null;
	}

	@Override
	public String getGeneName() {
		return null;
	}
	
	@Override
	public String getGeneAccession() {
		return null;
	}

	@Override
	public String getHeader() {
		return header;
	}

	private String header;
	private String name;
	private String description;
}
