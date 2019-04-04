package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the ScoreType database table.
 * 
 */
@Entity
@NamedQuery(name="ScoreType.findAll", query="SELECT s FROM ScoreType s")
public class ScoreType implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String description;
	private String name;
	private List<EvScore> evScores;
	private List<RepScore> repScores;

	public ScoreType() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	//bi-directional many-to-one association to EvScore
	@OneToMany(mappedBy="scoreType")
	public List<EvScore> getEvScores() {
		return this.evScores;
	}

	public void setEvScores(List<EvScore> evScores) {
		this.evScores = evScores;
	}

	public EvScore addEvScore(EvScore evScore) {
		getEvScores().add(evScore);
		evScore.setScoreType(this);

		return evScore;
	}

	public EvScore removeEvScore(EvScore evScore) {
		getEvScores().remove(evScore);
		evScore.setScoreType(null);

		return evScore;
	}


	//bi-directional many-to-one association to RepScore
	@OneToMany(mappedBy="scoreType")
	public List<RepScore> getRepScores() {
		return this.repScores;
	}

	public void setRepScores(List<RepScore> repScores) {
		this.repScores = repScores;
	}

	public RepScore addRepScore(RepScore repScore) {
		getRepScores().add(repScore);
		repScore.setScoreType(this);

		return repScore;
	}

	public RepScore removeRepScore(RepScore repScore) {
		getRepScores().remove(repScore);
		repScore.setScoreType(null);

		return repScore;
	}

}