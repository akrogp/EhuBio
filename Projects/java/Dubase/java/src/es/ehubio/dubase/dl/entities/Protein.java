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


/**
 * The persistent class for the Protein database table.
 * 
 */
@Entity
@NamedQueries({
@NamedQuery(name="Protein.findAll", query="SELECT p FROM Protein p"),
@NamedQuery(name="Protein.findByAcc", query="SELECT p FROM Protein p WHERE p.accession = :acc")
})
public class Protein implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String accession;
	private String description;
	private String name;
	private Gene geneBean;

	public Protein() {
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


	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	//uni-directional many-to-one association to Gene
	@ManyToOne
	@JoinColumn(name="gene")
	public Gene getGeneBean() {
		return this.geneBean;
	}

	public void setGeneBean(Gene geneBean) {
		this.geneBean = geneBean;
	}

}