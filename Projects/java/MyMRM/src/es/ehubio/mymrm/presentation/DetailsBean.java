package es.ehubio.mymrm.presentation;

import java.util.ArrayList;
import java.util.List;

import es.ehubio.mymrm.data.Experiment;
import es.ehubio.mymrm.data.PeptideEvidence;
import es.ehubio.mymrm.data.Precursor;
import es.ehubio.mymrm.data.Score;

public class DetailsBean {
	private PeptideEvidence evidence;
	private Experiment experiment;
	private Precursor precursor;
	private final List<FragmentBean> fragments = new ArrayList<>();
	private final List<Score> scores = new ArrayList<>();

	public List<FragmentBean> getFragments() {
		return fragments;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public Precursor getPrecursor() {
		return precursor;
	}

	public void setPrecursor(Precursor precursor) {
		this.precursor = precursor;
	}

	public List<Score> getScores() {
		return scores;
	}

	public PeptideEvidence getEvidence() {
		return evidence;
	}

	public void setEvidence(PeptideEvidence evidence) {
		this.evidence = evidence;
	}
}
