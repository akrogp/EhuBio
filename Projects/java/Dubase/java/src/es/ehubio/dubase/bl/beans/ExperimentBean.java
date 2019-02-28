package es.ehubio.dubase.bl.beans;

import java.util.Date;

public class ExperimentBean {
	private String enzyme;
	private Date date;
	private String contactName;
	private String contactMail;
	private String contactAffiliation;
	private String method;
	private String evidencesPath;
	
	public String getEnzyme() {
		return enzyme;
	}
	public void setEnzyme(String enzyme) {
		this.enzyme = enzyme;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactMail() {
		return contactMail;
	}
	public void setContactMail(String contactMail) {
		this.contactMail = contactMail;
	}
	public String getContactAffiliation() {
		return contactAffiliation;
	}
	public void setContactAffiliation(String contactAffiliation) {
		this.contactAffiliation = contactAffiliation;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getEvidencesPath() {
		return evidencesPath;
	}
	public void setEvidencesPath(String evidencesPath) {
		this.evidencesPath = evidencesPath;
	}
}
