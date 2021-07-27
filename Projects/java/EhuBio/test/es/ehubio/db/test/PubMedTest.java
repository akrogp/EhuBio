package es.ehubio.db.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import es.ehubio.db.pubmed.Paper;
import es.ehubio.db.pubmed.PubMed;

public class PubMedTest {
	@Test
	public void testPaper() throws IOException {
		Paper paper = PubMed.fillPaper("30531833");
		assertEquals("liuyzg@shsci.org", paper.getLastAuthor().getEmail());
	}
}
