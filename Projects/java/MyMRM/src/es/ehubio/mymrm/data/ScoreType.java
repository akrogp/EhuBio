package es.ehubio.mymrm.data;

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

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String description;

	private boolean largerBetter;

	private String name;

	//bi-directional many-to-one association to Score
	@OneToMany(mappedBy="scoreType")
	private List<Score> scores;

	public ScoreType() {
	}

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

	public boolean getLargerBetter() {
		return this.largerBetter;
	}

	public void setLargerBetter(boolean largerBetter) {
		this.largerBetter = largerBetter;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Score> getScores() {
		return this.scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	public Score addScore(Score score) {
		getScores().add(score);
		score.setScoreType(this);

		return score;
	}

	public Score removeScore(Score score) {
		getScores().remove(score);
		score.setScoreType(null);

		return score;
	}

}