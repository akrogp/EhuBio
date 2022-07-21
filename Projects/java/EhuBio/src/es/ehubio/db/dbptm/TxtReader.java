package es.ehubio.db.dbptm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public final class TxtReader {
	public static List<Entry> readFile( String basedir ) throws IOException {
		logger.info("Using original data");
		File dir = new File(basedir, "mods");
		List<Entry> entries = new ArrayList<>();
		for( File file : dir.listFiles() )
			entries.addAll(readFile(file));
		return entries;
	}
	
	private static List<Entry> readFile( File file ) throws IOException {		
		logger.info(file.getName());
		List<Entry> entries = new ArrayList<>();
		try(BufferedReader rd = new BufferedReader(new FileReader(file))) {
			String line;
			String[] fields;
			while( (line=rd.readLine()) != null ) {
				fields = line.split(SEP1, -1);
				if( fields.length != 6 )
					throw new IOException("Invalid line: '" + line + "'");
				if( fields[5].length() < 11 )
					continue;
				Entry entry = new Entry();
				entry.setId(fields[0]);
				entry.setAccession(fields[1]);
				entry.setPosition(Integer.parseInt(fields[2]));
				entry.setType(fields[3]);
				entry.setPubmedIds(new HashSet<>(Arrays.asList(fields[4].split(SEP2))));
				entry.setAminoacid(fields[5].charAt(10));				
				entries.add(entry);
			}
		}
		return entries;
	}
	
	private static final String SEP1 = "\t";
	private static final String SEP2 = ";";
	private static final Logger logger = Logger.getLogger(TxtReader.class.getName());
}
