package es.ehubio.proteomics.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.io.EhubioCsv;
import es.ehubio.proteomics.io.MsMsFile;

public class DatTest {

	@Test
	public void test() throws Exception {
		MsMsData data = MsMsFile.autoLoad(TEST_PATH,false);		
		EhubioCsv csv = new EhubioCsv(data);
		csv.setPsmScoreType(ScoreType.MASCOT_SCORE);
		csv.save(OUT_PATH);
		assertEquals(11077, data.getProteinCount());
	}

	private static final String TEST_PATH="/home/gorka/Descargas/Temp/CIMA/CBM-MCF7/F228518.dat";
	private static final String OUT_PATH="/home/gorka/Descargas/Temp/CIMA/EHU_CBM-MCF7_F228518";
	
	/*private static final String TEST_PATH="/home/gorka/Descargas/Temp/CIMA/CBM-MCF7-XT/SPHPP_CBM__MCF7_OTV_GEL_R1_1_4.2016_01_21_15_48_37.t.xml";
	private static final String OUT_PATH="/home/gorka/Descargas/Temp/CIMA/EHU_CBM-MCF7-XT";*/
}
