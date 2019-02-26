package es.ehubio.dubase.bl;

import java.io.IOException;
import java.util.ArrayList;
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
				for( String gene : csv.getField(IDX_GENE).split(";") ) {
					
				}
				evs.add(ev);
			}
		}
		return evs;
	}
	
	private static final int IDX_GENE = 1;
}
