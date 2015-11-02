package es.ehubio.db.cosmic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import es.ehubio.model.Aminoacid;

public class Locus {
	private int position;
	private Aminoacid original;
	private Map<Aminoacid, Integer> counts = new HashMap<>();
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public Aminoacid getOriginal() {
		return original;
	}

	public void setOriginal(Aminoacid aa) {
		this.original = aa;
	}
	
	public Set<Aminoacid> getMutations() {
		return counts.keySet();
	}

	public int getMutationCount(Aminoacid aa) {
		Integer count = counts.get(aa);
		return count == null ? 0 : count.intValue();
	}

	public void incMutationCount(Aminoacid aa) {
		Integer count = counts.get(aa);
		if( count == null )
			count = 1;
		else
			count = count+1;
		counts.put(aa, count);
	}
	
	public int getTotalMutationCount() {
		int count = 0;
		for( Integer mut : counts.values() )
			count += mut.intValue();
		return count;
	}
}
