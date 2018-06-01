package es.ehubio.wregex.data;

import java.util.List;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.wregex.Result;

public class CachedResult extends ResultEx {
	private String alignement;
	private int combinations;
	private int end;
	private String entry;
	private Fasta fasta;
	private List<String> groups;
	private String match;
	private String sequence;
	private String name;
	private double score;
	private int start;
	private String string;
	private String accession;
	private String gene;	

	public CachedResult() {
		super((Result)null);
	}
	
	@Override
	public String getAlignment() {
		return alignement;
	}
	
	@Override
	public int getCombinations() {
		return combinations;
	}
	
	@Override
	public int getEnd() {
		return end;
	}
	
	@Override
	public String getEntry() {
		return entry;
	}
	
	@Override
	public Fasta getFasta() {
		return fasta;
	}
	
	@Override
	public List<String> getGroups() {
		return groups;
	}
	
	@Override
	public String getMatch() {
		return match;
	}
	
	@Override
	public String getSequence() {
		return sequence;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public double getScore() {
		return score;
	}
	
	@Override
	public int getStart() {
		return start;
	}
	
	@Override
	public String toString() {
		return string;
	}
	
	@Override
	public String getAccession() {
		return accession;
	}
	
	@Override
	public String getGene() {
		return gene;
	}
	
	public void setAlignement(String alignement) {
		this.alignement = alignement;
	}

	public void setCombinations(int combinations) {
		this.combinations = combinations;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public void setFasta(Fasta fasta) {
		this.fasta = fasta;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public void setMatch(String match) {
		this.match = match;
	}
	
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setString(String string) {
		this.string = string;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}	
}
