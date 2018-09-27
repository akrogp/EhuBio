package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the ModificationEvidence database table.
 * 
 */
@Entity
@NamedQuery(name="ModificationEvidence.findAll", query="SELECT m FROM ModificationEvidence m")
public class ModificationEvidence implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private double deltaMassExp;
	private int position;
	private double probability;
	private Modification modificationBean;
	private PeptideEvidence peptideEvidenceBean;

	public ModificationEvidence() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public double getDeltaMassExp() {
		return this.deltaMassExp;
	}

	public void setDeltaMassExp(double deltaMassExp) {
		this.deltaMassExp = deltaMassExp;
	}


	public int getPosition() {
		return this.position;
	}

	public void setPosition(int position) {
		this.position = position;
	}


	public double getProbability() {
		return this.probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}


	//uni-directional many-to-one association to Modification
	@ManyToOne
	@JoinColumn(name="modification")
	public Modification getModificationBean() {
		return this.modificationBean;
	}

	public void setModificationBean(Modification modificationBean) {
		this.modificationBean = modificationBean;
	}


	//uni-directional many-to-one association to PeptideEvidence
	@ManyToOne
	@JoinColumn(name="peptideEvidence")
	public PeptideEvidence getPeptideEvidenceBean() {
		return this.peptideEvidenceBean;
	}

	public void setPeptideEvidenceBean(PeptideEvidence peptideEvidenceBean) {
		this.peptideEvidenceBean = peptideEvidenceBean;
	}

}