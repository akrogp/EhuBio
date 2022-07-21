package es.ehubio.wregex.view;

import java.util.List;

import es.ehubio.wregex.Pssm;
import es.ehubio.wregex.data.MotifDefinition;
import es.ehubio.wregex.data.MotifInformation;
import es.ehubio.wregex.data.MotifReference;

public class MotifBean {
	private String motif;
	private String definition;
	private MotifInformation motifInformation;
	private MotifDefinition motifDefinition;
	private boolean custom = false;
	private String customRegex;
	private String customPssmFile;
	private Pssm pssm = null;
	
	public List<MotifDefinition> getDefinitions() {
		return motifInformation == null ? null : motifInformation.getDefinitions();
	}
		
	public String getRegex() {
		return motifDefinition == null || motifInformation == null ? null : motifDefinition.getRegex();
	}
		
	public String getPssmFile() {
		return motifDefinition == null ? null : motifDefinition.getPssm();
	}
		
	public String getSummary() {
		return motifDefinition == null || motifInformation == null ? null : motifInformation.getSummary();
	}
		
	public String getDescription() {
		return motifDefinition == null || motifInformation == null ? null : motifDefinition.getDescription();
	}
		
	public List<MotifReference> getReferences() {
		return motifDefinition == null || motifInformation == null ? null : motifInformation.getReferences();
	}
	
	public String getMotif() {
		return motif;
	}
	
	public MotifInformation getMotifInformation() {
		return motifInformation;
	}
	
	public MotifDefinition getMotifDefinition() {
		return motifDefinition;
	}
	
	public void setMotif(String motif) {
		this.motif = motif;
	}

	public String getConfiguration() {
		return definition;
	}

	public void setConfiguration(String configuration) {
		this.definition = configuration;
	}
	
	public void setMotifInformation( MotifInformation newMotif ) {
		motifInformation = newMotif;
		if( motifInformation == null ) {
			motifDefinition = null;
			setConfiguration("Default");
		} else {
			motifDefinition = motifInformation.getDefinitions().get(0);
			setConfiguration(motifDefinition.toString());
		}
		pssm = null;
	}
		
	public void setMotifDefinition( MotifDefinition newDefinition ) {
		motifDefinition = newDefinition;
		pssm = null;
	}
	
	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}
	
	public String getCustomRegex() {
		return customRegex;
	}

	public void setCustomRegex(String customRegex) {
		this.customRegex = customRegex;
	}

	public String getCustomPssmFile() {
		return customPssmFile;
	}

	public void setCustomPssmFile(String customPssm) {
		this.customPssmFile = customPssm;
	}
	
	public Pssm getPssm() {
		return pssm;
	}
	
	public void setPssm(Pssm pssm) {
		this.pssm = pssm;
	}	
}
