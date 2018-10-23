package es.ehubio.ubase.dl.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;


/**
 * The persistent class for the Protein database table.
 * 
 */
@Entity
@NamedQuery(name="Protein.findAll", query="SELECT p FROM Protein p")
public class Protein implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String accession;
	private String entry;
	private String gene;
	private String name;
	private String sequence;
	private int version;
	private Experiment experimentBean;
	private Taxon taxon;

	public Protein() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getAccession() {
		return this.accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}


	public String getEntry() {
		return this.entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}


	public String getGene() {
		return this.gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Column(columnDefinition = "TEXT")
	public String getSequence() {
		return this.sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}


	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}


	//uni-directional many-to-one association to Experiment
	@ManyToOne
	@JoinColumn(name="experiment")
	public Experiment getExperimentBean() {
		return this.experimentBean;
	}

	public void setExperimentBean(Experiment experimentBean) {
		this.experimentBean = experimentBean;
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