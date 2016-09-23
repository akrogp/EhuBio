package es.ehubio.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import es.ehubio.io.CsvReader;

public class NesRevChecker {
	public static class Entry {
		String id;
		int begin, end;
	}
	
	public static void main(String[] args) throws IOException {
		CsvReader csv = new CsvReader("\t", true, false);
		Map<String,Entry> map = new HashMap<>();
		String id;
		csv.open(ORIG);
		while( csv.readLine() != null ) {
			Entry entry = new Entry();
			entry.id = csv.getField("ID");
			fillPosition(entry,csv.getField("Wregex pos"),csv.getField("NESmapper pos"));
			id = csv.getField("Entry").split("\\|")[1];
			map.put(id, entry);
		}
		csv.close();
		csv.open(REV);
		Entry rev = new Entry();
		while( csv.readLine() != null ) {
			fillPosition(rev,csv.getField("Wregex pos (orig)"),csv.getField("NESmapper pos (orig)"));
			id = csv.getField("Entry").replaceAll("rev-", "");
			Entry orig = map.get(id);
			if( orig != null && ((orig.begin>=rev.begin && orig.begin<=rev.end) || (orig.end>=rev.begin&&orig.end<=rev.end)) )
				System.out.println(orig.id);
			else
				System.out.println("X");
		}
		csv.close();
	}
	
	private static void fillPosition(Entry entry, String pos1, String pos2) {
		String[] fields1 = pos1.split("\\.\\.");
		String[] fields2 = pos2.split("\\.\\.");
		entry.begin = 0;
		entry.end = 0;
		if( fields1.length == 2 ) {
			entry.begin = Integer.parseInt(fields1[0]);
			entry.end = Integer.parseInt(fields1[1]);
		}
		if( fields2.length == 2 ) {
			int tmp = Integer.parseInt(fields2[0]);
			entry.begin = entry.begin == 0 ? tmp : Math.min(entry.begin, tmp);
			tmp = Integer.parseInt(fields2[1]);
			entry.end = entry.end == 0 ? tmp : Math.min(entry.end, tmp);
		}
	}

	private static final String ORIG = "/home/gorka/Bio/Proyectos/NES/Estudios/Wregex3.0/Joint/Prediction.csv";
	private static final String REV = "/home/gorka/Bio/Proyectos/NES/Estudios/Wregex3.0/Joint/PredictionRev.csv";
}
