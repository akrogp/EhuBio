package es.ehubio.ubase.bl.result;

import java.util.HashMap;
import java.util.Map;

public abstract class SearchResult {
	private final Map<String, ScoreResult> scores = new HashMap<>();

	public Map<String, ScoreResult> getScores() {
		return scores;
	}
}
