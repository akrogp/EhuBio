package es.ehubio.dubase.dl.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;


/**
 * The persistent class for the Modification database table.
 * 
 */
@Entity
@NamedQuery(name="Modification.findAll", query="SELECT m FROM Modification m")
public class Modification implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private int position;
	private Protein proteinBean;
	private ModType modType;
	private Experiment experimentBean;

	public Modification() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public int getPosition() {
		return this.position;
	}

	public void setPosition(int position) {
		this.position = position;
	}


	//bi-directional many-to-one association to Evidence
	@ManyToOne
	@JoinColumn(name="protein")
	public Protein getProteinBean() {
		return this.proteinBean;
	}

	public void setProteinBean(Protein proteinBean) {
		this.proteinBean = proteinBean;
	}


	//uni-directional many-to-one association to ModType
	@ManyToOne
	@JoinColumn(name="type")
	public ModType getModType() {
		return this.modType;
	}

	public void setModType(ModType modType) {
		this.modType = modType;
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
}