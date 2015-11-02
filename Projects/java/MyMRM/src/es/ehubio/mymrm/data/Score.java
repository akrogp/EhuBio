package es.ehubio.mymrm.data;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Score database table.
 * 
 */
@Entity
@NamedQuery(name="Score.findAll", query="SELECT s FROM Score s")
public class Score implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private double value;

	//bi-directional many-to-one association to PeptideEvidence
	@ManyToOne
	@JoinColumn(name="peptideEvidence")
	private PeptideEvidence peptideEvidenceBean;

	//bi-directional many-to-one association to ScoreType
	@ManyToOne
	@JoinColumn(name="type")
	private ScoreType scoreType;

	public Score() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getValue() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public PeptideEvidence getPeptideEvidenceBean() {
		return this.peptideEvidenceBean;
	}

	public void setPeptideEvidenceBean(PeptideEvidence peptideEvidenceBean) {
		this.peptideEvidenceBean = peptideEvidenceBean;
	}

	public ScoreType getScoreType() {
		return this.scoreType;
	}

	public void setScoreType(ScoreType scoreType) {
		this.scoreType = scoreType;
	}

}