package es.ehubio.dubase.dl;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the RepScore database table.
 * 
 */
@Entity
@NamedQuery(name="RepScore.findAll", query="SELECT r FROM RepScore r")
public class RepScore implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private boolean imputed;
	private Replicate replicateBean;
	private ScoreType scoreType;

	public RepScore() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public boolean getImputed() {
		return this.imputed;
	}

	public void setImputed(boolean imputed) {
		this.imputed = imputed;
	}


	//uni-directional many-to-one association to Replicate
	@ManyToOne
	@JoinColumn(name="replicate")
	public Replicate getReplicateBean() {
		return this.replicateBean;
	}

	public void setReplicateBean(Replicate replicateBean) {
		this.replicateBean = replicateBean;
	}


	//uni-directional many-to-one association to ScoreType
	@ManyToOne
	@JoinColumn(name="score")
	public ScoreType getScoreType() {
		return this.scoreType;
	}

	public void setScoreType(ScoreType scoreType) {
		this.scoreType = scoreType;
	}

}