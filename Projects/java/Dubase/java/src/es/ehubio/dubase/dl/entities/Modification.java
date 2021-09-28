package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


/**
 * The persistent class for the Modification database table.
 * 
 */
@Entity
@NamedQuery(name="Modification.findAll", query="SELECT m FROM Modification m")
public class Modification implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private int position;
	private Ambiguity ambiguityBean;
	private ModType modType;
	private List<ModRepScore> modRepScores;
	private List<ModScore> modScores;

	public Modification() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public int getPosition() {
		return this.position;
	}

	public void setPosition(int position) {
		this.position = position;
	}


	//bi-directional many-to-one association to Ambiguity
	@ManyToOne
	@JoinColumn(name="ambiguity")
	public Ambiguity getAmbiguityBean() {
		return this.ambiguityBean;
	}

	public void setAmbiguityBean(Ambiguity ambiguityBean) {
		this.ambiguityBean = ambiguityBean;
	}


	//uni-directional many-to-one association to ModType
	@ManyToOne
	@JoinColumn(name="type")
	public ModType getModType() {
		return this.modType;
	}

	public void setModType(ModType modType) {
		this.modType = modType;
	}


	//bi-directional many-to-one association to ModRepScore
	@OneToMany(mappedBy="modificationBean")
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<ModRepScore> getRepScores() {
		return this.modRepScores;
	}

	public void setRepScores(List<ModRepScore> modRepScores) {
		this.modRepScores = modRepScores;
	}

	public ModRepScore addRepScore(ModRepScore modRepScore) {
		getRepScores().add(modRepScore);
		modRepScore.setModificationBean(this);

		return modRepScore;
	}

	public ModRepScore removeRepScore(ModRepScore modRepScore) {
		getRepScores().remove(modRepScore);
		modRepScore.setModificationBean(null);

		return modRepScore;
	}


	//bi-directional many-to-one association to ModScore
	@OneToMany(mappedBy="modificationBean")
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<ModScore> getScores() {
		return this.modScores;
	}

	public void setScores(List<ModScore> modScores) {
		this.modScores = modScores;
	}

	public ModScore addScore(ModScore modScore) {
		getScores().add(modScore);
		modScore.setModificationBean(this);

		return modScore;
	}

	public ModScore removeScore(ModScore modScore) {
		getScores().remove(modScore);
		modScore.setModificationBean(null);

		return modScore;
	}

}