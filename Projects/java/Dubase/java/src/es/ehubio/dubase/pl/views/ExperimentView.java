package es.ehubio.dubase.pl.views;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import es.ehubio.dubase.dl.entities.Experiment;

@Named
@RequestScoped
public class ExperimentView {
	private Experiment entity;

	public Experiment getEntity() {
		return entity;
	}

	public void setEntity(Experiment entity) {
		this.entity = entity;
	}
	
	public String showExperiment(Experiment exp) {
		setEntity(exp);
		return "experiment";
	}
}
