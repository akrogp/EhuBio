package es.ehubio.db.pubmed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// https://www.nlm.nih.gov/bsd/mms/medlineelements.html
public class PubMed {
	public static Paper fillPaper(String pmid) throws IOException {
		URL url = new URL(String.format("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id=%s&retmode=text&rettype=medline", pmid));
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection)url.openConnection();
			try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				return parse(br);
			}
		} finally {
			if( conn != null )
				conn.disconnect();
		}
	}

	private static Paper parse(BufferedReader br) throws IOException {
		Paper paper = new Paper();
		String line, tag, current = null;
		StringBuilder text = null;
		while( (line = br.readLine()) != null ) {
			if( line.isEmpty() )
				continue;
			tag = line.substring(0, 6).replace("-", "").trim();
			if( current == null ) {
				current = tag;
				text = new StringBuilder(line.substring(6));
			} else {
				if( tag.isEmpty() )
					text.append(line.substring(6));
				else {
					addElement(current, text.toString(), paper);
					current = tag;
					text = new StringBuilder(line.substring(6));
				}
			}
		}
		addElement(current, text.toString(), paper);
		return paper;
	}

	private static void addElement(String tag, String text, Paper paper) {
		if( tag.equalsIgnoreCase("TI") )
			paper.setTitle(text);
		else if( tag.equalsIgnoreCase("AB") )
			paper.setAbs(text);
		else if( tag.equalsIgnoreCase("JT") )
			paper.setJournal(text);
		else if( tag.equalsIgnoreCase("DEP") )
			paper.setDate(text);
		else if( tag.equalsIgnoreCase("FAU") ) {
			Author author = new Author();
			author.setFullName(text);
			paper.getAuthors().add(author);
		} else if( tag.equalsIgnoreCase("AU") )
			paper.getLastAuthor().setAbbrvName(text);
		else if( tag.equalsIgnoreCase("AD") ) {
			paper.getLastAuthor().getAffiliations().add(text);
			if( text.contains("@") ) {
				String[] fields = text.split("\\. ");
				String email = fields[fields.length-1];
				email = email.substring(0, email.length()-1);
				paper.getLastAuthor().setEmail(email);
			}
		}
	}
}
