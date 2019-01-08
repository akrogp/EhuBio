package es.ehubio.dubase.bl;

public class EvidenceBean {
	private String gene;
	private double foldChange;
	private double pValue;
	
	public EvidenceBean() {
	}
	
	public EvidenceBean(String gene, double foldChange, double pValue) {
		this.gene = gene;
		this.foldChange = foldChange;
		this.pValue = pValue;
	}
	
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	public double getFoldChange() {
		return foldChange;
	}
	public void setFoldChange(double foldChange) {
		this.foldChange = foldChange;
	}
	public double getPValue() {
		return pValue;
	}
	public void setPValue(double pValue) {
		this.pValue = pValue;
	}
}
