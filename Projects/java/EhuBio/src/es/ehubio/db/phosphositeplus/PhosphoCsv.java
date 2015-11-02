package es.ehubio.db.phosphositeplus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import es.ehubio.model.ProteinModificationType;

public class PhosphoCsv {
	public static List<PhosphoEntry> loadAll( String gzPath ) throws FileNotFoundException, IOException {
		return loadByOrganism(gzPath, null);
	}
	
	public static List<PhosphoEntry> loadByOrganism( String gzPath, String org ) throws FileNotFoundException, IOException {
		List<PhosphoEntry> list = new ArrayList<>();
		BufferedReader rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(gzPath))));
		String line;
		String[] fields;
		boolean header = true;
		while( (line=rd.readLine()) != null ) {
			if( header ) {
				if( line.startsWith("PROT") )
					header = false;
				continue;
			}
			fields = line.split("\\t");
			PhosphoEntry entry = new PhosphoEntry();
			entry.setProtein(fields[0]);
			entry.setAccession(fields[1]);
			entry.setGene(fields[2]);
			entry.setChrLocation(fields[3]);
			entry.setName(fields[4]);
			if( entry.getName().startsWith("PHOSPHO") )
				entry.setType(ProteinModificationType.PHOSPHORYLATION);
			entry.setResidues(fields[5].charAt(0)+"");
			entry.setPosition(Integer.parseInt(fields[5].substring(1)));
			entry.setGrpId(fields[6]);
			entry.setOrg(fields[7]);
			list.add(entry);
		}
		rd.close();
		return list;
	}
}
