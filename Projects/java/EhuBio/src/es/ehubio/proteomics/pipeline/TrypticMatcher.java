package es.ehubio.proteomics.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.model.Aminoacid;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;

public class TrypticMatcher implements RandomMatcher {
	private TrypticMatcher( Digester.Config digestion, Searcher.Config searching ) {
		this.digestion = digestion;
		this.searching = searching;
	}
	
	public TrypticMatcher( String fastaPath, String decoyPath, String decoyPrefix, Digester.Config digestion, Searcher.Config searching ) throws IOException, InvalidSequenceException {
		this(digestion,searching);
		List<Fasta> targetFastas;
		List<Fasta> decoyFastas;
		if( decoyPath != null ) {
			targetFastas = Fasta.readEntries(fastaPath, SequenceType.PROTEIN);
			decoyFastas = Fasta.readEntries(decoyPath, SequenceType.PROTEIN);
		} else {
			targetFastas = new ArrayList<>();
			decoyFastas = new ArrayList<>();
			for( Fasta fasta : Fasta.readEntries(fastaPath, SequenceType.PROTEIN) )
				if( fasta.getAccession().contains(decoyPrefix) )
					decoyFastas.add(fasta);
				else
					targetFastas.add(fasta);
		}
		targetResults = createMq("target",targetFastas);
		decoyResults = createMq("decoy",decoyFastas);
	}
	
	/*public TrypticMatcher( Collection<Protein> proteins, String decoyPrefix, double decoys, double redundantDecoys, Digester.Config digestion, Searcher.Config searching ) throws IOException, InvalidSequenceException {
		this(decoyPrefix,decoys,redundantDecoys,digestion,searching);		
		List<Fasta> decoyFastas = new ArrayList<>();
		for( Protein protein : proteins )
			if( protein.isDecoy() )
				decoyFastas.add(new Fasta(protein.getAccession(), protein.getDescription(), protein.getSequence(), SequenceType.PROTEIN));
		createMq(decoyFastas, decoyPrefix);
	}*/
	
	public TrypticMatcher( Collection<Protein> proteins, Digester.Config digestion, Searcher.Config searching ) {
		this(digestion,searching);               
		List<Fasta> targetFastas = new ArrayList<>();
		List<Fasta> decoyFastas = new ArrayList<>();
		for( Protein protein : proteins ) {
			Fasta fasta = new Fasta(protein.getAccession(), protein.getDescription(), protein.getSequence(), SequenceType.PROTEIN);
			if( protein.isTarget() )
				targetFastas.add(fasta);
			else
				decoyFastas.add(fasta);
		}
		targetResults = createMq("target",targetFastas);
		decoyResults = createMq("decoy",decoyFastas);
	}
		
	private Map<String, Result> createMq(String title, List<Fasta> fastas) {
		Map<String, Result> results = new HashMap<>();
		List<Protein> proteins = digestDb(title, fastas);                      
		for( Protein protein : proteins ) {
			if( protein.getAccession().contains("decoy-Q16236-3") )
				System.out.println();
			double Mq = 0.0;
			double Nq = 0.0;
			for( Peptide peptide : protein.getPeptides() ) {
				if( peptide.getProteins().isEmpty() )
					throw new AssertionError("This should not happen");
				double tryptic = (double)getTryptic(peptide.getSequence());
				Nq += tryptic;
				Mq += tryptic/peptide.getProteins().size();                             
			}
			results.put(protein.getAccession(), new Result(Nq, Mq));                     
		}
		return results;
	}
	
	private List<Protein> digestDb(String title, List<Fasta> proteins) {
		logger.info(String.format("Building a %s dataset with all observable peptides for calculating expected Mq values ...",title));
		Map<String,Peptide> mapPeptides = new HashMap<>();
		List<Protein> list = new ArrayList<>();		
		for( Fasta protein : proteins ) {
			Set<String> pepSequences = Digester.digestSequence(protein.getSequence(), digestion);
			Protein protein2 = new Protein();
			protein2.setAccession(protein.getAccession());
			for( String pepSequence : pepSequences ) {
				if( pepSequence.length() < searching.getMinLength() || pepSequence.length() > searching.getMaxLength() )
					continue;
				Peptide peptide = mapPeptides.get(pepSequence);
				if( peptide == null ) {
					peptide = new Peptide();
					peptide.setSequence(pepSequence);
					mapPeptides.put(pepSequence, peptide);
				}				
				protein2.linkPeptide(peptide);
			}
			list.add(protein2);
		}		
		return list;
	}

	@Override
	public Result getExpected(Protein protein) {
		return protein.isDecoy() ? decoyResults.get(protein.getAccession()) : targetResults.get(protein.getAccession());
	}
	
	private long getTryptic( String peptide ) {
		if( peptide.length() < searching.getMinLength() || peptide.length() > searching.getMaxLength() )
			return 0;
		if( searching.getVarMods().isEmpty() )
			return 1;
		int n = 0;
		for( Aminoacid aa : searching.getVarMods() )
			n += Math.min(countChars(peptide, aa), searching.getMaxMods())+1;
		//return getCombinations(n);
		return n;
	}
	
	private long countChars( String seq, Aminoacid aa ) {
		char ch = Character.toUpperCase(aa.letter);
		char[] chars = seq.toUpperCase().toCharArray();
		long count = 0;
		for( int i = 0; i < chars.length; i++ )
			if( chars[i] == ch )
				count++;
		if( count >= countWarning )
			logger.warning(String.format("%s,%s",count,seq));
		return count;
	}
	
	/*private long getCombinations( int n ) {
		long result = 1;
		int kmax = n < maxMods ? n : maxMods;
		for( int k = 1; k <= kmax; k++ )			
			result += CombinatoricsUtils.binomialCoefficient(n, k);
		return result;
	}*/

	private final static Logger logger = Logger.getLogger(TrypticMatcher.class.getName());
	private final static int countWarning = 20;
	private final Digester.Config digestion;
	private final Searcher.Config searching;		
	private Map<String, Result> targetResults, decoyResults;
}
