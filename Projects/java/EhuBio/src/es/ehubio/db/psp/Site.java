package es.ehubio.db.psp;

import es.ehubio.db.PtmItem;

public class Site implements PtmItem {
	private String type;
	private String gene;
	private String protein;
	private String accession;
	private String rsd;
	private char aminoacid;
	private int position = -1;
	private String organism;
	private String sequence;	
	
	public String getType() {
		return type;
	}
	public void setType(String mod) {
		this.type = mod;
	}
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
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
	public String getRsd() {
		return rsd;
	}
	public void setRsd(String rsd) {
		this.rsd = rsd;
	}
	public char getAminoacid() {
		if( aminoacid == 0 )
			aminoacid = rsd.charAt(0);
		return aminoacid;
	}
	public void setAminoacid(char aminoacid) {
		this.aminoacid = aminoacid;
	}
	public int getPosition() {
		if( position < 0 ) {
			int len = rsd.indexOf('-');
			String str = rsd.substring(1, len);
			position = Integer.parseInt(str);
		}
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getOrganism() {
		return organism;
	}
	public void setOrganism(String organism) {
		this.organism = organism;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}	
}
