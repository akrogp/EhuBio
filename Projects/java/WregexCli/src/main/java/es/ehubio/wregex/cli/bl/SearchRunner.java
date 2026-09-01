package es.ehubio.wregex.cli.bl;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.wregex.InputGroup;
import es.ehubio.wregex.Pssm;
import es.ehubio.wregex.Result;
import es.ehubio.wregex.ResultGroup;
import es.ehubio.wregex.Wregex;
import es.ehubio.wregex.cli.dl.MotifCatalog;
import es.ehubio.wregex.cli.dl.MotifDefinition;
import es.ehubio.wregex.cli.dl.MotifInformation;

public class SearchRunner {
	public static List<Result> run(SearchOptions options) throws Exception {
		Wregex wregex = buildWregex(options);

		List<InputGroup> inputGroups = readInput(options.getInputPath());

		List<ResultGroup> resultGroups = new ArrayList<>();
		for (InputGroup inputGroup : inputGroups)
			resultGroups.addAll(wregex.searchGrouping(inputGroup.getFasta()));

		// score filtering mirrors the webapp: threshold <= 0 means "no filtering",
		// since unscored (no-PSSM) results carry score == -1.0
		if (options.getThreshold() > 0.0)
			resultGroups.removeIf(group -> group.getScore() < options.getThreshold());

		List<Result> results = expand(resultGroups, options.isGrouping());

		if (options.isFilterEqual())
			results = filterEqual(results);

		Collections.sort(results);
		return results;
	}

	private static Wregex buildWregex(SearchOptions options) throws Exception {
		if (options.getMotif() != null)
			return buildWregexFromMotif(options.getMotif());
		Pssm pssm = options.getPssmPath() == null ? null : Pssm.load(options.getPssmPath(), true);
		return new Wregex(options.getRegex(), pssm);
	}

	/** Resolves a "name" or "name@definition" token (defaulting to the first definition) into a Wregex engine. */
	private static Wregex buildWregexFromMotif(String token) throws Exception {
		MotifCatalog catalog = MotifCatalog.load();
		String[] parts = token.split("@", 2);
		String motifName = parts[0];
		String definitionName = parts.length > 1 ? parts[1] : null;

		MotifInformation motif = catalog.findMotif(motifName);
		if (motif == null)
			throw new IllegalArgumentException(
					"Unknown motif '" + motifName + "'. Available: " + availableNames(catalog));

		MotifDefinition definition = definitionName == null
				? motif.getDefinitions().get(0)
				: motif.getDefinitions().stream()
						.filter(d -> d.getName().equalsIgnoreCase(definitionName))
						.findFirst()
						.orElseThrow(() -> new IllegalArgumentException(
								"Unknown definition '" + definitionName + "' for motif '" + motifName + "'"));

		Pssm pssm = definition.getPssm() == null ? null : catalog.loadPssm(definition.getPssm());
		return new Wregex(definition.getRegex(), pssm);
	}

	private static String availableNames(MotifCatalog catalog) {
		return catalog.getMotifs().stream().map(MotifInformation::getName).reduce((a, b) -> a + ", " + b).orElse("");
	}

	private static List<InputGroup> readInput(String path) throws IOException, InvalidSequenceException {
		try (Reader reader = new FileReader(path)) {
			return InputGroup.readEntries(reader);
		}
	}

	private static List<Result> expand(List<ResultGroup> resultGroups, boolean grouping) {
		List<Result> results = new ArrayList<>();
		for (ResultGroup group : resultGroups) {
			if (grouping)
				results.add(group.getRepresentative());
			else
				for (Result result : group)
					results.add(result);
		}
		return results;
	}

	private static List<Result> filterEqual(List<Result> candidates) {
		List<Result> results = new ArrayList<>();
		Set<String> seen = new HashSet<>();
		for (Result result : candidates)
			if (seen.add(result.getMatch().toLowerCase()))
				results.add(result);
		return results;
	}
}
