package es.ehubio.db.gencode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.io.CsvReader;

public class Gencode {
	public static List<Feature> readGtf(String path) throws IOException {
		List<Feature> list = new ArrayList<>();
		CsvReader csv = new CsvReader("\t",true);
		csv.open(path);
		while( csv.readLine() != null ) {
			Feature feat = readFeature(csv);
			list.add(feat);
		}
		csv.close();
		return list;
	}
	
	public static Map<String, Feature> mapGtf(String path, String type, String key, boolean skipVersion) throws IOException {
		Map<String, Feature> map = new HashMap<>();
		CsvReader csv = new CsvReader("\t",true);
		csv.open(path);
		while( csv.readLine() != null ) {
			Feature feat = readFeature(csv);
			if( !type.equalsIgnoreCase(feat.getType()) )
				continue;
			String value = feat.getInfo().get(key);
			if( value == null )
				continue;
			if( skipVersion )
				value = value.replaceAll("\\..*", "");
			map.put(value, feat);
		}
		csv.close();
		return map;
	}
		
	private static Feature readFeature(CsvReader csv) {
		Feature feat = new Feature();
		feat.setChromosome(csv.getField(0));
		feat.setSource(csv.getField(1));
		feat.setType(csv.getField(2));
		feat.setStart(csv.getIntField(3));
		feat.setEnd(csv.getIntField(4));
		feat.setScore(csv.getField(5));
		feat.setStrand(csv.getField(6));
		feat.setPhase(csv.getField(7));
		
		String[] fields = csv.getField(8).split(";[ ]");
		for( String field : fields ) {
			String[] kv = field.split(" ");
			feat.getInfo().put(kv[0], kv[1].replaceAll("\"", ""));
		}
		
		return feat;
	}
}
