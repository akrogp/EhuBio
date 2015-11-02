package es.ehubio.proteomics;

import java.util.HashSet;
import java.util.Set;

import es.ehubio.Strings;

public class AmbiguityGroup extends ProteinGroup {
	public AmbiguityGroup() {
		id = idCount++;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	protected String buildUniqueString() {
		return ""+id;
	}
	
	public String buildName() {
		Set<String> names = new HashSet<>();
		for( AmbiguityItem item : getItems() )
			names.add(item.getAccession());
		setName(Strings.merge(names));
		return getName();
	}

	public Set<AmbiguityItem> getItems() {
		return items;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<Protein> getProteins() {		
		AmbiguityItem item = getItems().iterator().next();
		Set<? extends AmbiguityItem> items = (Set<? extends AmbiguityItem>)getItems();
		if( item instanceof Protein )
			return (Set<Protein>)items;
		else if( item instanceof Gene ) {
			Set<Protein> proteins = new HashSet<>();
			for( AmbiguityItem gene : items )
				proteins.addAll(((Gene)gene).getProteins());
			return proteins;
		}
		return null;
	}
	
	public boolean linkItem( AmbiguityItem item ) {
		if( items.add(item) ) {
			item.linkGroup(this);
			return true;
		}
		return false;
	}
	
	public AmbiguityItem firstItem() {
		if( items.isEmpty() )
			return null;
		return items.iterator().next();
	}
	
	public AmbiguityItem getBestItem( ScoreType type ) {
		return getBest(getItems(), type);
	}
	
	public int size() {
		return items.size();
	}
	
	public AmbiguityItem.Confidence getConfidence() {
		return firstItem().getConfidence();
	}
	
	@Override
	public Boolean getDecoy() {
		return getDecoy(getItems());
	}
	
	@Override
	public void setDecoy(Boolean decoy) {
		setDecoy(decoy, getItems());
	}
	
	@Override
	public boolean skipFdr() {
		return getConfidence() == null || getConfidence() == AmbiguityItem.Confidence.NON_CONCLUSIVE;
	}
	
	public Set<AmbiguityPart> getParts() {
		Set<AmbiguityPart> part = new HashSet<>();
		for( AmbiguityItem item : getItems() )
			part.addAll(item.getAmbiguityParts());
		return part;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<Peptide> getPeptides() {
		return (Set<Peptide>)(Set<? extends AmbiguityPart>)getParts();
	}

	public AmbiguityPart getBestPart( ScoreType type ) {
		return getBest(getParts(), type);
	}
	
	public Set<AmbiguityPart> getOwnParts() {
		Set<AmbiguityPart> parts = new HashSet<>();
		for( AmbiguityItem item : getItems() )
			for( AmbiguityPart part : item.getAmbiguityParts() )
				if( part.getConfidence() != AmbiguityPart.Confidence.NON_DISCRIMINATING )
					parts.add(part);
		return parts;
	}
	
	public AmbiguityPart getBestOwnPart( ScoreType type ) {
		return getBest(getOwnParts(), type);
	}
	
	@Override
	public Score getScoreByType(ScoreType type) {
		Score score = super.getScoreByType(type);
		if( score != null )
			return score;
		
		DecoyBase best = getBestOwnPart(type);
		if( best != null )
			return best.getScoreByType(type);
				
		best = getBestItem(type);
		if( best != null )
			return best.getScoreByType(type);
		
		return null;
	}

	private static int idCount = 1;
	private final int id;
	private final Set<AmbiguityItem> items = new HashSet<>();
}
