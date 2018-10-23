package es.ehubio.ubase.bl.result;

import es.ehubio.ubase.dl.entities.Protein;

public class ProteinResult extends SearchResult {
	private final Protein protein;
	private int position;
	
	public ProteinResult(Protein protein) {
		this.protein = protein;
	}
	public String getAccession() {
		return protein.getAccession();
	}
	public String getEntry() {
		return protein.getEntry();
	}
	public String getGene() {
		return protein.getGene();
	}
	public String getName() {
		return protein.getName();
	}
	public String getSequence() {
		return protein.getSequence();
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
}
