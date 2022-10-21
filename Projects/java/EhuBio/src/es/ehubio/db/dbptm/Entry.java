package es.ehubio.db.dbptm;

import java.util.Set;

import es.ehubio.db.PtmItem;

public class Entry implements PtmItem {
	public String getProtein() {
		return protein;
	}
	
	public void setProtein(String id) {
		this.protein = id;
	}
	
	public String getAccession() {
		return accession;
	}
	
	public void setAccession(String accession) {
		this.accession = accession;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	/*public String getModification() {
		return modification;
	}
	
	public void setModification(String modification) {
		this.modification = modification;
	}*/
	
	public Set<String> getPubmedIds() {
		return pubmedIds;
	}
	
	public void setPubmedIds(Set<String> pubmedIds) {
		this.pubmedIds = pubmedIds;
	}
	
	/*public String getResource() {
		return resource;
	}
	
	public void setResource(String resource) {
		this.resource = resource;
	}*/
	
	public char getAminoacid() {
		return aminoacid;
	}
	
	public void setAminoacid(char aminoacid) {
		this.aminoacid = aminoacid;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(protein+":"+accession+":"+position+":"+aminoacid+":"+type);
		boolean first = true;
		for( String id : pubmedIds )
			if( first ) {
				str.append(":"+id);
				first = false;
			} else
				str.append(";"+id);
		return str.toString();
	}
	
	private String protein;
	private String accession;
	private int position;
	//private String modification;
	private Set<String> pubmedIds;
	//private String resource;
	private char aminoacid;
	private String type;
}
