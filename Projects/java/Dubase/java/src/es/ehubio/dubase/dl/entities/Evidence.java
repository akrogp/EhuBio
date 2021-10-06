package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import es.ehubio.dubase.dl.input.ScoreType;


/**
 * The persistent class for the Evidence database table.
 * 
 */
@Entity
@NamedQuery(name="Evidence.findAll", query="SELECT e FROM Evidence e")
public class Evidence implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private List<Ambiguity> ambiguities;
	private List<EvScore> evScores;
	private Experiment experimentBean;
	private List<EvRepScore> evRepScores;

	public Evidence() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	//bi-directional many-to-one association to Ambiguity
	@OneToMany(mappedBy="evidenceBean")
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<Ambiguity> getAmbiguities() {
		return this.ambiguities;
	}

	public void setAmbiguities(List<Ambiguity> ambiguities) {
		this.ambiguities = ambiguities;
	}

	public Ambiguity addAmbiguity(Ambiguity ambiguity) {
		getAmbiguities().add(ambiguity);
		ambiguity.setEvidenceBean(this);

		return ambiguity;
	}

	public Ambiguity removeAmbiguity(Ambiguity ambiguity) {
		getAmbiguities().remove(ambiguity);
		ambiguity.setEvidenceBean(null);

		return ambiguity;
	}


	//bi-directional many-to-one association to EvScore
	@OneToMany(mappedBy="evidenceBean")
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<EvScore> getEvScores() {
		return this.evScores;
	}

	public void setEvScores(List<EvScore> evScores) {
		this.evScores = evScores;
	}

	public EvScore addEvScore(EvScore evScore) {
		getEvScores().add(evScore);
		evScore.setEvidenceBean(this);

		return evScore;
	}

	public EvScore removeEvScore(EvScore evScore) {
		getEvScores().remove(evScore);
		evScore.setEvidenceBean(null);

		return evScore;
	}


	//uni-directional many-to-one association to Experiment
	@ManyToOne
	@JoinColumn(name="experiment")
	public Experiment getExperimentBean() {
		return this.experimentBean;
	}

	public void setExperimentBean(Experiment experimentBean) {
		this.experimentBean = experimentBean;
	}


	//bi-directional many-to-one association to EvRepScore
	@OneToMany(mappedBy="evidenceBean")
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<EvRepScore> getRepScores() {
		return this.evRepScores;
	}

	public void setRepScores(List<EvRepScore> evRepScores) {
		this.evRepScores = evRepScores;
	}

	public EvRepScore addRepScore(EvRepScore evRepScore) {
		getRepScores().add(evRepScore);
		evRepScore.setEvidenceBean(this);

		return evRepScore;
	}

	public EvRepScore removeRepScore(EvRepScore evRepScore) {
		getRepScores().remove(evRepScore);
		evRepScore.setEvidenceBean(null);

		return evRepScore;
	}

	@Transient
	public List<String> getGenes() {
		return getAmbiguities().stream().map(a->a.getProteinBean().getGeneBean().getName()).distinct().collect(Collectors.toList());
	}
	
	@Transient
	public List<String> getProteins() {
		return getAmbiguities().stream().map(a->a.getProteinBean().getAccession()).distinct().collect(Collectors.toList());
	}
	
	@Transient
	public List<Protein> getProteinBeans() {
		return getAmbiguities().stream().map(a->a.getProteinBean()).collect(Collectors.toList());
	}
	
	@Transient
	public List<String> getDescriptions() {
		return getAmbiguities().stream().map(a->a.getProteinBean().getDescription()).distinct().collect(Collectors.toList());
	}
	
	public Double getScore(ScoreType type) {
		if( getEvScores() == null || getEvScores().isEmpty() )
			return null;
		EvScore s = getEvScores().stream()
			.filter(score->score.getScoreType().getId() == type.ordinal())
			.findFirst().orElse(null);
		return s == null ? null : s.getValue();
	}
}