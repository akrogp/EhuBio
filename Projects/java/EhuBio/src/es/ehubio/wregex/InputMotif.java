package es.ehubio.wregex;

import java.io.Serializable;

import es.ehubio.db.fasta.Fasta;

public final class InputMotif implements Serializable {
	private static final long serialVersionUID = 1L;
	private int start;	
	private int end;
	private double weight;
	public final Fasta fasta;
	private int matches = 0;

	public InputMotif(Fasta fasta, int start, int end, double weight) {
		this.fasta = fasta;
		this.start = start;
		this.end = end;
		this.weight = weight;
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	public String getMotif() {
		return fasta.getSequence().substring(start-1, end);
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public String getId() {
		return fasta.getEntry();
	}
	
	public boolean contains(Result result) {
		if( result.getStart() >= start && result.getStart() <= end )
			return true;
		if( result.getEnd() >= start && result.getEnd() <= end )
			return true;
		if( start >= result.getStart() && start <= result.getEnd() )
			return true;
		if( end >= result.getStart() && end <= result.getEnd() )
			return true;
		return false;
	}

	public int getMatches() {
		return matches;
	}

	public void setMatches(int matches) {
		this.matches = matches;
	}
	
	public void addMatch() {
		matches++;
	}
}
