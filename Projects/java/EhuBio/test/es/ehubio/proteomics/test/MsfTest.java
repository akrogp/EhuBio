package es.ehubio.proteomics.test;

import static org.junit.Assert.assertEquals;

import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.io.MsMsFile;
import es.ehubio.proteomics.io.ProteomeDiscovererMsf;
import es.ehubio.proteomics.pipeline.Filter;

public class MsfTest {
	//@Test
	public void test() throws Exception {
		//MsMsData data = MsMsFile.autoLoad("/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Sequest/ProteomeDiscoverer/PD14.msf",false);
		//MsMsData data = MsMsFile.autoLoad("/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Sequest/ProteomeDiscoverer/PME10/PME10_120MIN_MSMS_141106_001.msf",true);
		//MsMsData data = MsMsFile.autoLoad("/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Sequest/ProteomeDiscoverer/PME10/PME10_120MIN_MSMS_141106_001.msf",false);
		MsMsData data = MsMsFile.autoLoad("/home/gorka/Descargas/OscarTarget.msf",false);
		Filter filter = new Filter(data);
		filter.setPeptideScoreThreshold(new Score(ScoreType.PEPTIDE_MSF_CONFIDENCE, ProteomeDiscovererMsf.PeptideConfidenceLevel.HIGH.getLevel()));		
		filter.run();
		assertEquals(data.getProteins().size(), 22);
	}
}
