package es.ehubio.dubase.pl;

public class SearchBean {
	private String experiment;
	private String enzyme;
	private String genes;
	private String foldChange;
	private String pValue;
	private String totalPepts;
	private String uniqPepts;
	private String weight;
	
	public String getExperiment() {
		return experiment;
	}
	public void setExperiment(String experiment) {
		this.experiment = experiment;
	}
	public String getEnzyme() {
		return enzyme;
	}
	public void setEnzyme(String enzyme) {
		this.enzyme = enzyme;
	}
	public String getGenes() {
		return genes;
	}
	public void setGenes(String genes) {
		this.genes = genes;
	}
	public String getFoldChange() {
		return foldChange;
	}
	public void setFoldChange(String foldChange) {
		this.foldChange = foldChange;
	}
	public String getpValue() {
		return pValue;
	}
	public void setpValue(String pValue) {
		this.pValue = pValue;
	}
	public String getTotalPepts() {
		return totalPepts;
	}
	public void setTotalPepts(String totalPepts) {
		this.totalPepts = totalPepts;
	}
	public String getUniqPepts() {
		return uniqPepts;
	}
	public void setUniqPepts(String uniqPepts) {
		this.uniqPepts = uniqPepts;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
}
