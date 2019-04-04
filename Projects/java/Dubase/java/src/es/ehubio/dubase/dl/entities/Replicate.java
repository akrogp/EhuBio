package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;


/**
 * The persistent class for the Replicate database table.
 * 
 */
@Entity
@NamedQuery(name="Replicate.findAll", query="SELECT r FROM Replicate r")
public class Replicate implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private boolean control;
	private List<RepScore> repScores;
	private Evidence evidenceBean;

	public Replicate() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public boolean isControl() {
		return this.control;
	}

	public void setControl(boolean control) {
		this.control = control;
	}


	//bi-directional many-to-one association to RepScore
	@OneToMany(mappedBy="replicateBean", fetch=FetchType.EAGER)
	public List<RepScore> getRepScores() {
		return this.repScores;
	}

	public void setRepScores(List<RepScore> repScores) {
		this.repScores = repScores;
	}

	public RepScore addRepScore(RepScore repScore) {
		getRepScores().add(repScore);
		repScore.setReplicateBean(this);

		return repScore;
	}

	public RepScore removeRepScore(RepScore repScore) {
		getRepScores().remove(repScore);
		repScore.setReplicateBean(null);

		return repScore;
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

}