package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.io.UrlBuilder;

@Named
@SessionScoped
public class VolcanoView implements Serializable {
	static final long serialVersionUID = 1L;
	private Experiment experiment;
	@Inject
	private PrefView prefs;
	
	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
		
	public String getGene() {
		return experiment.getEnzymeBean().getGene();
	}
	
	public Thresholds getThresholds() {
		return prefs.getThresholds(experiment);
	}
		
	public String plot() {
		return "volcano";
	}
	
	public String getDataUrl() {
		try {
			return new UrlBuilder("rest/analyze")
				.path(String.valueOf(experiment.getId()))
				.pathf("%s.json", getGene())
				.param("xth", 0)
				.param("yth", 0)
				.build();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
