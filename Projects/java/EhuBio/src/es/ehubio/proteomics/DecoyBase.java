package es.ehubio.proteomics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class DecoyBase implements Decoyable {
	private Boolean decoy = null;
	private Map<ScoreType, Score> scores = new HashMap<>(); 
	private boolean passThreshold = true;
	private String uniqueString;

	@Override
	public Score putScore(Score score) {
		return scores.put(score.getType(), score);
	}

	@Override
	public Score getScoreByType(ScoreType type) {
		return scores.get(type);
	}
	
	@Override
	public Collection<Score> getScores() {
		return scores.values();
	}
	
	@Override
	public void clearScores() {
		scores.clear();
	}

	@Override
	public boolean skipFdr() {
		return false;
	}

	@Override
	public Boolean getDecoy() {
		return decoy;
	}
	
	/**
	 * 
	 * @return true if decoy and false if not or unknown
	 */
	public boolean isDecoy() {
		return Boolean.TRUE.equals(getDecoy());
	}
	
	/**
	 * 
	 * @return true if not decoy or unknown
	 */
	public boolean isTarget() {
		return !isDecoy();
	}

	@Override
	public void setDecoy(Boolean decoy) {
		this.decoy = decoy;
	}
	
	@Override
	public void setPassThreshold(boolean passThreshold) {
		this.passThreshold = passThreshold;
	}
	
	@Override
	public boolean isPassThreshold() {
		return passThreshold;
	}
	
	public void setUniqueString( String unique ) {
		uniqueString = unique;
	}
	
	public String getUniqueString() {
		return uniqueString == null ? buildUniqueString() : uniqueString;
	}
	
	abstract protected String buildUniqueString();
	
	public static <T extends DecoyBase> T getBest( Collection<T> list, ScoreType type ) {
		T best = null;
		for( T item : list ) {
			Score score = item.getScoreByType(type);
			if( score == null )
				continue;
			if( best != null && best.getScoreByType(type).compare(score.getValue()) >= 0 )
				continue;
			best = item;				
		}
		return best;
	}
}