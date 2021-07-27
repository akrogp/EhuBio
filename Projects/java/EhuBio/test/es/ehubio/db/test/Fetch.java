package es.ehubio.db.test;

import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import es.ehubio.db.ebi.DbFetcher;
import es.ehubio.db.uniprot.xml.Entry;

public class Fetch {
	private final static Logger logger = Logger.getLogger(Fetch.class.getName());
	
	//@Test
	public void test() {
		String acc = "P27348";
		DbFetcher fetcher = new DbFetcher();
		Entry entry = fetcher.fetchUniProt(acc);
		logger.info(entry.getSequence().getValue());
		assertTrue(entry.getAccession().contains(acc));
	}

}
