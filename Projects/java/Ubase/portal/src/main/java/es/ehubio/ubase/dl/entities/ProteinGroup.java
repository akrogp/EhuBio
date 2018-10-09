package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the ProteinGroup database table.
 * 
 */
@Entity
@NamedQuery(name="ProteinGroup.findAll", query="SELECT p FROM ProteinGroup p")
public class ProteinGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String accessions;
	private String description;
	private String name;
	private List<GroupScore> groupScores;

	public ProteinGroup() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getAccessions() {
		return this.accessions;
	}

	public void setAccessions(String accessions) {
		this.accessions = accessions;
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


	//bi-directional many-to-one association to GroupScore
	@OneToMany(mappedBy="proteinGroupBean")
	public List<GroupScore> getGroupScores() {
		return this.groupScores;
	}

	public void setGroupScores(List<GroupScore> groupScores) {
		this.groupScores = groupScores;
	}

	public GroupScore addGroupScore(GroupScore groupScore) {
		getGroupScores().add(groupScore);
		groupScore.setProteinGroupBean(this);

		return groupScore;
	}

	public GroupScore removeGroupScore(GroupScore groupScore) {
		getGroupScores().remove(groupScore);
		groupScore.setProteinGroupBean(null);

		return groupScore;
	}

}