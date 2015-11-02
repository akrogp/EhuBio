package es.ehubio.wregex;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import es.ehubio.db.fasta.Fasta;

/** Inmutable class for storing a Wregex result*/
public final class Result implements Comparable<Result> {
	private final String name;
	private final int start;
	private final int end;
	private final String match;
	private final List<String> groups;
	private final String alignment;
	private String printString = null;
	private final Fasta fasta;
	private double score = -1.0;
	private double assay = -1.0;
	private ResultGroup group = null;
	private int combinations = 0;	
	
	Result( Fasta fasta, int start, String match, Collection<String> groups ) {
		assert groups.size() > 0;
		
		this.fasta = fasta;		
		this.start = start;
		this.end = start+match.length()-1;
		this.name = fasta.getEntry()+"@"+start+"-"+end;		 		
		this.match = match;
		this.groups = new ArrayList<String>(groups);		
		
		StringBuilder builder = new StringBuilder();
		for( String str : groups )
			if( str != null )
				builder.append(str+"-");
		builder.deleteCharAt(builder.length()-1);
		this.alignment = builder.toString();				
	}
	
	void complete( ResultGroup group, double score ) {
		this.group = group;
		this.combinations = group == null ? 1 : this.group.getSize();
		this.score = score;
		this.printString = this.name + " (x" + getCombinations() + ") " + this.alignment + " score=" + score;
	}		
	
	public String getName() {
		return name;
	}
	
	public String getEntry() {
		return fasta.getEntry();
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getCombinations() {
		return combinations;
	}

	public String getMatch() {
		return match;
	}

	public String getAlignment() {
		return alignment;
	}

	public Fasta getFasta() {
		return fasta;
	}

	public double getScore() {
		return score;
	}
	
	void setAssay(double assay) {
		this.assay = assay;
	}
	
	public double getAssay() {
		return assay;
	}			
	
	public double getGroupAssay() {
		return group.getAssay();
	}

	/** returns a defensive copy of the groups */
	public List<String> getGroups() {
		return new ArrayList<String>(groups);
	}
	
	@Override
	public String toString() {
		return printString;
	}
	
	public boolean overlaps(Result result) {
		if( result.start >= start && result.start <= end )
			return true;
		if( result.end >= start && result.end <= end )
			return true;
		if( start >= result.start && start <= result.end )
			return true;
		if( end >= result.start && end <= result.end )
			return true;
		return false;
	}

	@Override
	public int compareTo(Result o) {
		if( score > o.score )
			return -1;
		if( score < o.score )
			return 1;
		if( combinations > o.combinations )
			return -1;
		if( combinations < o.combinations )
			return 1;
		if( match.length() > o.match.length() )
			return -1;
		if( match.length() < o.match.length() )
			return 1;
		return 0;
	}
	
	public static void saveAln(Writer wr, List<Result> results) {
		PrintWriter pw = new PrintWriter(wr);
		int groups = results.get(0).getGroups().size();
		int[] sizes = new int[groups];
		int first = 0, i;
		
		// Calculate lengths for further alignment
		for( i = 0; i < groups; i++ )
			sizes[i] = 0;
		for( Result result : results ) {
			if( result.getName().length() > first )
				first = result.getName().length();
			for( i = 0; i < groups; i++ )
				if( result.getGroups().get(i).length() > sizes[i] )
					sizes[i] = result.getGroups().get(i).length(); 
		}
		
		// Write ALN
		pw.println("CLUSTAL 2.1 multiple sequence alignment (by WREGEX)\n\n");
		for( Result result : results ) {
			pw.print(StringUtils.rightPad(result.getName(), first+4));
			for( i = 0; i < groups; i++ )
				pw.print(StringUtils.rightPad(result.getGroups().get(i),sizes[i],'-'));
			pw.println();
		}
		pw.println();
		pw.flush();
	}
}
