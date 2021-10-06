package es.ehubio.dubase.pl.beans;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.ScoreType;

public class DetailsBean {
	public static class Result {
		private String name;
		private String avgScore;
		private String pValue;
		private List<String> scores;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getAvgScore() {
			return avgScore;
		}
		public void setAvgScore(String lfq) {
			this.avgScore = lfq;
		}
		public List<String> getScores() {
			if( scores == null )
				scores = new ArrayList<>();
			return scores;
		}
		public void setScores(List<String> lfqs) {
			this.scores = lfqs;
		}
		public String getpValue() {
			return pValue;
		}
		public void setpValue(String pValue) {
			this.pValue = pValue;
		}
	}
	
	public DetailsBean(SearchBean searchBean) {
		this.searchBean = searchBean;
	}

	public String getExperiment() {
		return searchBean.getExperiment();
	}

	public String getEnzyme() {
		return searchBean.getEnzyme();
	}

	public String getGenes() {
		return searchBean.getGenes();
	}
	
	public String getProteins() {
		return searchBean.getProteins();
	}
	
	public String getDescriptions() {
		return searchBean.getDescriptions();
	}

	public String getFoldChangeFmt() {
		return searchBean.getFoldChangeFmt();
	}

	public String getpValueFmt() {
		return searchBean.getpValueFmt();
	}

	public Integer getTotalPepts() {
		return searchBean.getTotalPepts();
	}

	public Integer getUniqPepts() {
		return searchBean.getUniqPepts();
	}

	public String getWeightFmt() {
		return searchBean.getWeightFmt();
	}
	
	public String getGlygly() {
		return searchBean.getGlygly();
	}
	
	public List<Result> getSamples() {
		return samples;
	}
	
	public Map<String, List<Result>> getMods() {
		return mods;
	}
	
	public List<Integer> getRepsIndexes() {
		if( reps == null || reps.isEmpty() )
			return new ArrayList<>();
		List<Integer> result = new ArrayList<>(reps.size());
		for( int i = 0; i < reps.size(); i++ )
			result.add(i);
		return result;
	}
	
	public String getCoverage() {
		return searchBean.getCoverageFmt();
	}
	
	public SearchBean getSearchBean() {
		return searchBean;
	}
	
	public ScoreType getScoreType() {
		return scoreType;
	}
	
	public void setScoreType(ScoreType scoreType) {
		this.scoreType = scoreType;
	}

	public String getScoreName() {
		return scoreType == null ? null : scoreType.getName();
	}
	
	public List<String> getReps() {
		return reps;
	}
	
	public void setReps(List<String> reps) {
		this.reps = reps;
	}
	
	public Evidence getEvidence() {
		return searchBean.getEntity();
	}

	private final SearchBean searchBean;
	private ScoreType scoreType;
	private List<String> reps;
	private final List<Result> samples = new ArrayList<>();
	private final Map<String, List<Result>> mods = new LinkedHashMap<>();
}
