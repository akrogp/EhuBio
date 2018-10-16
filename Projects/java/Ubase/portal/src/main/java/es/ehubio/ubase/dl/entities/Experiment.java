package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the Experiment database table.
 * 
 */
@Entity
@NamedQuery(name="Experiment.findAll", query="SELECT e FROM Experiment e")
public class Experiment implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String accession;
	private String affiliation;
	private String contactMail;
	private String contactName;
	private String dbVersion;
	private String description;
	private Date expDate;
	private String instrument;
	private Date pubDate;
	private Date subDate;
	private String title;
	private Taxon taxon;

	public Experiment() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getAccession() {
		return this.accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}


	public String getAffiliation() {
		return this.affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}


	public String getContactMail() {
		return this.contactMail;
	}

	public void setContactMail(String contactMail) {
		this.contactMail = contactMail;
	}


	public String getContactName() {
		return this.contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}


	public String getDbVersion() {
		return this.dbVersion;
	}

	public void setDbVersion(String dbVersion) {
		this.dbVersion = dbVersion;
	}


	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@Temporal(TemporalType.TIMESTAMP)
	public Date getExpDate() {
		return this.expDate;
	}

	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}


	public String getInstrument() {
		return this.instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}


	@Temporal(TemporalType.TIMESTAMP)
	public Date getPubDate() {
		return this.pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}


	@Temporal(TemporalType.TIMESTAMP)
	public Date getSubDate() {
		return this.subDate;
	}

	public void setSubDate(Date subDate) {
		this.subDate = subDate;
	}


	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	//uni-directional many-to-one association to Taxon
	@ManyToOne
	@JoinColumn(name="organism")
	public Taxon getTaxon() {
		return this.taxon;
	}

	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

}