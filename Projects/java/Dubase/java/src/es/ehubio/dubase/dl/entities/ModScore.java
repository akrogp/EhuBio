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
 * The persistent class for the ModScore database table.
 * 
 */
@Entity
@NamedQuery(name="ModScore.findAll", query="SELECT m FROM ModScore m")
public class ModScore implements Serializable, Score {
	private static final long serialVersionUID = 1L;
	private long id;
	private Double value;
	private Modification modificationBean;
	private ScoreType scoreType;

	public ModScore() {
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