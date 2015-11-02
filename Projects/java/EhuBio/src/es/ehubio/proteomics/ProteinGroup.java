package es.ehubio.proteomics;

import java.util.HashSet;
import java.util.Set;

public abstract class ProteinGroup extends AmbiguityItem {
	@Override
	protected String buildUniqueString() {
		return getAccession();
	}
	
	public abstract Set<Protein> getProteins();
	
	public Set<Peptide> getPeptides() {
		Set<Peptide> peptides = new HashSet<>();
		for( Protein protein : getProteins() )
			peptides.addAll(protein.getPeptides());
		return peptides;
	}
	
	@Override
	public Boolean getDecoy() {
		return getDecoy(getProteins());
	}
	
	@Override
	public void setDecoy(Boolean decoy) {
		setDecoy(decoy, getProteins());
	}
}
