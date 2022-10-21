package es.ehubio.db.psp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class PspFile {
	private static final String SITE_SUFFIX = "_site_dataset.gz";

	public static List<Site> readDir(String path) throws FileNotFoundException, IOException {
		File dir = new File(path);
		if( !dir.isDirectory() )
			return readFile(dir);
		List<Site> list = new ArrayList<>();
		for( File file : dir.listFiles() )
			if( file.isFile() && file.getName().endsWith(SITE_SUFFIX) )
				list.addAll(readFile(file));
		return list;
	}
	
	public static List<Site> readFile(File file) throws FileNotFoundException, IOException {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))))) {
			String type = file.getName().replace(SITE_SUFFIX, "");
			List<Site> list = new ArrayList<>();
			String line;
			String[] fields;
			while( (line = br.readLine()) != null && !line.startsWith("GENE"));
			while( (line = br.readLine()) != null ) {
				fields = line.split("\t");
				Site site = new Site();
				site.setType(type);
				site.setGene(fields[0]);
				site.setProtein(fields[1]);
				site.setAccession(fields[2]);
				site.setRsd(fields[4]);
				site.setOrganism(fields[6]);
				site.setSequence(fields[9]);
				list.add(site);
			}
			return list;
		}
	}
}
