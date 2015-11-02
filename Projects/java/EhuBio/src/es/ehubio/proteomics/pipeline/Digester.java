package es.ehubio.proteomics.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.ehubio.proteomics.Enzyme;

public class Digester {
	public static class Config {
		public Config(Enzyme enzyme) {
			this(enzyme, 0, false, 0);
		}
		public Config(Enzyme enzyme, int missedCleavages ) {
			this(enzyme, missedCleavages, false, 0);
		}
		public Config(Enzyme enzyme, int missedCleavages, boolean usingDP, int cutNterm) {
			this.enzyme = enzyme;
			this.missedCleavages = missedCleavages;
			this.usingDP = usingDP;
			this.cutNterm = cutNterm;			
		}
		public Enzyme getEnzyme() {
			return enzyme;
		}
		public int getMissedCleavages() {
			return missedCleavages;
		}
		// http://sourceforge.net/p/open-ms/tickets/580/
		// http://www.sigmaaldrich.com/life-science/custom-oligos/custom-peptides/learning-center/peptide-stability.html
		public boolean isUsingDP() {
			return usingDP;
		}
		// http://sourceforge.net/p/open-ms/tickets/580/
		public int getCutNterm() {
			return cutNterm;
		}
		private final Enzyme enzyme;
		private final int missedCleavages;
		private final boolean usingDP;
		private final int cutNterm;
	}
	
	public static String[] digestSequence( String sequence, Enzyme enzyme ) {
		return enzyme.getPattern().split(sequence);
	}
	
	public static Set<String> digestSequence( String sequence, Enzyme enzyme, int missedCleavages ) {
		String[] orig = digestSequence(sequence, enzyme);
		missedCleavages = Math.min(missedCleavages, orig.length-1);
		Set<String> list = new HashSet<>(Arrays.asList(orig));
		for( int i = 1; i <= missedCleavages; i++ )
			list.addAll(getMissed(orig,i));
		return list;		
	}
	
	private static List<String> getMissed(String[] orig, int num) {
		List<String> list = new ArrayList<>();
		int stop = orig.length-num;
		for( int i = 0; i < stop; i++ ) {
			StringBuilder str = new StringBuilder();
			for( int j = 0; j <= num; j++ )
				str.append(orig[i+j]);
			list.add(str.toString());
		}
		return list;
	}
	
	public static Set<String> digestSequence( String sequence, Config config ) {
		Set<String> list = digestSequence(sequence, config.getEnzyme(), config.getMissedCleavages());		
		if( config.isUsingDP() )
			for( String str : list.toArray(new String[0]) )
				list.addAll(digestSequence(str, Enzyme.ASP_PRO, 100));
		if( config.getCutNterm() > 0 && Character.toLowerCase(sequence.charAt(0))=='m' )		
			for( String str : list.toArray(new String[0]) )
				if( sequence.startsWith(str) )
					for( int cut = 1; cut <= config.getCutNterm(); cut++ )
						if( str.length() > cut )
							list.add(str.substring(cut));
		return list;		
	}
}
