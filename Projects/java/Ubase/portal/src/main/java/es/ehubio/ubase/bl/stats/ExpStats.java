package es.ehubio.ubase.bl.stats;

import java.util.HashMap;
import java.util.Map;

import es.ehubio.ubase.dl.entities.Experiment;

public class ExpStats {
	private Experiment experiment;
	private final Map<Integer, ModStats> modStats = new HashMap<>();
	public Experiment getExperiment() {
		return experiment;
	}
	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	public Map<Integer, ModStats> getModStats() {
		return modStats;
	}
}
