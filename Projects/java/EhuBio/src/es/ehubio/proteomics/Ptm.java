package es.ehubio.proteomics;

import es.ehubio.model.ProteinModification;
import es.ehubio.model.ProteinModificationType;

/**
 * Post-Translational Mofidication in a MS/MS proteomics experiment.
 * 
 * @author gorka
 *
 */
public class Ptm extends ProteinModification {	
	private Double massDelta;
		
	public Double getMassDelta() {
		return massDelta;
	}

	public void setMassDelta(Double massDelta) {
		this.massDelta = massDelta;
	}
	
	public void guessMissing( String seq ) {
		if( getType() == null && getName() != null )
			setType(ProteinModificationType.guessFromName(getName()));
		if( getType() == null && getMassDelta() != null )
			setType(ProteinModificationType.guessFromMass(getMassDelta()));
		if( getMassDelta() == null && getType() != null )
			setMassDelta(getType().getMass());
		if( getResidues() == null && seq != null && getFrom() != null && getTo() != null )
			setResidues(seq.substring(getFrom()-1, getTo()));
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		if( getName() != null )
			str.append(getName());
		if( getResidues() != null ) {
			if( str.length() > 0 )
				str.append('+');
			str.append(getResidues());
		}
		if( getPosition() != null )
			str.append(String.format("(%d)", getPosition()));
		return str.toString();
	}
}
