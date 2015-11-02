package es.ehubio.wregex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.ehubio.model.Aminoacid;
import es.ehubio.wregex.PssmBuilder.PssmBuilderException;
import es.ehubio.wregex.Wregex.WregexException;

public final class Trainer {
	Wregex wregex;
	List<TrainingGroup> trainingGroups = null;
	
	public Trainer( String regex ) {
		try {
			wregex = new Wregex(regex,null);
		} catch (WregexException e) {
			throw new RuntimeException(e);
		}
	}
	
	private List<TrainingGroup> trainStep( List<InputGroup> inputGroups, long tout ) {
		List<ResultGroup> results;
		TrainingGroup group;
		trainingGroups = new ArrayList<>();
		long wdt = System.currentTimeMillis() + tout;
		for( InputGroup inputGroup : inputGroups ) {
			results = wregex.searchGrouping(inputGroup.getFasta());
			if( tout > 0 && System.currentTimeMillis() >= wdt )
				return null;
			for( InputMotif motif : inputGroup.getMotifs() ) {
				motif.setMatches(0);
				for( ResultGroup result : results ) {
					group = new TrainingGroup(result, motif.getWeight());
					for( Result r : result ) {
						if( !motif.contains(r) )
							group.remove(r);
						else
							motif.addMatch();
					}
					if( !group.isEmpty() )
						trainingGroups.add(group);
				}
			}
		}
		
		// Filter duplicates (when same result overlaps with two input motifs)
		List<TrainingMotif> motifs = new ArrayList<>();
		for( TrainingGroup trainingGroup : trainingGroups )
			motifs.addAll(trainingGroup);
		for( int i = 0; i < motifs.size()-1; i++ )
			for( int j = i + 1; j < motifs.size(); j++ )
				if( motifs.get(i).getResult() == motifs.get(j).getResult() )
					if( motifs.get(i).getWeight() >= motifs.get(j).getWeight() )
						motifs.get(j).remove();
					else
						motifs.get(i).remove();
		
		return trainingGroups;
	}
	
	public List<TrainingGroup> train( List<InputGroup> inputGroups, boolean calculateScores, long tout ) {
		List<TrainingGroup> result = trainStep(inputGroups, tout);
		if( !calculateScores || result == null )
			return result;
		try {
			Pssm pssm = buildPssm(true);
			wregex = new Wregex(wregex.getRegex(), pssm);
		} catch (PssmBuilderException e) {
			throw new RuntimeException(e);
		} catch (WregexException e) {
			throw new RuntimeException(e);
		}
		return trainStep(inputGroups, tout);
	}
	
	public Pssm buildPssm( boolean doNormalization ) throws PssmBuilderException {
		assert trainingGroups != null && !trainingGroups.isEmpty();
		PssmBuilder pssm = new PssmBuilder();
		List<TrainingMotif> motifs = new ArrayList<>();		
		for( TrainingGroup group : trainingGroups )
			motifs.addAll(group);
		int groupCount = motifs.get(0).getRegexGroups().size();		
		for( Aminoacid aa : Aminoacid.values() ) {
			Double[] scores = new Double[groupCount];
			for( int i = 0; i < groupCount; i++ ) {
				scores[i] = 0.0;
				double sum = 0.0;
				for( TrainingMotif motif : motifs ) {
					if( motif.getRegexGroups().get(i) == null )
						continue;
					String str = motif.getRegexGroups().get(i).toUpperCase();
					sum += str.length()*motif.getDividedWeight();
					int last = 0;
					while( (last=str.indexOf(aa.letter, last)) != -1 ) {
						scores[i] += motif.getDividedWeight();
						last++;
					}
				}
				scores[i] = Math.log10((sum==0.0?0.0:scores[i]/sum)+0.00001); 
			}
			pssm.setScores(aa, Arrays.asList(scores));
		}
		if( doNormalization )
			pssm.normalize();
		return pssm.build();
	}
	
	public String getRegex() {
		return wregex.getRegex();
	}
}
