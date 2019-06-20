package es.ehubio.dubase.pl.beans;

import java.util.ArrayList;
import java.util.List;

public class DetailsBean {
	public static class Sample {
		private String name;
		private String lfq;
		private List<String> lfqs;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getLfq() {
			return lfq;
		}
		public void setLfq(String lfq) {
			this.lfq = lfq;
		}
		public List<String> getLfqs() {
			if( lfqs == null )
				lfqs = new ArrayList<>();
			return lfqs;
		}
		public void setLfqs(List<String> lfqs) {
			this.lfqs = lfqs;
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

	public int getTotalPepts() {
		return searchBean.getTotalPepts();
	}

	public int getUniqPepts() {
		return searchBean.getUniqPepts();
	}

	public String getWeightFmt() {
		return searchBean.getWeightFmt();
	}
	
	public String getGlygly() {
		return searchBean.getGlygly();
	}
	
	public List<Sample> getSamples() {
		return samples;
	}
	
	public List<Integer> getReplicates() {
		int size = samples.get(0).getLfqs().size();
		List<Integer> result = new ArrayList<>(size);
		for( int i = 0; i < size; i++ )
			result.add(i);
		return result;
	}
	
	public String getCoverage() {
		return searchBean.getCoverageFmt();
	}
	
	public SearchBean getSearchBean() {
		return searchBean;
	}

	private final SearchBean searchBean;
	private final List<Sample> samples = new ArrayList<>();
}
