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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;


/**
 * The persistent class for the Enzyme database table.
 * 
 */
@XmlRootElement
@Entity
@NamedQueries({
@NamedQuery(name="Enzyme.findAll", query="SELECT e FROM Enzyme e"),
@NamedQuery(name="Enzyme.findByGene", query="SELECT e FROM Enzyme e WHERE e.gene = :gene")
})
public class Enzyme implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String description;
	private String gene;
	private Superfamily superfamilyBean;

	public Enzyme() {
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


	@XmlTransient
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@XmlValue
	public String getGene() {
		return this.gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}


	//uni-directional many-to-one association to Superfamily
	@ManyToOne
	@JoinColumn(name="superfamily")
	@XmlTransient
	public Superfamily getSuperfamilyBean() {
		return this.superfamilyBean;
	}

	public void setSuperfamilyBean(Superfamily superfamilyBean) {
		this.superfamilyBean = superfamilyBean;
	}

}