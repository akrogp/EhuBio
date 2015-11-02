package es.ehubio.mymrm.presentation;

import java.io.Serializable;

import es.ehubio.proteomics.Peptide;

public class CandidateBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Peptide peptide;
	private boolean available = false;
	
	public Peptide getPeptide() {
		return peptide;
	}
	
	public void setPeptide(Peptide peptide) {
		this.peptide = peptide;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}
}
