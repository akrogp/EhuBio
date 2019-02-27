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
		final double log2 = Math.log(2);
		List<EvidenceBean> evs = new ArrayList<>();
		try( CsvReader csv = new CsvReader("\t", true, false) ) {
			csv.open(evidencesPath);
			
			while( csv.readLine() != null ) {
				EvidenceBean ev = new EvidenceBean();
				ev.getGenes().addAll(Arrays.asList(
						csv.getField(IDX_GENE).split(";")));
				ev.getDescriptions().addAll(Arrays.asList(
						csv.getField(IDX_DESC).split(";")));
				ev.putScore(Score.TOTAL_PEPTS, csv.getIntField(IDX_TOTAL_PEPTS).doubleValue());
				String uniqPepts = csv.getField(IDX_UNIQ_PEPTS);
				ev.putScore(Score.UNIQ_PEPTS, Double.parseDouble(uniqPepts));
				String[] uniqCounts = csv.getField(IDX_UNIQ_COUNTS).split(";");
				int count = 0;
				for( String tmp : uniqCounts )
					if( tmp.equals(uniqPepts) )
						count++;
					else
						break;
				truncate(ev.getGenes(), count);
				truncate(ev.getDescriptions(), count);
				ev.putScore(Score.MOL_WEIGHT, csv.getDoubleField(IDX_MOL_WEIGHT));
				ev.putScore(Score.SEQ_COVERAGE, csv.getDoubleField(IDX_SEQ_COVER));
				ev.putScore(Score.FOLD_CHANGE, Math.log(csv.getDoubleField(IDX_FOLD_CHANGE))/log2);
				ev.putScore(Score.P_VALUE, -Math.log10(csv.getDoubleField(IDX_P_VALUE)));
				for( int i = 0; i < NUM_REPS; i++ ) {
					ReplicateBean rep = new ReplicateBean();
					rep.putScore(Score.LFQ_INTENSITY, csv.getDoubleField(IDX_LFQ1+i*2), csv.getIntField(IDX_LFQ1+i*2+1) == 1);
					ev.getReplicates().add(rep);
				}
				evs.add(ev);
			}
		}
		return evs;
	}
	
	private static void truncate(List<String> genes, int count) {
		Iterator<String> it = genes.iterator();
		while( count-- > 0 && it.hasNext() )
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
	private static final int IDX_MOL_WEIGHT = 10;
	private static final int IDX_SEQ_COVER = 11;
	private static final int IDX_FOLD_CHANGE = 12;
	private static final int IDX_P_VALUE = 13;
	private static final int IDX_LFQ1 = 14;
	private static final int NUM_REPS = 3;
}
