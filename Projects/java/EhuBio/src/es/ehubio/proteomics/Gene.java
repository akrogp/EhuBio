package es.ehubio.proteomics;

import java.util.HashSet;
import java.util.Set;

public class Gene extends ProteinGroup {

	public Gene() {
		id = idCount++;
	}
	
	public int getId() {
		return id;
	}
	
	public Set<Transcript> getTranscripts() {
		return transcripts;
	}
	
	public boolean linkTranscript( Transcript transcript ) {
		if( transcripts.add(transcript) ) {
			if( transcript.getGene() != this )
				transcript.linkGene(this);
			return true;
		}
		return false;
	}

	@Override
	public Set<Protein> getProteins() {
		Set<Protein> proteins = new HashSet<>();
		for( Transcript transcript : transcripts )
			proteins.addAll(transcript.getProteins());
		return proteins;
	}
		
	private static int idCount = 1;
	private final int id;
	private final Set<Transcript> transcripts = new HashSet<>();
}
