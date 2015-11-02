package es.ehubio.portal.model;

import java.io.Serializable;

public class Project implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String url;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
}