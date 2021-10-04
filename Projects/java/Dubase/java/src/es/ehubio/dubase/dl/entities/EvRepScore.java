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
 * The persistent class for the EvRepScore database table.
 * 
 */
@Entity
@NamedQuery(name="EvRepScore.findAll", query="SELECT e FROM EvRepScore e")
public class EvRepScore implements Serializable, RepScore {
	private static final long serialVersionUID = 1L;
	private long id;
	private boolean imputed;
	private Double value;
	private Evidence evidenceBean;
	private Replicate replicateBean;
	private Replicate basalBean;
	private ScoreType scoreType;

	public EvRepScore() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	@Override
	public boolean getImputed() {
		return this.imputed;
	}

	public void setImputed(boolean imputed) {
		this.imputed = imputed;
	}


	@Override
	public Double getValue() {
		return this.value;
	}

	public void setValue(Double value) {
		this.value = value;
	}


	//bi-directional many-to-one association to Evidence
	@ManyToOne
	@JoinColumn(name="evidence")
	public Evidence getEvidenceBean() {
		return this.evidenceBean;
	}

	public void setEvidenceBean(Evidence evidenceBean) {
		this.evidenceBean = evidenceBean;
	}


	//bi-directional many-to-one association to Replicate
	@Override
	@ManyToOne
	@JoinColumn(name="replicate")
	public Replicate getReplicateBean() {
		return this.replicateBean;
	}

	public void setReplicateBean(Replicate replicateBean) {
		this.replicateBean = replicateBean;
	}
	
	
	//bi-directional many-to-one association to Replicate
	@Override
	@ManyToOne
	@JoinColumn(name="basal")
	public Replicate getBasalBean() {
		return this.basalBean;
	}

	public void setBasalBean(Replicate basalBean) {
		this.basalBean = basalBean;
	}


	//uni-directional many-to-one association to ScoreType
	@Override
	@ManyToOne
	@JoinColumn(name="score")
	public ScoreType getScoreType() {
		return this.scoreType;
	}

	public void setScoreType(ScoreType scoreType) {
		this.scoreType = scoreType;
	}

}