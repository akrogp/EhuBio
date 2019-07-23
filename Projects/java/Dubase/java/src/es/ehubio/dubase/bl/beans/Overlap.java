package es.ehubio.dubase.bl.beans;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Overlap implements Comparable<Overlap> {	
	private String gene;
	private final Set<String> enzymes = new TreeSet<>(new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			int cmp = o1.length() - o2.length();
			if( cmp == 0 )
				cmp = o1.compareTo(o2);
			return cmp;
		}
	});
	
	public String getGene() {
		return gene;
	}
	
	public void setGene(String gene) {
		this.gene = gene;
	}
	
	public Set<String> getEnzymes() {
		return enzymes;
	}

	@Override
	public int compareTo(Overlap o) {
		int cmp = o.getEnzymes().size() - getEnzymes().size();
		if( cmp == 0 )
			cmp = getGene().compareTo(o.getGene());
		return cmp;
	}
}
