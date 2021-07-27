package es.ehubio.db.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import es.ehubio.db.ensembl.Ensembl;
import es.ehubio.db.ensembl.EnsemblIds;
import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;

public class EnsemblTest {

	//@Test
	public void testResolve() {
		EnsemblIds ids = Ensembl.resolveSymbol("P36507");
		assertEquals("ENSG00000126934", ids.getGene());
		assertEquals("ENST00000262948", ids.getTranscripts().get(0));
		assertEquals("ENSP00000262948", ids.getProteins().get(0));
	}
	
	//@Test
	public void testCds() throws InvalidSequenceException {		
		EnsemblIds ids = Ensembl.resolveSymbol("P36507");
		String cds = Ensembl.findCds(ids.getTranscripts().get(0));
		assertEquals("CCDS12120.1", cds);
		Fasta fasta = Ensembl.getCdsFasta(cds);
		assertTrue(fasta.getSequence().contains("CCGTACATCGTGGGCTTCTACGGGGCCTTCTAC"));
	}

}
