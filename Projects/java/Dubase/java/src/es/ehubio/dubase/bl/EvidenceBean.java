package es.ehubio.dubase.bl;

import java.util.ArrayList;
import java.util.List;

public class EvidenceBean {
	private List<String> genes;
	private List<String> descriptions;
	private List<EvScoreBean> evScores;
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
	
	public List<EvScoreBean> getEvScores() {
		if( evScores == null )
			evScores = new ArrayList<>();
		return evScores;
	}
	public List<ReplicateBean> getReplicates() {
		if( replicates == null )
			replicates = new ArrayList<>();
		return replicates;
	}
}
