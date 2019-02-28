package es.ehubio.dubase.bl.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.dubase.bl.Score;

public class EvidenceBean {
	private List<String> genes;
	private List<String> descriptions;
	private Map<Integer, Double> mapScores;
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
}
