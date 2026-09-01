package es.ehubio.wregex.cli.bl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import es.ehubio.wregex.Result;

public class SearchRunnerTest {
	private static final double THRESHOLD = 50;
	private static final String NES_REGEX =
			"([DEQS].{0,1})([LIMA])(.{2,3})([LIVMF])([^P]{2,3})([LMVF])([^P])([LMIV])(.{0,3}[DEQ])";

	@ParameterizedTest(name = "{0} -> {1} results")
	@CsvSource({
		"NES/CRM1,         24",
		"NES/CRM1@Relaxed, 172",
		"NLS_MonoCore,     7"
	})
	void searchesMotifAgainstCajalFasta(String motif, int expectedResults) throws Exception {
		SearchOptions options = new SearchOptions(
				null, null, motif, cajalFastaPath(), null, THRESHOLD, false, true, "csv");

		List<Result> results = SearchRunner.run(options);

		assertEquals(expectedResults, results.size());
	}

	@Test
	void customRegexWithPssmMatchesMotifCatalogEquivalent() throws Exception {
		SearchOptions options = new SearchOptions(
				NES_REGEX, resourcePath("/motifs/NES-total.pssm"), null, cajalFastaPath(), null,
				THRESHOLD, false, true, "csv");

		List<Result> results = SearchRunner.run(options);

		// Same regex + same PSSM as NES/CRM1's default ("Recommended") definition
		assertEquals(24, results.size());
	}

	@Test
	void zeroThresholdDoesNotFilterUnscoredResults() throws Exception {
		SearchOptions options = new SearchOptions(
				NES_REGEX, null, null, cajalFastaPath(), null, 0, false, true, "csv");

		List<Result> results = SearchRunner.run(options);

		// Without a PSSM every match scores -1.0; if threshold=0 filtered by
		// score instead of disabling filtering, this would always be empty.
		assertFalse(results.isEmpty());
	}

	@Test
	void ungroupedSearchReturnsMoreResultsThanGrouped() throws Exception {
		SearchOptions grouped = new SearchOptions(
				null, null, "NES/CRM1", cajalFastaPath(), null, THRESHOLD, false, true, "csv");
		SearchOptions ungrouped = new SearchOptions(
				null, null, "NES/CRM1", cajalFastaPath(), null, THRESHOLD, false, false, "csv");

		int groupedCount = SearchRunner.run(grouped).size();
		int ungroupedCount = SearchRunner.run(ungrouped).size();

		assertTrue(ungroupedCount > groupedCount,
				() -> "expected ungrouped (" + ungroupedCount + ") > grouped (" + groupedCount + ")");
	}

	@Test
	void unknownMotifThrows() {
		SearchOptions options = new SearchOptions(
				null, null, "Foo", "unused", null, THRESHOLD, false, true, "csv");

		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> SearchRunner.run(options));
		assertTrue(e.getMessage().contains("Unknown motif 'Foo'"));
	}

	@Test
	void unknownDefinitionThrows() {
		SearchOptions options = new SearchOptions(
				null, null, "NES/CRM1@Bar", "unused", null, THRESHOLD, false, true, "csv");

		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> SearchRunner.run(options));
		assertTrue(e.getMessage().contains("Unknown definition 'Bar' for motif 'NES/CRM1'"));
	}

	private static String cajalFastaPath() throws URISyntaxException {
		return resourcePath("/Cajal.fasta");
	}

	private static String resourcePath(String resource) throws URISyntaxException {
		URL url = SearchRunnerTest.class.getResource(resource);
		Path path = Paths.get(url.toURI());
		return path.toString();
	}
}
