package es.ehubio.proteomics.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import es.ehubio.proteomics.Enzyme;
import es.ehubio.proteomics.pipeline.Digester;

public class Digestion {
	//private static final Logger logger = Logger.getLogger(Digestion.class.getName());
	
	@Test
	public void testTrypsine() {
		String seq = "ASDFKFDSARQWERPQWE";
		String[] list = Digester.digestSequence(seq, Enzyme.TRYPSIN);
		/*logger.info("Sequence: "+seq);
		for( String pep : list )
			logger.info("Peptide: "+pep);*/
		assertEquals(3, list.length);
		assertTrue(list[2].equalsIgnoreCase("QWERPQWE"));
	}

	@Test
	public void testTrypsineP() {
		String seq = "ASDFKFDSARQWERPQWE";
		String[] list = Digester.digestSequence(seq, Enzyme.TRYPSINP);
		/*logger.info("Sequence: "+seq);
		for( String pep : list )
			logger.info("Peptide: "+pep);*/
		assertEquals(4, list.length);
		assertTrue(list[3].equalsIgnoreCase("PQWE"));
	}
	
	@Test
	public void testProtein() {
		String seq = "MGKVKVGVNGFGRIGRLVTRAAFNSGKVDIVAINDPFIDLNYMVYMFQYDSTHGKFHGTVKAENGKLVINGNPITIFQERDPSKIKWGDAGAEYVVESTGVFTTMEKAGAHLQGGAKRVIISAPSADAPMFVMGVNHEKYDNSLKIISNASCTTNCLAPLAKVIHDNFGIVEGLMTTVHAITATQKTVDGPSGKLWRDGRGALQNIIPASTGAAKAVGKVIPELNGKLTGMAFRVPTANVSVVDLTCRLEKPAKYDDIKKVVKQASEGPLKGILGYTEHQVVSSDFNSDTHSSTFDAGAGIALNDHFVKLISWYDNEFGYSNRVVDLMAHMASKE";
		Set<String> list = Digester.digestSequence(seq, Enzyme.TRYPSIN, 2);
		/*Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int dif = o2.length()-o1.length();
				if( dif != 0 )
					return dif;
				return o1.compareTo(o2);
			}
		});
		for( String pep : list )
			logger.info("Peptide: "+pep);*/
		assertEquals(105, list.size());
	}
	
	@Test
	public void testFull() {
		String seq = "MASDFKFDSARIIIDPLLL";
		Set<String> list = Digester.digestSequence(seq, new Digester.Config(Enzyme.TRYPSINP, 1, true, 2));
		/*for( String pep : list )
			logger.info("Peptide: "+pep);*/
		assertEquals(12, list.size());
	}
	
	/*@Test
	public void testDb() throws IOException, InvalidSequenceException {
		PrintWriter pw = new PrintWriter("/home/gorka/iakes.csv");
		List<Fasta> list = Fasta.readEntries("/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/Gencode20cds_TD_Jul14_TARGET.J.fasta.gz", SequenceType.PROTEIN);
		list.addAll(Fasta.readEntries("/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/Gencode20cds_DECOY_TRYPSIN.fasta.gz", SequenceType.PROTEIN));
		for( Fasta fasta : list ) {
			pw.print(fasta.getAccession()+"\t");
			List<String> peptides = Digester.digestSequence(fasta.getSequence(), Enzyme.TRYPSIN, 2);
			for( String peptide : peptides )
				if( peptide.length() >= 7 && peptide.length() <= 30 )
					pw.print(peptide+",");
			pw.println();
		}
		pw.close();
	}*/
}
