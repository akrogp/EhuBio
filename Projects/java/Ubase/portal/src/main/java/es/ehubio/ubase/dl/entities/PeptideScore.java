package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the PeptideScore database table.
 * 
 */
@Entity
@NamedQuery(name="PeptideScore.findAll", query="SELECT p FROM PeptideScore p")
public class PeptideScore implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String scoreType;
	private double scoreValue;
	private PeptideEvidence peptideEvidenceBean;

	public PeptideScore() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getScoreType() {
		return this.scoreType;
	}

	public void setScoreType(String scoreType) {
		this.scoreType = scoreType;
	}


	public double getScoreValue() {
		return this.scoreValue;
	}

	public void setScoreValue(double scoreValue) {
		this.scoreValue = scoreValue;
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