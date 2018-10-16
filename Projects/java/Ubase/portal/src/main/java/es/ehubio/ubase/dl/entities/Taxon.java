package es.ehubio.ubase.dl.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The persistent class for the Taxon database table.
 * 
 */
@XmlRootElement
@Entity
@NamedQuery(name="Taxon.findAll", query="SELECT t FROM Taxon t")
public class Taxon implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String commonName;
	private String sciName;

	public Taxon() {
	}


	@Id
	@XmlAttribute
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getCommonName() {
		return this.commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}


	public String getSciName() {
		return this.sciName;
	}

	public void setSciName(String sciName) {
		this.sciName = sciName;
	}

}