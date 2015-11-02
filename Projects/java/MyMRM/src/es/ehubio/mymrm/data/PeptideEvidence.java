package es.ehubio.mymrm.data;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the PeptideEvidence database table.
 * 
 */
@Entity
@NamedQuery(name="PeptideEvidence.findAll", query="SELECT p FROM PeptideEvidence p")
public class PeptideEvidence implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	//bi-directional many-to-one association to Experiment
	@ManyToOne
	@JoinColumn(name="experiment")
	private Experiment experimentBean;

	//bi-directional many-to-one association to Peptide
	@ManyToOne
	@JoinColumn(name="peptide")
	private Peptide peptideBean;

	//bi-directional many-to-one association to Precursor
	@ManyToOne
	@JoinColumn(name="precursor")
	private Precursor precursorBean;

	//bi-directional many-to-one association to Score
	@OneToMany(mappedBy="peptideEvidenceBean")
	private List<Score> scores;

	public PeptideEvidence() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Experiment getExperimentBean() {
		return this.experimentBean;
	}

	public void setExperimentBean(Experiment experimentBean) {
		this.experimentBean = experimentBean;
	}

	public Peptide getPeptideBean() {
		return this.peptideBean;
	}

	public void setPeptideBean(Peptide peptideBean) {
		this.peptideBean = peptideBean;
	}

	public Precursor getPrecursorBean() {
		return this.precursorBean;
	}

	public void setPrecursorBean(Precursor precursorBean) {
		this.precursorBean = precursorBean;
	}

	public List<Score> getScores() {
		return this.scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	public Score addScore(Score score) {
		getScores().add(score);
		score.setPeptideEvidenceBean(this);

		return score;
	}

	public Score removeScore(Score score) {
		getScores().remove(score);
		score.setPeptideEvidenceBean(null);

		return score;
	}

}