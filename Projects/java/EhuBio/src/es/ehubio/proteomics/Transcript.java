package es.ehubio.proteomics;

import java.util.HashSet;
import java.util.Set;

public class Transcript extends ProteinGroup {

	public Transcript() {
		id = idCount++;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public Set<Protein> getProteins() {
		return proteins;
	}
		
	public boolean linkProtein( Protein protein ) {
		if( proteins.add(protein) ) {
			protein.linkTranscript(this);
			return true;
		}
		return false;
	}

	public Gene getGene() {
		return gene;
	}

	public void linkGene(Gene gene) {
		this.gene = gene;
		if( gene != null  )		
			gene.linkTranscript(this);
	}
	
	private static int idCount = 1;
	private final int id;
	private Gene gene;
	private final Set<Protein> proteins = new HashSet<>();
}