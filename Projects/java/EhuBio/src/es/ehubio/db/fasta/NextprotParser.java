package es.ehubio.db.fasta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NextprotParser implements HeaderParser {
	@Override
	public boolean parse(String header) {
		if( neXtProtPattern == null )
			neXtProtPattern = Pattern.compile("^nxp:(\\S+)");
		Matcher matcher = neXtProtPattern.matcher(header);
		if( !matcher.find() )
			return false;
		
		this.header = header;
		accession = matcher.group(1);
		if( headerPattern == null )
			headerPattern = Pattern.compile("\\\\(\\S+)=([^\\\\]+)");
		matcher = headerPattern.matcher(header);
		while( matcher.find() ) {
			if( matcher.group(1).equalsIgnoreCase("Pname") )
				proteinName = matcher.group(2).trim();
			else if( matcher.group(1).equalsIgnoreCase("Gname") )
				geneName = matcher.group(2).trim();
		}
		return true;
	}

	@Override
	public String getAccession() {
		return accession;
	}
	
	@Override
	public String getDescription() {
		return proteinName;
	}

	@Override
	public String getProteinName() {
		return proteinName;
	}

	@Override
	public String getGeneName() {
		return geneName;
	}
	
	@Override
	public String getHeader() {
		return header;
	}

	private static Pattern neXtProtPattern;
	private static Pattern headerPattern;
	private String accession;
	private String proteinName;
	private String geneName;
	private String header;	
}