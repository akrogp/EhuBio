package es.ehubio.db.dbptm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ehubio.io.CsvUtils;

public final class TxtReader {
	public static List<Entry> readFile( String report ) throws IOException {
		File cache = new File(report+cacheSuffix);
		File org = new File(report);		
		List<Entry> entries = null;
		boolean update = true;
		
		if( !cache.exists() || cache.lastModified() < org.lastModified() )
			entries = readReport(report);
		else {
			try {
				entries = readCache(cache.getAbsolutePath());
				update = false;
			} catch( Exception e ) {
				entries = readReport(report);
			}
		}
		
		if( update ) {
			try {
				writeCache(cache.getAbsolutePath(), entries);
			} catch( Exception e ) {				
			}
		}
		
		return entries;
	}
	
	private static List<Entry> readReport( String report ) throws IOException {
		logger.info("Using original data");
		List<Entry> entries = new ArrayList<>();
		BufferedReader rd = new BufferedReader(new FileReader(report));
		String line;
		Pattern pattern = Pattern.compile("(^.*)[ \\t]+(.*)[ \\t]+(\\d+)[ \\t]+(.*)[ \\t]+([\\d;N\\.\\?,-]+)[ \\t]+(.*)[ \\t]+(.)[ \\t]+(.*$)", Pattern.CASE_INSENSITIVE);
		Matcher matcher;
		while( (line=rd.readLine()) != null ) {
			matcher = pattern.matcher(line);
			if( !matcher.find() || matcher.groupCount() != 8 ) {
				rd.close();
				throw new IOException("Invalid line: '" + line + "'");
			}
			Entry entry = new Entry();
			entry.setId(matcher.group(1));
			entry.setAccession(matcher.group(2));
			entry.setPosition(Integer.parseInt(matcher.group(3)));			
			entry.setModification(matcher.group(4));
			entry.setPubmedIds(new HashSet<>(Arrays.asList(matcher.group(5).split(";"))));
			entry.setResource(matcher.group(6));
			entry.setAminoacid(matcher.group(7).charAt(0));
			entry.setType(matcher.group(8));
			entries.add(entry);
		}
		rd.close();
		return entries;
	}
	
	private static void writeCache( String cache, List<Entry> entries ) throws IOException {
		logger.info("Writing data to a cache file");
		PrintWriter pw = new PrintWriter(cache);
		for( Entry entry : entries ) {
			pw.println(CsvUtils.getCsv(cacheSeparator,
				entry.getId(),
				entry.getAccession(),
				entry.getPosition(),
				entry.getModification(),
				CsvUtils.getCsv(cacheSubSeparator, entry.getPubmedIds().toArray()),
				entry.getResource(),
				entry.getAminoacid(),
				entry.getType()
			));
		}
		pw.close();
	}
	
	private static List<Entry> readCache( String cache ) throws IOException {
		logger.info("Using cached data");
		List<Entry> entries = new ArrayList<>();
		BufferedReader rd = new BufferedReader(new FileReader(cache));
		String line;
		String[] fields;
		Entry entry;
		while( (line=rd.readLine()) != null ) {
			entry = new Entry();
			fields = line.split(""+cacheSeparator);
			entry.setId(fields[0]);
			entry.setAccession(fields[1]);
			entry.setPosition(Integer.parseInt(fields[2]));
			entry.setModification(fields[3]);
			entry.setPubmedIds(new HashSet<>(Arrays.asList(fields[4].split(cacheSubSeparator+""))));
			entry.setResource(fields[5]);
			entry.setAminoacid(fields[6].charAt(0));
			entry.setType(fields[7]);
			entries.add(entry);
		}
		rd.close();
		return entries;
	}
	
	private static final char cacheSeparator = ':';
	private static final char cacheSubSeparator = ';';
	private static final String cacheSuffix = ".cache";
	private static final Logger logger = Logger.getLogger(TxtReader.class.getName());
}
