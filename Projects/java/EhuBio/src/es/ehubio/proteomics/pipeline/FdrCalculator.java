package es.ehubio.proteomics.pipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import es.ehubio.proteomics.AmbiguityGroup;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class FdrCalculator {
	public static enum FdrFormula {DT, D2TD, MAYU};
	
	private static final Logger logger = Logger.getLogger(FdrCalculator.class.getName());
	private final FdrFormula fdrFormula;
	
	/**
	 * If true uses FDR=2*D/(T+D), else FDR=D/T
	 * 
	 * @param countDecoy
	 */
	public FdrCalculator( FdrFormula fdrFormula ) {
		this.fdrFormula = fdrFormula;
	}
	
	public FdrCalculator() {
		this(FdrFormula.DT);
	}
	
	public double getFdr( int d, int t, int nd, int nt ) {
		if( t == 0 )
			return 0.0;
		switch(fdrFormula) {
			case DT:
				return ((double)d)/t;
			case D2TD:
				return (2.0*d)/(t+d);
			case MAYU:
				double dcorr = nd <= d ? d : ((double)d)*(nt-t)/(nd-d);
				return dcorr/t;
			default:
				throw new RuntimeException("FDR formula not supported");
		}
	}
	
	public void updatePsmScores( Collection<Psm> psms, ScoreType type, boolean updatePvalues ) {
		updateDecoyScores(psms, type, updatePvalues ? ScoreType.PSM_P_VALUE : null,
			ScoreType.PSM_LOCAL_FDR, ScoreType.PSM_Q_VALUE, ScoreType.PSM_FDR_SCORE);
	}
	
	public void updatePsmScores( Collection<Psm> target, Collection<Psm> decoy, ScoreType type, boolean updatePvalues ) {
		List<Psm> list = new ArrayList<>();
		list.addAll(target);
		list.addAll(decoy);
		updatePsmScores(list, type, updatePvalues);
	}
	
	public void updatePeptideScores( Collection<Peptide> peptides, ScoreType type, boolean updatePvalues ) {
		updateDecoyScores(peptides, type, updatePvalues ? ScoreType.PEPTIDE_P_VALUE : null,
			ScoreType.PEPTIDE_LOCAL_FDR, ScoreType.PEPTIDE_Q_VALUE, ScoreType.PEPTIDE_FDR_SCORE);
	}
	
	public void updatePeptideScores( Collection<Peptide> target, Collection<Peptide> decoy, ScoreType type, boolean updatePvalues ) {
		List<Peptide> list = new ArrayList<>();
		list.addAll(target);
		list.addAll(decoy);
		updatePeptideScores(list, type, updatePvalues);
	}
	
	public void updateProteinScores( Collection<Protein> proteins, ScoreType type, boolean updatePvalues ) {
		updateDecoyScores(proteins, type, updatePvalues ? ScoreType.PROTEIN_P_VALUE : null,
			ScoreType.PROTEIN_LOCAL_FDR, ScoreType.PROTEIN_Q_VALUE, ScoreType.PROTEIN_FDR_SCORE);
	}
	
	public void updateProteinScores( Collection<Protein> target, Collection<Protein> decoy, ScoreType type, boolean updatePvalues ) {
		List<Protein> list = new ArrayList<>();
		list.addAll(target);
		list.addAll(decoy);
		updateProteinScores(list, type, updatePvalues);
	}
		
	public void updateGroupScores( Collection<? extends Decoyable> groups, ScoreType type, boolean updatePvalues ) {
		updateDecoyScores(groups, type, updatePvalues ? ScoreType.GROUP_P_VALUE : null,
			ScoreType.GROUP_LOCAL_FDR, ScoreType.GROUP_Q_VALUE, ScoreType.GROUP_FDR_SCORE);
	}
	
	public void updateGroupScores( Collection<AmbiguityGroup> target, Collection<AmbiguityGroup> decoy, ScoreType type, boolean updatePvalues ) {
		List<AmbiguityGroup> list = new ArrayList<>();
		list.addAll(target);
		list.addAll(decoy);
		updateGroupScores(list, type, updatePvalues);
	}
	
	public void updateDecoyScores( Collection<? extends Decoyable> items, ScoreType type, ScoreType pValue, ScoreType localFdr, ScoreType qValue, ScoreType fdrScore ) {
		if( items.isEmpty() )
			return;
		
		// Sort items from worst to best
		List<Decoyable> list = new ArrayList<>();
		for( Decoyable item : items )
			if( !item.skipFdr() )
				list.add(item);
		sort(list,type);
		
		Map<Double,ScoreGroup> mapScores = new HashMap<>();
		getLocalFdr(list,type,pValue!=null,mapScores);
		if( qValue != null )
			getQValues(list,type,mapScores);
		if( fdrScore != null )
			getFdrScores(list,type,mapScores);		
		
		// Assign scores
		for( Decoyable item : list ) {
			ScoreGroup scoreGroup = mapScores.get(item.getScoreByType(type).getValue());
			if( pValue != null )
				item.putScore(new Score(pValue, scoreGroup.getpValue()));
			if( localFdr != null )
				item.putScore(new Score(localFdr,scoreGroup.getFdr()));
			if( qValue != null )
				item.putScore(new Score(qValue,scoreGroup.getqValue()));
			if( fdrScore != null )
				item.putScore(new Score(fdrScore,scoreGroup.getFdrScore()));
			//System.out.println(String.format("%s,%s,%s,%s,%s",psm.getScoreByType(type).getValue(),scoreGroup.getpValue(),scoreGroup.getFdr(),scoreGroup.getqValue(),scoreGroup.getFdrScore()));
		}
	}
	
	public FdrResult getGlobalFdr( Collection<? extends Decoyable> items ) {
		int decoy = 0;
		int target = 0;
		for( Decoyable item : items ) {
			if( item.skipFdr() )
				continue;
			if( Boolean.TRUE.equals(item.getDecoy()) )
				decoy++;
			else
				target++;
		}
		return new FdrResult(decoy, target, 0, 0);
	}
	
	public void logFdrs(MsMsData data) {
		logger.info(String.format("FDR -> PSM: %s, Peptide: %s, Protein: %s, Group: %s",
			getGlobalFdr(data.getPsms()).getRatio(),
			getGlobalFdr(data.getPeptides()).getRatio(),
			getGlobalFdr(data.getProteins()).getRatio(),
			getGlobalFdr(data.getGroups()).getRatio()));
	}
	
	private void sort( List<? extends Decoyable> list, final ScoreType type ) {
		//logger.info("Sorting scores ...");
		Collections.sort(list, new Comparator<Decoyable>() {
			@Override
			public int compare(Decoyable o1, Decoyable o2) {
				return o1.getScoreByType(type).compare(o2.getScoreByType(type).getValue());
			}
		});
	}
	
	private void getLocalFdr(List<Decoyable> list, ScoreType type, boolean pValue, Map<Double,ScoreGroup> mapScores) {
		// Count total decoy and target numbers (for p-values and MAYU)
		int totalDecoys = 0, totalTargets;
		for( Decoyable item : list )
			if( Boolean.TRUE.equals(item.getDecoy()) )
				totalDecoys++;
		totalTargets = list.size()-totalDecoys;

		// Traverse from best to worst to calculate local FDRs and p-values
		int decoy = 0, target = 0;
		Decoyable item;
		double pOff;
		Double score;
		ScoreGroup scoreGroup;
		for( int i = list.size()-1; i >= 0; i-- ) {
			item = list.get(i);
			score = item.getScoreByType(type).getValue();
			scoreGroup = mapScores.get(score);
			if( Boolean.TRUE.equals(item.getDecoy()) ) {
				decoy++;
				pOff = -0.5;
			} else {
				target++;
				pOff = (scoreGroup == null || scoreGroup.isDecoy() == false) ? 0.5 : -0.5;
			}
			if( scoreGroup == null ) {
				scoreGroup = new ScoreGroup();
				mapScores.put(score, scoreGroup);
			}
			scoreGroup.setFdr(getFdr(decoy,target,totalDecoys,totalTargets));
			if( pValue ) {
				scoreGroup.setpValue(totalDecoys==0?0:(decoy+pOff)/totalDecoys);
				if( pOff < 0 )
					scoreGroup.setDecoy(true);
			}
		}
	}
	
	private void getQValues(List<Decoyable> list, ScoreType type, Map<Double, ScoreGroup> mapScores) {
		// Traverse from worst to best to calculate q-values
		double min = mapScores.get(list.get(0).getScoreByType(type).getValue()).getFdr();		
		for( int i = 0; i < list.size(); i++ ) {
			ScoreGroup scoreGroup = mapScores.get(list.get(i).getScoreByType(type).getValue());
			double fdr = scoreGroup.getFdr();
			if( fdr < min )
				min = fdr;
			scoreGroup.setqValue(min);
		}
	}
	
	private void getFdrScores(List<Decoyable> list, ScoreType type, Map<Double, ScoreGroup> mapScores) {
		// Interpolate q-values from best to worst to calculate FDRScores
		int j, i = list.size()-1;
		double x1 = list.get(i).getScoreByType(type).getValue();
		double y1 = mapScores.get(x1).getqValue();
		double x0, y0, x, m;
		while( i > 0 ) {
			x0 = x1;
			y0 = y1;
			j = i;
			do {
				j--;
				x1 = list.get(j).getScoreByType(type).getValue();
				y1 = mapScores.get(x1).getqValue();  
			} while( j > 0 && y1 == y0 );
			m = (y1-y0)/(x1-x0);
			for( int k = j; k <= i; k++ ) {
				x = list.get(k).getScoreByType(type).getValue();
				mapScores.get(x).setFdrScore((x-x0)*m+y0);
			}
			i=j;
		}
	}
	
	public class FdrResult {
		private final int target, totalTargets;
		private final int decoy, totalDecoys;
		private final double fdr;
		
		public FdrResult( int decoy, int target, int totalDecoys, int totalTargets ) {
			this.decoy = decoy;
			this.target = target;
			this.totalDecoys = totalDecoys;
			this.totalTargets = totalTargets;
			fdr = getFdr(decoy, target, totalDecoys, totalTargets); 
		}

		public int getTarget() {
			return target;
		}

		public int getDecoy() {
			return decoy;
		}

		public double getRatio() {
			return fdr;
		}

		public int getTotalTargets() {
			return totalTargets;
		}

		public int getTotalDecoys() {
			return totalDecoys;
		}		
	}
	
	private class ScoreGroup {
		private double pValue;
		private double fdr;
		private double qValue;
		private double fdrScore;
		private boolean decoy = false;
		public double getFdr() {
			return fdr;
		}
		public void setFdr(double fdr) {
			this.fdr = fdr;
		}
		public double getqValue() {
			return qValue;
		}
		public void setqValue(double qValue) {
			this.qValue = qValue;
		}
		public double getFdrScore() {
			return fdrScore;
		}
		public void setFdrScore(double fdrScore) {
			this.fdrScore = fdrScore;
		}
		public double getpValue() {
			return pValue;
		}
		public void setpValue(double pValue) {
			this.pValue = pValue;
		}
		public boolean isDecoy() {
			return decoy;
		}
		public void setDecoy(boolean decoy) {
			this.decoy = decoy;
		}
	}
}
