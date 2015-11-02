package es.ehubio.proteomics.test;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.io.MsMsFile;
import es.ehubio.proteomics.pipeline.Filter;

public class PlgsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() throws Exception {
		MsMsData data;
		data = MsMsFile.autoLoad(path24, false);
		assertNotNull(data);
		data = MsMsFile.autoLoad(path25, false);
		assertNotNull(data);
		
		Filter filter = new Filter(data);
		filter.setMinPeptideLength(7);
		filter.setPeptideScoreThreshold(new Score(ScoreType.PSM_PLGS_COLOR, 2));
		filter.run();
		
		//assertEquals(1560, data.getPeptides().size());
		assertEquals(179, data.getProteins().size());
	}

	private static final String path25 = "/home/gorka/MyProjects/EhuBio/Projects/csharp/Apps/PAnalyzer/examples/PLGS2.5/newFNR_04_1_IA_workflow.xml";
	private static final String path24 = "/home/gorka/MyProjects/EhuBio/Projects/csharp/Apps/PAnalyzer/examples/PLGS2.4/PROTEORED_1_101008_003_IA_workflow.xml";
}
