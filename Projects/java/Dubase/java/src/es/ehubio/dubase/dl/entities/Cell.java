package es.ehubio.dubase.dl.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * The persistent class for the Cell database table.
 * 
 */
@XmlRootElement
@Entity
@NamedQueries({
@NamedQuery(name="Cell.findAll", query="SELECT c FROM Cell c"),
@NamedQuery(name="Cell.findByName", query="SELECT c FROM Cell c WHERE c.name = :name")
})
public class Cell implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private Taxon taxonBean;

	public Cell() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	//uni-directional many-to-one association to Taxon
	@ManyToOne
	@JoinColumn(name="taxon")
	@XmlElement(name="taxon")
	public Taxon getTaxonBean() {
		return this.taxonBean;
	}

	public void setTaxonBean(Taxon taxonBean) {
		this.taxonBean = taxonBean;
	}

}