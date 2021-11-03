package es.ehubio.db.pubmed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

// https://www.nlm.nih.gov/bsd/mms/medlineelements.html
public class PubMed {
	public static Paper fillPaper(String pmid) throws IOException, ParseException {
		String path = String.format("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id=%s&retmode=text&rettype=medline", pmid);
		String key = getApiKey();
		if( key != null )
			path = path.concat("&api_key=" + key);
		URL url = new URL(path);
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
	
	/*public static Paper fillPaper(String pmid) throws IOException, ParseException {
		try(BufferedReader br = new BufferedReader(new FileReader("/home/gorka/Descargas/Temp/pubmed.txt"))) {
			return parse(br);
		}
	}*/
	
	public static void waitLimit() throws InterruptedException {
		long limit = getApiKey() == null ? 334 : 100;
		Thread.sleep(limit);
	}

	private static Paper parse(BufferedReader br) throws IOException, ParseException {
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

	private static void addElement(String tag, String text, Paper paper) throws ParseException {
		if( tag.equalsIgnoreCase("TI") )
			paper.setTitle(text);
		else if( tag.equalsIgnoreCase("AB") )
			paper.setAbs(text);
		else if( tag.equalsIgnoreCase("JT") )
			paper.setJournal(text);
		else if( tag.equalsIgnoreCase("DEP") )
			paper.setDate(new SimpleDateFormat("yyyyMMdd").parse(text));
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
	
	private static String getApiKey() {
		if( apiKey == null )
			try(BufferedReader br = new BufferedReader(new InputStreamReader(PubMed.class.getResourceAsStream(KEY_PATH)))) {
				apiKey = br.readLine();
			} catch (Exception e) {
				e.printStackTrace();
			}			
		return apiKey;
	}
	
	private static String apiKey;
	private static final String KEY_PATH = "/keys/ncbi.txt";
}
