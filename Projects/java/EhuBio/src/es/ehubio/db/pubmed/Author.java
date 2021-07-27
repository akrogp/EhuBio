package es.ehubio.db.pubmed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Author implements Serializable {
	private static final long serialVersionUID = 1L;
	private String abbrvName;
	private String fullName;
	private final List<String> affiliations = new ArrayList<>();
	private String email;
	
	public String getAbbrvName() {
		return abbrvName;
	}
	public void setAbbrvName(String abbrvName) {
		this.abbrvName = abbrvName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public List<String> getAffiliations() {
		return affiliations;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
