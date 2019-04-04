package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Enzyme database table.
 * 
 */
@Entity
@NamedQuery(name="Enzyme.findAll", query="SELECT e FROM Enzyme e")
public class Enzyme implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String description;
	private String gene;
	private Superfamily superfamilyBean;
	private List<Experiment> experiments;

	public Enzyme() {
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


	public String getGene() {
		return this.gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}


	//bi-directional many-to-one association to Superfamily
	@ManyToOne
	@JoinColumn(name="superfamily")
	public Superfamily getSuperfamilyBean() {
		return this.superfamilyBean;
	}

	public void setSuperfamilyBean(Superfamily superfamilyBean) {
		this.superfamilyBean = superfamilyBean;
	}


	//bi-directional many-to-one association to Experiment
	@OneToMany(mappedBy="enzymeBean")
	public List<Experiment> getExperiments() {
		return this.experiments;
	}

	public void setExperiments(List<Experiment> experiments) {
		this.experiments = experiments;
	}

	public Experiment addExperiment(Experiment experiment) {
		getExperiments().add(experiment);
		experiment.setEnzymeBean(this);

		return experiment;
	}

	public Experiment removeExperiment(Experiment experiment) {
		getExperiments().remove(experiment);
		experiment.setEnzymeBean(null);

		return experiment;
	}

}