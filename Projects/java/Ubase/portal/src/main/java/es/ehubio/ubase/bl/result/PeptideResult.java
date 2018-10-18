package es.ehubio.ubase.bl.result;

import java.util.ArrayList;
import java.util.List;

import es.ehubio.ubase.dl.entities.Experiment;
import es.ehubio.ubase.dl.entities.PeptideEvidence;

public class PeptideResult extends SearchResult {
	private PeptideEvidence pev;
	private Experiment experiment;
	private final List<ModificationResult> mods = new ArrayList<>();
	private final List<ProteinResult> prots = new ArrayList<>();
	
	public PeptideResult(PeptideEvidence pev) {
		this.pev = pev;
	}
	public Character getAfter() {
		return pev.getAfter();
	}
	public Character getPrev() {
		return pev.getPrev();
	}
	public Double getMass() {
		return pev.getMass();
	}
	public Integer getMissedCleavages() {
		return pev.getMissedCleavages();
	}
	public Boolean getUniqueGroup() {
		return pev.getUniqueGroup();
	}
	public Boolean getUniqueProtein() {
		return pev.getUniqueProtein();
	}
	public String getSequence() {
		return pev.getPeptideBean().getSequence();
	}
	public Experiment getExperiment() {
		return experiment;
	}
	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	public List<ModificationResult> getMods() {
		return mods;
	}
	public List<ProteinResult> getProts() {
		return prots;
	}
}
