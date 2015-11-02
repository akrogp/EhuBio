package es.ehubio.proteomics;

import java.util.HashSet;
import java.util.Set;

import es.ehubio.db.DbItem;

public abstract class AmbiguityItem extends DecoyBase implements DbItem {	
	public enum Confidence {
		CONCLUSIVE, INDISTINGUISABLE_GROUP, AMBIGUOUS_GROUP, NON_CONCLUSIVE 
	}
	
	@Override
	public String toString() {
		return getAccession();
	}
	
	@Override
	protected String buildUniqueString() {
		return getAccession();
	}
	
	@Override
	public String getAccession() {
		return accession;
	}

	@Override
	public void setAccession(String accession) {
		this.accession = accession;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	public Confidence getConfidence() {
		return confidence;
	}

	public void setConfidence(Confidence confidence) {
		this.confidence = confidence;
	}

	public AmbiguityGroup getGroup() {
		return group;
	}

	public void linkGroup(AmbiguityGroup group) {
		if( this.group == group )
			return;
		this.group = group;
		if( group != null )
			group.linkItem(this);
	}

	public Set<AmbiguityPart> getAmbiguityParts() {
		return parts;
	}
	
	public boolean linkAmbiguityPart( AmbiguityPart part ) {
		if( parts.add(part) ) {
			part.linkAmbiguityItem(this);
			return true;
		}
		return false;
	}
	
	protected Boolean getDecoy( Set<? extends DecoyBase> parts ) {
		if( parts.isEmpty() )
			return null;
		
		boolean nullDecoy = false;
		for( DecoyBase part : parts )
			if( Boolean.FALSE.equals(part.getDecoy()) )
				return false;
			else if( part.getDecoy() == null )
				nullDecoy = true;
		return nullDecoy ? null : true;
	}
	
	protected void setDecoy( Boolean decoy, Set<? extends DecoyBase> parts ) {
		for( DecoyBase part : parts )
			part.setDecoy(decoy);
	}
	
	@Override
	public Boolean getDecoy() {
		return getDecoy(getAmbiguityParts());
	}
	
	@Override
	public void setDecoy(Boolean decoy) {
		setDecoy(decoy, getAmbiguityParts());
	}
	
	public AmbiguityPart getBestPart( ScoreType type ) {		
		return getBest(getAmbiguityParts(), type);
	}	

	@Override
	public Score getScoreByType(ScoreType type) {
		Score score = super.getScoreByType(type);
		if( score != null )
			return score;
		
		AmbiguityPart bestPart = getBestPart(type);
		if( bestPart != null )
			return bestPart.getScoreByType(type);
		
		return null;
	}
	
	public Set<String> getReplicates() {
		Set<String> set = new HashSet<>();
		for( AmbiguityPart peptide : getAmbiguityParts() )
			set.addAll(peptide.getReplicates());
		return set;
	}

	private String accession;
	private String name;
	private String description;
	private Confidence confidence;
	private AmbiguityGroup group;
	private final Set<AmbiguityPart> parts = new HashSet<>();
}
