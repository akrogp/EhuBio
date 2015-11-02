package es.ehubio.proteomics.test;

import org.junit.Test;
import static org.junit.Assert.*;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.HeaderParser;

public class HeaderTest {

	@Test
	public void test() {
		String header = "decoy-sp|ALBU_BOVIN| Decoy for sp|ALBU_BOVIN| using PSEUDO_REVERSE strategy with TRYPSIN";
		HeaderParser parser = Fasta.guessParser(header);
		assertEquals("decoy-sp|ALBU_BOVIN|", parser.getAccession());
	}

}
