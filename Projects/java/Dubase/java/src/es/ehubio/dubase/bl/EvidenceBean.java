package es.ehubio.dubase.bl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvidenceBean {
	private List<String> genes;
	private List<String> descriptions;
	private Map<Integer, Double> mapScores;
	private List<ReplicateBean> replicates;
	
	public List<String> getGenes() {
		if( genes == null )
			genes = new ArrayList<>();
		return genes;
	}
	
	public List<String> getDescriptions() {
		if( descriptions == null )
			descriptions = new ArrayList<>();
		return descriptions;
	}
	
	public Map<Integer, Double> getMapScores() {
		if( mapScores == null )
			mapScores = new HashMap<>();
		return mapScores;
	}
	public List<ReplicateBean> getReplicates() {
		if( replicates == null )
			replicates = new ArrayList<>();
		return replicates;
	}
	
	public void putScore(Score score, Double value) {
		getMapScores().put(score.ordinal(), value);
	}
}
