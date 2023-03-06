package es.ehubio.db.phosphositeplus;

import es.ehubio.model.ProteinModification;

public class PhosphoEntry extends ProteinModification {
	public String getProtein() {
		return protein;
	}
	public void setProtein(String protein) {
		this.protein = protein;
	}
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	public String getChrLocation() {
		return chrLocation;
	}
	public void setChrLocation(String chrLocation) {
		this.chrLocation = chrLocation;
	}
	public String getGrpId() {
		return grpId;
	}
	public void setGrpId(String grpId) {
		this.grpId = grpId;
	}
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	private String protein;
	private String accession;
	private String gene;
	private String chrLocation;
	private String grpId;
	private String org;
}
