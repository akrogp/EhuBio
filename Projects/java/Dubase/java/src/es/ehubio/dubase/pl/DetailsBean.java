package es.ehubio.dubase.pl;

public class DetailsBean {
	private final SearchBean searchBean;
	
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

	public String getFoldChange() {
		return searchBean.getFoldChange();
	}

	public String getpValue() {
		return searchBean.getpValue();
	}

	public String getTotalPepts() {
		return searchBean.getTotalPepts();
	}

	public String getUniqPepts() {
		return searchBean.getUniqPepts();
	}

	public String getWeight() {
		return searchBean.getWeight();
	}
	
	
}
