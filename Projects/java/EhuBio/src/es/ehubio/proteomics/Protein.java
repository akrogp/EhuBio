package es.ehubio.proteomics;

import java.util.HashSet;
import java.util.Set;

import es.ehubio.db.fasta.Fasta;

/**
 * Protein in a MS/MS proteomics experiment with PAnalyzer confidence category.
 * 
 * @author gorka
 *
 */
public class Protein extends AmbiguityItem {
	private static int idCount = 1;
	private final int id;
	private String sequence;
	private final Set<Peptide> peptides = new HashSet<>();
	private final Set<Transcript> transcripts = new HashSet<Transcript>();

	public Protein() {
		id = idCount++;
	}

	public int getId() {
		return id;
	}
	
	public void setFasta( Fasta fasta ) {
		if( fasta == null )
			return;
		setAccession(fasta.getAccession());
		setDescription(fasta.getDescription());
		setName(fasta.getProteinName());
		setSequence(fasta.getSequence());
	}
	
	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	public Set<Peptide> getPeptides() {
		return peptides;
	}

	public boolean linkPeptide( Peptide peptide ) {
		if( peptides.add(peptide) ) {
			peptide.linkProtein(this);
			return true;
		}
		return false;
	}
	
	public Psm getBestPsm( ScoreType type ) {		
		return getBest(getPsms(), type);
	}

	public Set<Psm> getPsms() {
		Set<Psm> psms = new HashSet<>();
		for( Peptide peptide : getPeptides() )
			psms.addAll(peptide.getPsms());
		return psms;
	}
	
	@Override
	public Score getScoreByType(ScoreType type) {
		Score score = super.getScoreByType(type);
		if( score != null )
			return score;
		
		Psm bestPsm = getBestPsm(type);
		if( bestPsm != null )
			return bestPsm.getScoreByType(type);
		
		return null;
	}

	public Set<Transcript> getTranscripts() {
		return transcripts;
	}
	
	public boolean linkTranscript( Transcript transcript ) {
		if( transcripts.add(transcript) ) {
			transcript.linkProtein(this);
			return true;
		}
		return false;
	}
	
	@Override
	public Boolean getDecoy() {
		return getDecoy(getPeptides());
	}
	
	@Override
	public void setDecoy(Boolean decoy) {
		setDecoy(decoy, getPeptides());
	}
}