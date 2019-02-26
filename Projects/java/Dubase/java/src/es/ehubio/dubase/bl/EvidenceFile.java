package es.ehubio.dubase.bl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import es.ehubio.io.CsvReader;

class EvidenceFile {
	
	public static List<EvidenceBean> loadEvidences(String evidencesPath) throws IOException {
		return loadUgoEvidences(evidencesPath);
	}

	private static List<EvidenceBean> loadUgoEvidences(String evidencesPath) throws IOException {
		List<EvidenceBean> evs = new ArrayList<>();
		try( CsvReader csv = new CsvReader("\t", true, false) ) {
			csv.open(evidencesPath);
			
			while( csv.readLine() != null ) {
				EvidenceBean ev = new EvidenceBean();
				ev.getGenes().addAll(Arrays.asList(
						csv.getField(IDX_GENE).split(";")));
				ev.getDescriptions().addAll(Arrays.asList(
						csv.getField(IDX_DESC).split(";")));
				ev.getEvScores().add(new EvScoreBean(
						Score.TOTAL_PEPTS, csv.getIntField(IDX_TOTAL_PEPTS).doubleValue()));
				String uniqPepts = csv.getField(IDX_UNIQ_PEPTS);
				ev.getEvScores().add(new EvScoreBean(Score.UNIQ_PEPTS, Double.parseDouble(uniqPepts)));
				String[] uniqCounts = csv.getField(IDX_UNIQ_COUNTS).split(";");
				int count = 0;
				for( String tmp : uniqCounts )
					if( tmp.equals(uniqPepts) )
						count++;
					else
						break;
				truncate(ev.getGenes(), count);
				truncate(ev.getDescriptions(), count);
				evs.add(ev);
			}
		}
		return evs;
	}
	
	private static void truncate(List<String> genes, int count) {
		Iterator<String> it = genes.iterator();
		while( count-- > 0 )
			it.next();
		while( it.hasNext() ) {
			it.next();
			it.remove();
		}
	}

	private static final int IDX_GENE = 1;
	private static final int IDX_DESC = 2;
	private static final int IDX_TOTAL_PEPTS = 3;
	private static final int IDX_UNIQ_PEPTS = 4;
	private static final int IDX_UNIQ_COUNTS = 9;
}
