package es.ehubio.proteomics.pipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;

import es.ehubio.MathUtil;
import es.ehubio.proteomics.DecoyBase;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.ProteinGroup;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.pipeline.RandomMatcher.Result;

public class ScoreIntegrator {
	public static class IterativeResult {
		public IterativeResult(int iteration, Map<Protein, Map<Peptide,Double>> mapFactors) {
			this.iteration = iteration;
			this.mapFactors = mapFactors;
		}		
		public int getIteration() {
			return iteration;
		}
		public Map<Protein, Map<Peptide, Double>> getMapFactors() {
			return mapFactors;
		}
		private final int iteration;
		private final Map<Protein, Map<Peptide,Double>> mapFactors;
	}
	
	public static class ModelFitness {
		public ModelFitness( double nf, double mf, double nm, double mm, double r2n, double r2m ) {
			this.nf = nf;
			this.mf = mf;
			this.nm = nm;
			this.mm = mm;
			this.r2n = r2n;
			this.r2m = r2m;
		}
		public double getR2n() {
			return r2n;
		}
		public double getR2m() {
			return r2m;
		}
		public double getNf() {
			return nf;
		}
		public double getMf() {
			return mf;
		}		
		public double getMm() {
			return mm;
		}
		public double getNm() {
			return nm;
		}
		private final double nf, mf;
		private final double nm, mm;
		private final double r2n, r2m;
	}
	
	public static void updatePsmScores( Collection<Psm> psms ) {
		for( Psm psm : psms ) {			
			Score pValue = psm.getScoreByType(ScoreType.PSM_P_VALUE);
			Score spHpp = new Score(ScoreType.LPS_SCORE, -Math.log10(pValue.getValue()));
			psm.putScore(spHpp);
		}
	}
	
	public static void psmToPeptide( Collection<Peptide> peptides ) {
		for( Peptide peptide : peptides )
			sumIntegrator(peptide, peptide.getPsms(), ScoreType.LPS_SCORE, ScoreType.LPP_SCORE);
	}
	
	public static void peptideToProteinEquitative( Collection<Protein> proteins ) {		
		for( Protein protein : proteins ) {
			double score = 0.0;
			double Mq = 0.0;
			for( Peptide peptide : protein.getPeptides() ) {
				double factor = 1.0/peptide.getProteins().size();
				Mq += factor;
				score += factor*peptide.getScoreByType(ScoreType.LPP_SCORE).getValue();
			}
			protein.putScore(new Score(ScoreType.LPQ_SCORE, score));
			protein.putScore(new Score(ScoreType.MQ_OVALUE, Mq));
			protein.putScore(new Score(ScoreType.NQ_OVALUE, protein.getPeptides().size()));
		}
	}
	
	public static IterativeResult peptideToProteinIterative( Collection<Protein> proteins, double epsilon, int maxIters ) {
		Map<Protein, Map<Peptide,Double>> mapFactors = initFactors(proteins);
		int iteration = 0;
		double dif;
		do {
			dif=updateProteinScoresStep(proteins,mapFactors);
			logger.info(String.format("Iteration %d, LPQ max change = %f",++iteration,dif));
		} while( iteration < maxIters && dif > epsilon );
		return new IterativeResult(iteration, mapFactors);
	}
	
	public static void proteinToGroup( Collection<? extends ProteinGroup> groups ) {
		for( ProteinGroup group : groups ) {
			double LPG = 0.0;
			double MgExp = 0.0;
			double MgObs = 0.0;
			for( Protein protein : group.getProteins() ) {
				LPG += protein.getScoreByType(ScoreType.LPQ_SCORE).getValue();
				MgExp += protein.getScoreByType(ScoreType.MQ_EVALUE).getValue();
				MgObs += protein.getScoreByType(ScoreType.MQ_OVALUE).getValue();
			}
			Score score = new Score(ScoreType.LPG_SCORE,LPG);
			group.putScore(score);
			score = new Score(ScoreType.MG_EVALUE,MgExp);
			group.putScore(score);
			score = new Score(ScoreType.MG_OVALUE,MgObs);
			group.putScore(score);
		}
	}
		
	public static ModelFitness setExpectedValues( Collection<Protein> proteins, RandomMatcher random ) {		
		Result factor = getCorrectionFactors(proteins, random);
		logger.info(String.format("Using correction factors: Nf=%s, Mf=%s",factor.getNq(),factor.getMq()));
		applyCorrectionFactors(proteins, random, factor);		
		showEspected(proteins);		
		ModelFitness fitness = getFitness(proteins);
		logger.info(String.format("Random matching model fit: R²(Nq)=%s, R²(Mq)=%s", fitness.getR2n(), fitness.getR2m()));
		return new ModelFitness(factor.getNq(), factor.getMq(), fitness.getNm(), fitness.getMm(), fitness.getR2n(), fitness.getR2m());
	}
	
	private static Result getCorrectionFactors(Collection<Protein> proteins, RandomMatcher random) {
		List<Double> listNq = new ArrayList<>();
		List<Double> listMq = new ArrayList<>();
		double Ny, My;
		for( Protein protein : proteins ) {
			if( protein.isTarget() )
				continue;
			Result expected = random.getExpected(protein);
			if( expected == null || expected.getNq() == 0 || expected.getMq() == 0 ) {
				throw new AssertionError(String.format("Not expected protein %s! correct your search parameters", protein.getAccession()));
				//logger.warning(String.format("Not expected protein %s! correct your search parameters", protein.getAccession()));
				//continue;
			}
			Ny = protein.getScoreByType(ScoreType.NQ_OVALUE).getValue();
			My = protein.getScoreByType(ScoreType.MQ_OVALUE).getValue();
			listNq.add(Ny/expected.getNq());				
			listMq.add(My/expected.getMq());
		}
		return new Result(MathUtil.median(listNq), MathUtil.median(listMq));
		//return new Result(MathUtil.percentile(50.0, listNq), MathUtil.percentile(50.0, listMq));
		//return new Result(MathUtil.percentile(30.0, listNq), MathUtil.percentile(30.0, listMq));
		//return new Result(MathUtil.percentile(70.0, listNq), MathUtil.percentile(30.0, listMq));
	}
	
	/*private static Result getCorrectionFactors(Collection<Protein> proteins, RandomMatcher random) {
		return new Result(1.0, 1.0);
	}*/
	
	/*private static Result getCorrectionFactors(Collection<Protein> proteins, RandomMatcher random) {
		double Nt = 0.0, Mt = 0.0;
		for( Protein protein : proteins ) {			
			Result expected = random.getExpected(protein);
			Nt += expected.getNq();
			Mt += expected.getMq();
		}
		TrypticMatcher tryptic = (TrypticMatcher)random;
		return new Result(tryptic.getRedundantDecoys()/Nt, tryptic.getDecoys()/Mt);
	}*/
	
	private static void applyCorrectionFactors(Collection<Protein> proteins, RandomMatcher random, Result factor) {
		for( Protein protein : proteins ) {
			Result expected = random.getExpected(protein);
			/*if( expected == null )
				System.out.println(String.format("%s: %s", protein.getAccession(), protein.isDecoy()));*/
			protein.putScore(new Score(ScoreType.NQ_EVALUE, expected.getNq()*factor.getNq()));
			protein.putScore(new Score(ScoreType.MQ_EVALUE, expected.getMq()*factor.getMq()));
		}
	}
	
	private static void showEspected(Collection<Protein> proteins) {
		double obsNqCount = 0.0, expNqCount = 0.0;
		double obsMqCount = 0.0, expMqCount = 0.0;		
		for( Protein protein : proteins ) {
			if( protein.isTarget() )
				continue;
			obsNqCount += protein.getScoreByType(ScoreType.NQ_OVALUE).getValue();
			expNqCount += protein.getScoreByType(ScoreType.NQ_EVALUE).getValue();
			obsMqCount += protein.getScoreByType(ScoreType.MQ_OVALUE).getValue();
			expMqCount += protein.getScoreByType(ScoreType.MQ_EVALUE).getValue();
		}
		logger.info(String.format("Expected/Observed: Nq=%d/%d, Mq=%d/%d",
			Math.round(expNqCount), Math.round(obsNqCount),
			Math.round(expMqCount), Math.round(obsMqCount)));
	}
	
	private static ModelFitness getFitness(Collection<Protein> proteins) {
		double Nm = 0.0, Mm = 0.0;
		for( Protein protein : proteins ) {
			if( protein.isTarget() )
				continue;
			Nm += protein.getScoreByType(ScoreType.NQ_OVALUE).getValue();
			Mm += protein.getScoreByType(ScoreType.MQ_OVALUE).getValue();
		}
		Nm /= proteins.size();
		Mm /= proteins.size();
		
		double Nt = 0.0, Nr = 0.0;
		double Mt = 0.0, Mr = 0.0;
		double Ny, My;
		for( Protein protein : proteins ) {
			if( protein.isTarget() )
				continue;
			Ny = protein.getScoreByType(ScoreType.NQ_OVALUE).getValue();			
			Nt += MathUtil.pow2(Ny-Nm);
			Nr += MathUtil.pow2(Ny-protein.getScoreByType(ScoreType.NQ_EVALUE).getValue()); 
			My = protein.getScoreByType(ScoreType.MQ_OVALUE).getValue();
			Mt += MathUtil.pow2(My-Mm);
			Mr += MathUtil.pow2(My-protein.getScoreByType(ScoreType.MQ_EVALUE).getValue());
		}
		
		return new ModelFitness(1.0, 1.0, Nm, Mm, 1-Nr/Nt, 1-Mr/Mt);
	}
	
	public static void divideRandom( Collection<Protein> proteins, boolean shared ) {
		for( Protein protein : proteins ) {
			double Mq = shared ? protein.getScoreByType(ScoreType.MQ_EVALUE).getValue() : protein.getScoreByType(ScoreType.NQ_EVALUE).getValue();
			double LPQ = protein.getScoreByType(ScoreType.LPQ_SCORE).getValue();
			protein.putScore(new Score(ScoreType.LPQCORR_SCORE, LPQ/Mq));
		}
	}
	
	public static void modelRandomProteins( Collection<? extends Decoyable> proteins, boolean shared, boolean fast ) {		
		if( fast ) {
			logger.info("Modelling aprox. random peptide-protein matching ...");
			modelRandomAprox(proteins, shared?ScoreType.MQ_EVALUE:ScoreType.NQ_EVALUE, ScoreType.LPQ_SCORE, ScoreType.LPQCORR_SCORE);
		} else {
			logger.info("Modelling random peptide-protein matching ...");
			modelRandom(proteins, shared?ScoreType.MQ_EVALUE:ScoreType.NQ_EVALUE, ScoreType.LPQ_SCORE, ScoreType.LPQCORR_SCORE);
		}
	}
	
	public static void modelRandomGroups( Collection<? extends Decoyable> groups, boolean fast ) {		
		if( fast ) {
			logger.info("Modelling aprox. random peptide-group matching ...");
			modelRandomAprox(groups, ScoreType.MG_EVALUE, ScoreType.LPG_SCORE, ScoreType.LPGCORR_SCORE);
		} else {
			logger.info("Modelling random peptide-group matching ...");
			modelRandom(groups, ScoreType.MG_EVALUE, ScoreType.LPG_SCORE, ScoreType.LPGCORR_SCORE);
		}
	}
	
	public static void modelRandom( Collection<? extends Decoyable> proteins, ScoreType mScore, ScoreType lpScore, ScoreType lpcScore ) {
		double loge = Math.log(10.0);
		double epsilon = 1e-30;
		for( Decoyable protein : proteins ) {
			double Mq = protein.getScoreByType(mScore).getValue();			
			double LPQ = protein.getScoreByType(lpScore).getValue()*loge;
			
			int n1 = searchInf(Mq, LPQ, 1, (int)Math.round(Mq), 1, epsilon);
			int n2 = searchSup(Mq, LPQ, (int)Math.round(Mq), 10000, 1, epsilon);
			double sum = 0.0;			
			PoissonDistribution poisson = new PoissonDistribution(Mq);
			for( int n = n1; n <= n2; n++ ) {
				GammaDistribution gamma = new GammaDistribution(n, 1);
				sum += poisson.probability(n)*(1-gamma.cumulativeProbability(LPQ));
			}
			
			double LPQcorr = sum < 1e-30 ? 30 : -Math.log10(sum);
			protein.putScore(new Score(lpcScore, LPQcorr));
		}
	}
	
	public static void modelRandomAprox( Collection<? extends Decoyable> proteins, ScoreType mScore, ScoreType lpScore, ScoreType lpcScore ) {		
		double loge = Math.log(10.0);
		for( Decoyable protein : proteins ) {
			double Mq = protein.getScoreByType(mScore).getValue();			
			double LPQ = protein.getScoreByType(lpScore).getValue()*loge;
			GammaDistribution gamma = new GammaDistribution(Mq, 1);
			double sum = 1-gamma.cumulativeProbability(LPQ);
			//sum = -Math.log10(sum);
			//double LPQcorr = sum > 290 ? 300 : (LPQ/Mq+sum)/2;
			double LPQcorr = sum < 1e-40 ? 30 : -0.75*Math.log10(sum);
			protein.putScore(new Score(lpcScore, LPQcorr));
		}
	}
	
	private static int searchInf( double Mq, double LPQ, int n1, int n2, int dn, double epsilon ) {
		PoissonDistribution poisson = new PoissonDistribution(Mq);
		double p;
		int n=n1, prev=n1;		
		while( n2 - n > dn && n2 > n1) {			
			GammaDistribution gamma = new GammaDistribution(n, 1);
			p = poisson.probability(n)*(1-gamma.cumulativeProbability(LPQ));
			if( p < epsilon ) {
				prev = n;
				n = (n+n2)/2;
			} else {
				n2 = n;
				n = (prev+n2)/2;
			}
		};
		
		return n;
	}
	
	private static int searchSup( double Mq, double LPQ, int n1, int n2, int dn, double epsilon ) {
		PoissonDistribution poisson = new PoissonDistribution(Mq);
		double p;
		int n=n2, prev=n2;		
		while( n-n1 > dn && n1 < n2) {			
			GammaDistribution gamma = new GammaDistribution(n, 1);
			p = poisson.probability(n)*(1-gamma.cumulativeProbability(LPQ));
			if( p < epsilon ) {
				prev = n;
				n = (n+n1)/2;
			} else {
				n1 = n;
				n = (prev+n1)/2;
			}
		};
		
		return n;
	}
	
	private static Map<Protein, Map<Peptide,Double>> initFactors( Collection<Protein> proteins ) {
		Map<Protein, Map<Peptide,Double>> mapFactors = new HashMap<>();
		Map<Peptide,Double> scores = null;
		
		for( Protein protein : proteins ) {
			scores = new HashMap<>();
			for( Peptide peptide : protein.getPeptides() ) {
				double factor = 1.0/peptide.getProteins().size();
				scores.put(peptide, factor);
			}			
			mapFactors.put(protein, scores);
		}

		return mapFactors;
	}
	
	private static double updateProteinScoresStep( Collection<Protein> proteins, Map<Protein, Map<Peptide,Double>> mapFactors ) {
		modelRandomProteins(proteins, true, true);
		//modelRandomProteins(proteins, true, false);
		updateFactors(proteins, mapFactors);
		return updateLpq(proteins, mapFactors);		
	}

	private static void updateFactors( Collection<Protein> proteins, Map<Protein, Map<Peptide,Double>> mapFactors ) {		
		for( Protein protein : proteins ) {
			double num = protein.getScoreByType(ScoreType.LPQCORR_SCORE).getValue();
			//double Mq = 0.0;
			for( Peptide peptide : protein.getPeptides() ) {
				double den = 0.0;
				for( Protein protein2 : peptide.getProteins() )
					den += protein2.getScoreByType(ScoreType.LPQCORR_SCORE).getValue();
				double factor = den < 1e-10 ? 1.0/peptide.getProteins().size() : num/den;
				//Mq += factor;
				mapFactors.get(protein).put(peptide, factor);
			}
			//protein.getScoreByType(ScoreType.MQ_OVALUE).setValue(Mq);
		}
	}
	
	private static double updateLpq( Collection<Protein> proteins, Map<Protein, Map<Peptide,Double>> mapFactors ) {
		double dif = 0.0;		
		for( Protein protein : proteins ) {
			double newScore = 0.0;
			for( Peptide peptide : protein.getPeptides() )
				newScore += mapFactors.get(protein).get(peptide)*peptide.getScoreByType(ScoreType.LPP_SCORE).getValue();
			Score score = protein.getScoreByType(ScoreType.LPQ_SCORE);
			dif = Math.max(dif, Math.abs(newScore-score.getValue()));			
			score.setValue(newScore);
		}		
		return dif;
	}
	
	private static void sumIntegrator(DecoyBase item, Collection<? extends DecoyBase> subitems, ScoreType lowScore, ScoreType upScore) {
		double s = 0.0;
		for( DecoyBase subitem : subitems )
			s += subitem.getScoreByType(lowScore).getValue();
		Score spHpp = new Score(upScore,s);
		item.putScore(spHpp);
	}
	
	private static final Logger logger = Logger.getLogger(ScoreIntegrator.class.getName()); 
}