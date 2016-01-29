package es.ehubio.proteomics.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.io.MsMsFile;
import es.ehubio.proteomics.pipeline.ConfigDetector;
import es.ehubio.proteomics.pipeline.Digester;
import es.ehubio.proteomics.pipeline.Searcher;

public class CfgDetectorTest {

	@Test
	public void test() throws Exception {
		MsMsData data = MsMsFile.autoLoad(DATA, false);
		data.updateProteinInformation(FASTA);
		ConfigDetector detector = new ConfigDetector(MAX);
		Digester.Config digestConfig = null;
		Searcher.Config searchConfig = null;
		try {
			digestConfig = detector.getDigestion(data);
			searchConfig = detector.getSearching(data);
		} catch( Exception e ) {
			e.printStackTrace();
		}
		assertNotNull(digestConfig);
		assertNotNull(searchConfig);
	}

	private static final String DATA = "/home/gorka/Descargas/Temp/Oscar/OscarTarget.msf";
	private static final String FASTA = "/home/gorka/Descargas/Temp/Oscar/uniprot-brachyspira_totaldb_151122.fasta.gz";
	private static final int MAX = 1000;
}
