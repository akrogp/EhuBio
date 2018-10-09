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
	private double deltaMass;
	private int position;
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


	public double getDeltaMass() {
		return this.deltaMass;
	}

	public void setDeltaMass(double deltaMass) {
		this.deltaMass = deltaMass;
	}


	public int getPosition() {
		return this.position;
	}

	public void setPosition(int position) {
		this.position = position;
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