package es.ehubio.proteomics.pipeline;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.ScoreType;

public class DecoyMatcher implements RandomMatcher {
	public DecoyMatcher( Collection<Protein> proteins, String decoyTag ) {
		for( Protein protein : proteins )
			if( Boolean.TRUE.equals(protein.getDecoy()) )
				mapDecoys.put(protein.getAccession().replaceAll(decoyTag,""), protein);
	}
	
	public boolean checkPrefix( Collection<Protein> proteins ) {
		for( Protein protein : proteins )
			if( !Boolean.TRUE.equals(protein.getDecoy()) )
				if( mapDecoys.get(protein.getAccession()) != null )
					return true;
		return false;
	}
	
	@Override
	public Result getExpected(Protein protein) {
		if( !Boolean.TRUE.equals(protein.getDecoy()) ) {
			protein = mapDecoys.get(protein.getAccession());
			if( protein == null )
				return new Result(1.0, 1.0);
		}
		return new Result(
			protein.getScoreByType(ScoreType.NQ_OVALUE).getValue(),
			protein.getScoreByType(ScoreType.MQ_OVALUE).getValue()
		);
	}
	
	private final Map<String, Protein> mapDecoys = new HashMap<>();
}
