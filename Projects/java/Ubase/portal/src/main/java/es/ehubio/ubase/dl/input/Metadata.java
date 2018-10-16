package es.ehubio.ubase.dl.input;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import es.ehubio.ubase.dl.entities.Taxon;
import es.ehubio.ubase.dl.providers.Provider;

@XmlRootElement
@XmlType(propOrder={
		"version", "provider",
		"title","contactName","contactMail","affiliation","organism","dbVersion","description","instrument",
		"expDate", "subDate", "pubDate", "conditions"})
public class Metadata {
	public static final String CURRENT_VERSION = "1.0";
	private Provider provider;
	private String version;
	private String title;
	private String contactName;
	private String contactMail;
	private String affiliation;
	private Taxon organism;
	private String dbVersion;
	private String description;
	private String instrument;
	private Date expDate, subDate, pubDate;
	private List<Condition> conditions;
	private File data;
	
	public Provider getProvider() {
		return provider;
	}
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	@XmlAttribute
	public String getVersion() {
		return version == null ? CURRENT_VERSION : version;
	}
	public void setVersion(String version) {
		this.version = version;
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
	public Taxon getOrganism() {
		return organism;
	}
	public void setOrganism(Taxon organism) {
		this.organism = organism;
	}
	public String getDbVersion() {
		return dbVersion;
	}
	public void setDbVersion(String dbVersion) {
		this.dbVersion = dbVersion;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@XmlElement(name="condition")
	public List<Condition> getConditions() {
		return conditions;
	}
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAffiliation() {
		return affiliation;
	}
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	public String getInstrument() {
		return instrument;
	}
	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}
	public Date getExpDate() {
		return expDate;
	}
	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}
	public Date getSubDate() {
		return subDate;
	}
	public void setSubDate(Date subDate) {
		this.subDate = subDate;
	}
	public Date getPubDate() {
		return pubDate;
	}
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}
	@XmlTransient
	public File getData() {
		return data;
	}
	public void setData(File data) {
		this.data = data;
	}
}
