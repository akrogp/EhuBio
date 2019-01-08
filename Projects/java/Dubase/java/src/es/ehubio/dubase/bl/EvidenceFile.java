package es.ehubio.dubase.bl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.ehubio.io.CsvReader;

class EvidenceFile {

	public static List<EvidenceBean> loadEvidences(String evidencesPath) throws IOException {
		List<EvidenceBean> evs = new ArrayList<>();
		try( CsvReader csv = new CsvReader("\t", true, false) ) {
			csv.open(evidencesPath);
			while( csv.readLine() != null ) {
				EvidenceBean ev = new EvidenceBean();
				ev.setGene(csv.getField(0));
				ev.setFoldChange(csv.getDoubleField(1));
				ev.setPValue(csv.getDoubleField(2));
				evs.add(ev);
			}
		}
		return evs;
	}
	
}
