package es.ehubio.dubase.dl.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;


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
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlValue
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@XmlTransient
	public String getCommonName() {
		return this.commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}


	@XmlTransient
	public String getSciName() {
		return this.sciName;
	}

	public void setSciName(String sciName) {
		this.sciName = sciName;
	}

}