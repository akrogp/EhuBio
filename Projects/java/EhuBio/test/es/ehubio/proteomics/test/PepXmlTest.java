package es.ehubio.proteomics.test;

import static org.junit.Assert.assertNotEquals;

import java.util.Collection;

import org.junit.Test;

import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.io.MsMsFile;

public class PepXmlTest {

	@Test
	public void test() throws Exception {
		/*MsMsData data = MsMsFile.autoLoad("/home/gorka/Descargas/Temp/Adult_Adrenalgland_bRP_Velos_1_f01.pep.xml", false);
		assertEquals(2181, data.getSpectraCount());
		assertEquals(10098, data.getPsmCount());*/
		MsMsData data = MsMsFile.autoLoad("/media/gorka/EhuBio/Search/Adult_Adrenalgland/Comet/target", false);
		ScoreType type = selectScore(data.getPsms());
		assertNotEquals(null, type);
	}

	private static ScoreType selectScore( Collection<? extends Decoyable> items, ScoreType... scores ) {
		if( items.isEmpty() )
			return null;
		if( scores.length == 0 )
			scores = SCORES;
		Decoyable item = items.iterator().next();
		for( ScoreType type : scores )
			if( item.getScoreByType(type) != null )
				return type;
		return item.getScores().iterator().next().getType();
	}
	
	private static final ScoreType[] SCORES = {
			ScoreType.SEQUEST_XCORR, ScoreType.MASCOT_SCORE, ScoreType.XTANDEM_EVALUE,
			ScoreType.LP_SCORE, ScoreType.LPCORR_SCORE, ScoreType.N_EVALUE, ScoreType.N_OVALUE, ScoreType.M_EVALUE, ScoreType.M_OVALUE,
			ScoreType.OTHER_LARGER, ScoreType.OTHER_SMALLER};  
}
