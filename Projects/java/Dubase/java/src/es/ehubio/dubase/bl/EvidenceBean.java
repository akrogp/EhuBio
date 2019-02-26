package es.ehubio.dubase.bl;

import java.util.ArrayList;
import java.util.List;

public class EvidenceBean {
	private String gene;
	private String description;
	private List<EvScoreBean> evScores;
	private List<ReplicateBean> replicates;
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
