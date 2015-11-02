package es.ehubio.mymrm.data;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the IonizationType database table.
 * 
 */
@Entity
@NamedQuery(name="IonizationType.findAll", query="SELECT i FROM IonizationType i")
public class IonizationType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String description;

	private String name;

	//bi-directional many-to-one association to Experiment
	@OneToMany(mappedBy="ionizationTypeBean")
	private List<Experiment> experiments;

	public IonizationType() {
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Experiment> getExperiments() {
		return this.experiments;
	}

	public void setExperiments(List<Experiment> experiments) {
		this.experiments = experiments;
	}

	public Experiment addExperiment(Experiment experiment) {
		getExperiments().add(experiment);
		experiment.setIonizationTypeBean(this);

		return experiment;
	}

	public Experiment removeExperiment(Experiment experiment) {
		getExperiments().remove(experiment);
		experiment.setIonizationTypeBean(null);

		return experiment;
	}

}