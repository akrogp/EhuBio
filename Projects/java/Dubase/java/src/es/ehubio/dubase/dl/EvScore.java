package es.ehubio.dubase.dl;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the EvScore database table.
 * 
 */
@Entity
@NamedQuery(name="EvScore.findAll", query="SELECT e FROM EvScore e")
public class EvScore implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private double value;
	private Evidence evidenceBean;
	private ScoreType scoreType;

	public EvScore() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public double getValue() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
	}


	//uni-directional many-to-one association to Evidence
	@ManyToOne
	@JoinColumn(name="evidence")
	public Evidence getEvidenceBean() {
		return this.evidenceBean;
	}

	public void setEvidenceBean(Evidence evidenceBean) {
		this.evidenceBean = evidenceBean;
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