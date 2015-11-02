package es.ehubio.proteomics;

import java.util.HashSet;
import java.util.Set;

public abstract class AmbiguityPart extends DecoyBase {

	public enum Confidence {
		UNIQUE(0), DISCRIMINATING(1), NON_DISCRIMINATING(2);
		
		private Confidence( int order ) {
			this.order = order;
		}
		public int getOrder() {
			return order;
		}
		private final int order;
	}

	public AmbiguityPart.Confidence getConfidence() {
		return confidence;
	}

	public void setConfidence(AmbiguityPart.Confidence confidence) {
		this.confidence = confidence;
	}
	
	public Set<AmbiguityItem> getAmbiguityItems() {
		return items;
	}
	
	public boolean linkAmbiguityItem(AmbiguityItem item) {
		if( items.add(item) ) {
			item.linkAmbiguityPart(this);
			return true;
		}
		return false;
	}
	
	public abstract Set<String> getReplicates();

	private AmbiguityPart.Confidence confidence;
	private final Set<AmbiguityItem> items = new HashSet<>();	
}
