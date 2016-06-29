package es.ehubio.db.ensembl;

import java.util.ArrayList;
import java.util.List;

public class EnsemblIds {
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	public List<String> getTranscripts() {
		if( transcripts == null )
			transcripts = new ArrayList<>();
		return transcripts;
	}
	public List<String> getProteins() {
		if( proteins == null )
			proteins = new ArrayList<>();
		return proteins;
	}
	private String gene;
	private List<String> transcripts;
	private List<String> proteins;
}
