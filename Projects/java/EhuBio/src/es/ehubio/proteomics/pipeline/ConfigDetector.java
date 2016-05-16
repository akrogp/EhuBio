package es.ehubio.proteomics.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.ehubio.model.Aminoacid;
import es.ehubio.proteomics.Enzyme;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Ptm;

public class ConfigDetector {
	public static class Modification {		
		public Aminoacid getAa() {
			return aa;
		}
		public void setAa(Aminoacid aa) {
			this.aa = aa;
		}
		public double getDeltaMass() {
			return deltaMass;
		}
		public void setDeltaMass(double deltaMass) {
			this.deltaMass = deltaMass;
		}
		public boolean isFixed() {
			return fixed;
		}
		public void setFixed(boolean fixed) {
			this.fixed = fixed;
		}
		public boolean isCterm() {
			return cterm;
		}
		public void setCterm(boolean cterm) {
			this.cterm = cterm;
		}
		public boolean isNterm() {
			return nterm;
		}
		public void setNterm(boolean nterm) {
			this.nterm = nterm;
		}
		private Aminoacid aa = null;
		private double deltaMass = 0.0;
		private boolean fixed = false;
		private boolean cterm = false;
		private boolean nterm = false;
	}
	
	public ConfigDetector() {
		this(0);
	}
	
	public ConfigDetector( int proteinSubset ) {
		this.proteinSubset = proteinSubset;
	}
	
	public int getMinPeptideLength( MsMsData data ) {
		int minPeptideLength = data.getPeptides().iterator().next().getSequence().length();
		for( Peptide peptide : data.getPeptides() ) {
			int len = peptide.getSequence().length();
			if( len < minPeptideLength )
				minPeptideLength = len;
		}
		return minPeptideLength;
	}
	
	public int getMaxPeptideLength( MsMsData data ) {
		int maxPeptideLength = data.getPeptides().iterator().next().getSequence().length();
		for( Peptide peptide : data.getPeptides() ) {
			int len = peptide.getSequence().length();
			if( len > maxPeptideLength )
				maxPeptideLength = len;
		}
		return maxPeptideLength;
	}
	
	private Enzyme getEnzyme( MsMsData data, boolean useDP, int cutNterm ) {
		for( Enzyme enzyme : Enzyme.values() ) {
			int count = proteinSubset <= 0 ? data.getProteins().size() : proteinSubset;
			boolean valid = true;
			for( Protein protein : data.getProteins() ) {
				if( protein.getSequence() == null )
					continue;
				String protSeq = protein.getSequence().toLowerCase();
				Set<String> peptides = new HashSet<>(Arrays.asList(Digester.digestSequence(protSeq,enzyme)));
				for( Peptide peptide : protein.getPeptides() ) {
					String pepSeq = peptide.getSequence().toLowerCase();
					if( pepSeq.matches(".*[bjzx].*") )
						continue;
					if( useDP && (pepSeq.indexOf("dp") != -1 || pepSeq.charAt(0)=='p' || pepSeq.endsWith("d")) )
						continue;
					if( cutNterm > 0 && protSeq.charAt(0) == 'm' && protSeq.indexOf(pepSeq) <= cutNterm )
						continue;
					if( Digester.digestSequence(pepSeq,enzyme).length > 1 )
						continue;
					if( !peptides.contains(pepSeq) ) {
						valid = false;
						break;
					}
				}
				if( valid == false )
					break;
				if( --count == 0 )
					return enzyme;
			}
		}
		return null;
	}
	
	public Digester.Config getDigestion( MsMsData data ) {
		int cut = 0;		
		int count = proteinSubset <= 0 ? data.getProteins().size() : proteinSubset;
		for( Protein protein : data.getProteins() ) {
			cut = Math.max(cut, getNtermCut(protein));			
			if( cut == 2 || --count == 0 ) break;
		}
		
		boolean dp = false;
		count = proteinSubset <= 0 ? data.getProteins().size() : proteinSubset;		
		for( Protein protein : data.getProteins() ) {
			if( usesDP(protein) ) {
				dp = true;
				break;
			}
			if( --count == 0 ) break;
		}
		
		Enzyme enzyme = getEnzyme(data, dp, cut);
		if( enzyme == null )
			return null;
		
		int missedCleavages = getMissedCleavages(data, enzyme);
		if( missedCleavages == -1 )
			return null;
		
		Digester.Config config = new Digester.Config(enzyme, missedCleavages, dp, cut);
		count = proteinSubset <= 0 ? data.getProteins().size() : proteinSubset;		
		for( Protein protein : data.getProteins() ) {
			Set<String> peptides = Digester.digestSequence(protein.getSequence().toLowerCase(), config);
			for( Peptide peptide : protein.getPeptides() )
				if( !peptides.contains(peptide.getSequence().toLowerCase()) )
					return null;
			if( --count == 0 ) break;
		}
		
		return config;
	}
	
	public Searcher.Config getSearching( MsMsData data ) {
		int minLen = getMinPeptideLength(data);
		int maxLen = getMaxPeptideLength(data);
		List<Modification> mods = getMods(data);
		List<Aminoacid> varMods = new ArrayList<>();
		for( Modification mod : mods )
			if( !mod.isFixed() )
				varMods.add(mod.getAa());
		int maxMods = getMaxModsPerPeptide(data, varMods);
		return new Searcher.Config(minLen, maxLen, maxMods, varMods);
	}
	
	private int getNtermCut( Protein protein ) {
		if( Character.toLowerCase(protein.getSequence().charAt(0)) != 'm' )
			return 0;
		String cut1 = protein.getSequence().substring(1);
		String cut2 = protein.getSequence().substring(2);
		int cut = 0;
		for( Peptide peptide : protein.getPeptides() ) {
			if( cut2.startsWith(peptide.getSequence()) )
				return 2;
			if( cut1.startsWith(peptide.getSequence()) )
				cut = 1;
		}
		return cut;
	}
	
	private boolean usesDP( Protein protein ) {
		if( protein.getSequence() == null )
			return false;
		for( Peptide peptide : protein.getPeptides() )
			if( peptide.getSequence().toLowerCase().endsWith("d") ) {
				int i = protein.getSequence().indexOf(peptide.getSequence())+peptide.getSequence().length();
				if( i < protein.getSequence().length() && Character.toLowerCase(protein.getSequence().charAt(i))=='p' )
					return true;
			}
		return false;
	}
	
	private int getMissedCleavages(MsMsData data, Enzyme enzyme) {
		int missedCleavages = 0;
		int count = proteinSubset <= 0 ? data.getProteins().size() : proteinSubset;
		for( Protein protein : data.getProteins() ) {
			for( Peptide peptide : protein.getPeptides() ) {
				if( peptide.getSequence().toLowerCase().matches(".*[bjzx].*") )
					continue;
				missedCleavages = Math.max(missedCleavages, Digester.digestSequence(peptide.getSequence(),enzyme).length-1);
				/*if( missedCleavages > 2 ) {
					System.out.println(peptide.getSequence());
					missedCleavages = 2;
				}*/
			}
			if( --count == 0 )
				break;
		}
		return missedCleavages;
	}
	
	public int getMaxModsPerPeptide(MsMsData data, Collection<Aminoacid> varMods) {
		if( varMods == null || varMods.isEmpty() )
			return 0;
		
		Set<Character> chars = new HashSet<>();
		for( Aminoacid aa : varMods )
			chars.add(Character.toUpperCase(aa.letter));
		
		int max = 0;		
		for( Peptide peptide : data.getPeptides() ) {
			Map<Character, Integer> mapCount = new HashMap<>();
			for( Ptm ptm : peptide.getPtms() ) {
				if( ptm.getAminoacid() == null || !chars.contains(Character.toUpperCase(ptm.getAminoacid())) )
					continue;
				Integer count = mapCount.get(ptm.getAminoacid());
				if( count == null )
					mapCount.put(ptm.getAminoacid(), 1);
				else
					mapCount.put(ptm.getAminoacid(), count+1);
			}
			for( Integer count : mapCount.values() )
				if( count > max )
					max = count;
		}
		return max;
	}

	public List<Modification> getMods(MsMsData data) {
		List<Modification> mods = new ArrayList<>();
		for( Peptide peptide : data.getPeptides() )
			for( Ptm ptm : peptide.getPtms() ) {				
				if( ptm.getAminoacid() == null || ptm.getMassDelta() == null )
					continue;
				/*if( ptm.getPosition() != null && (ptm.getPosition() == 1 || ptm.getPosition() == peptide.getSequence().length()) ) {
					System.out.println(ptm.toString());
					continue;
				}*/
				boolean add = true;
				for( Modification mod : mods )
					if( mod.getAa().letter == ptm.getAminoacid() && Math.round(mod.getDeltaMass()*100) == Math.round(ptm.getMassDelta()*100) ) {
						add = false;
						break;
					}
				if( add ) {
					Modification mod = new Modification();
					mod.setAa(Aminoacid.parseLetter(ptm.getAminoacid()));
					mod.setDeltaMass(ptm.getMassDelta());
					mods.add(mod);
				}
			}
		findVarMods(mods, data);
		//findNtermMods(mods, data);
		//findCtermMods(mods, data);
		return mods;
	}	

	private void findVarMods(List<Modification> mods, MsMsData data) {
		for( Modification mod : mods ) {
			mod.setFixed(true);
			for( Peptide peptide : data.getPeptides() )
				if( countChars(peptide.getSequence(), mod.getAa()) > peptide.getPtms().size() ) {
					mod.setFixed(false);
					break;
				}
		}
	}
	
	private int countChars( String seq, Aminoacid aa ) {
		char ch = Character.toUpperCase(aa.letter);
		char[] chars = seq.toUpperCase().toCharArray();
		int count = 0;
		for( int i = 0; i < chars.length; i++ )
			if( chars[i] == ch )
				count++;
		return count;
	}
	
	private final int proteinSubset;
}
