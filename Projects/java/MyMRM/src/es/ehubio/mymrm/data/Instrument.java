package es.ehubio.mymrm.data;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Instrument database table.
 * 
 */
@Entity
@NamedQuery(name="Instrument.findAll", query="SELECT i FROM Instrument i")
public class Instrument implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String description;

	private String name;

	//bi-directional many-to-one association to Experiment
	@OneToMany(mappedBy="instrumentBean")
	private List<Experiment> experiments;

	//bi-directional many-to-one association to InstrumentType
	@ManyToOne
	@JoinColumn(name="instrumentType")
	private InstrumentType instrumentTypeBean;

	public Instrument() {
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
		experiment.setInstrumentBean(this);

		return experiment;
	}

	public Experiment removeExperiment(Experiment experiment) {
		getExperiments().remove(experiment);
		experiment.setInstrumentBean(null);

		return experiment;
	}

	public InstrumentType getInstrumentTypeBean() {
		return this.instrumentTypeBean;
	}

	public void setInstrumentTypeBean(InstrumentType instrumentTypeBean) {
		this.instrumentTypeBean = instrumentTypeBean;
	}

}