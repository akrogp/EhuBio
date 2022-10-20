package es.ehubio.wregex.data;

import java.util.Comparator;
import java.util.Map;

public class ResultComparator implements Comparator<ResultEx> {
	private final String[] ptms;
	
	public ResultComparator(String[] ptms) {
		this.ptms = ptms;
	}

	@Override
	public int compare(ResultEx o1, ResultEx o2) {
		// 1. Mutation effect
		if( o1.getMutScore() == null && o2.getMutScore() != null )
			return 1;
		if( o1.getMutScore() != null && o2.getMutScore() == null )
			return -1;
		if( o1.getMutScore() != null && o2.getMutScore() != null ) {
			if( Math.abs(o1.getMutScore()) > Math.abs(o2.getMutScore()) )
				return -1;
			if( Math.abs(o1.getMutScore()) < Math.abs(o2.getMutScore()) )
				return 1;
		}
		// 2. Mutation count
		if( o1.getCosmicMissense() > o2.getCosmicMissense() )
			return -1;
		if( o1.getCosmicMissense() < o2.getCosmicMissense() )
			return 1;
		// 3. Wregex Score
		if( o1.getScore() > o2.getScore() )
			return -1;
		if( o1.getScore() < o2.getScore() )
			return 1;
		// 4. Selected PTMs
		int ptms1 = countPtms(o1);
		int ptms2 = countPtms(o2);
		if( ptms1 != ptms2 )
			return ptms2 - ptms1;
		// 5. All PTMs
		if( o1.getDbPtms() != o2.getDbPtms() )
			return o2.getDbPtms() - o1.getDbPtms();
		// 6. Aux Score (combinations)
		if( o1.getAuxScore() == null && o2.getAuxScore() != null )
			return 1;
		if( o1.getAuxScore() != null && o2.getAuxScore() == null )
			return -1;
		if( o1.getAuxScore() != null && o2.getAuxScore() != null )
			return (int)Math.signum(o2.getAuxScore() - o1.getAuxScore());		
		// 7. Wregex Combinations
		if( o1.getCombinations() != o2.getCombinations() )
			return o2.getCombinations() - o1.getCombinations();
		// 8. Match length
		if( o1.getMatch().length() != o2.getMatch().length() )
			return o2.getMatch().length() - o1.getMatch().length();
		return 0;
	}

	private int countPtms(ResultEx o) {
		Map<String, Integer> map = o.getPtmCounts();
		if( o.getDbPtms() < 0 || map == null || map.isEmpty() )
			return o.getDbPtms();
		int count = 0;
		for( String ptm : ptms ) {
			Integer inc = map.get(ptm);
			if( inc != null )
				count += inc;
		}
		return count;
	}
}
