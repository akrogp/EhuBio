package es.ehubio.proteomics;

import java.util.Collection;

public interface Decoyable {
	/**
	 * 
	 * @return true if decoy, false if no decoy, and null if unknown
	 */
	Boolean getDecoy();
	
	/**
	 * 
	 * @param decoy true if decoy, false if no decoy, and null if unknown
	 */
	void setDecoy( Boolean decoy );
	
	
	/**
	 * Replaces all existing scores of the same type with the new score
	 * 
	 * @param score
	 * @return true if there was not a previous score of the same type
	 */
	boolean setScore(Score score);
	
	/**
	 * 
	 * @return Score collection
	 */
	Collection<Score> getScores();
	
	/**
	 * 
	 * @param type
	 * @return The first score of the specified type
	 */
	Score getScoreByType( ScoreType type );
	
	/**
	 * Clear score list
	 */
	void clearScores();
	
	/**
	 * 
	 * @param passThreshold true if passes the FDR threshold, false if not 
	 */
	void setPassThreshold(boolean passThreshold);
	
	/**
	 * 
	 * @return true if passes the FDR threshold, false if not
	 */
	boolean isPassThreshold();
	
	/**
	 * 
	 * @return true to skip this object from FDR calculation
	 */
	boolean skipFdr();
}
