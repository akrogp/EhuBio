package es.ehubio.mymrm.presentation;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import es.ehubio.mymrm.data.Experiment;

@ManagedBean
@SessionScoped
public class FilesMB implements Serializable {
	private static final long serialVersionUID = 1L;
	private Experiment experiment;
	
	public String setExperiment( Experiment experiment ) {
		this.experiment = experiment;		
		return "experiment";
	}
	
	public Experiment getExperiment() {
		return experiment;
	}
}
