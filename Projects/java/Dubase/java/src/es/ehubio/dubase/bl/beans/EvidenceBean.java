package es.ehubio.dubase.bl.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import es.ehubio.Util;
import es.ehubio.dubase.bl.Score;
import es.ehubio.dubase.dl.entities.Experiment;

public class EvidenceBean {
	private Experiment experiment;
	private List<String> genes;
	private List<String> descriptions;
	private Map<Integer, Double> mapScores;
	private List<Integer> modPositions;
	private List<ReplicateBean> samples, controls;
	
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
	
	public List<Integer> getModPositions() {
		if( modPositions == null )
			modPositions = new ArrayList<>();
		return modPositions;
	}
	
	public List<ReplicateBean> getSamples() {
		if( samples == null )
			samples = new ArrayList<>();
		return samples;
	}
	
	public List<ReplicateBean> getControls() {
		if( controls == null )
			controls = new ArrayList<>();
		return controls;
	}
	
	public void putScore(Score score, Double value) {
		getMapScores().put(score.ordinal(), value);
	}

	public Experiment getExperiment() {
		return experiment;
	}
	
	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	
	private String getEnzyme() {
		if( experiment == null )
			return null;
		return experiment.getEnzymeBean().getGene();
	}
	
	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof EvidenceBean) )
			return super.equals(obj);
		EvidenceBean other = (EvidenceBean)obj;
		for( int i = 0; i < genes.size(); i++ )
			if( !getGenes().get(i).equals(other.getGenes().get(i)) )
				return false;
		return Util.compare(getEnzyme(), other.getEnzyme());
	}
	
	@Override
	public int hashCode() {
		int hash1 = Objects.hash(getGenes().toArray());
		if( getEnzyme() == null )
			return hash1;
		return Objects.hash(getEnzyme(), hash1);
	}
}
