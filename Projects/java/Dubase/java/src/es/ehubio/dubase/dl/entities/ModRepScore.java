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
 * The persistent class for the ModRepScore database table.
 * 
 */
@Entity
@NamedQuery(name="ModRepScore.findAll", query="SELECT m FROM ModRepScore m")
public class ModRepScore implements Serializable, RepScore {
	private static final long serialVersionUID = 1L;
	private long id;
	private boolean imputed;
	private Double value;
	private Modification modificationBean;
	private Replicate replicateBean;
	private Replicate basalBean;
	private ScoreType scoreType;

	public ModRepScore() {
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


	//bi-directional many-to-one association to Modification
	@ManyToOne
	@JoinColumn(name="modification")
	public Modification getModificationBean() {
		return this.modificationBean;
	}

	public void setModificationBean(Modification modificationBean) {
		this.modificationBean = modificationBean;
	}


	//uni-directional many-to-one association to Replicate
	@Override
	@ManyToOne
	@JoinColumn(name="replicate")
	public Replicate getReplicateBean() {
		return this.replicateBean;
	}

	public void setReplicateBean(Replicate replicateBean) {
		this.replicateBean = replicateBean;
	}
	
	
	//uni-directional many-to-one association to Replicate
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