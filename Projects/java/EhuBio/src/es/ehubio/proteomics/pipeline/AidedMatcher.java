package es.ehubio.proteomics.pipeline;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import es.ehubio.io.CsvReader;
import es.ehubio.io.Streams;
import es.ehubio.model.Aminoacid;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;

public class AidedMatcher implements RandomMatcher {
	public AidedMatcher(String targetRelationsPath, String decoyRelationsPath, Searcher.Config searching ) throws FileNotFoundException, IOException {
        this.searching = searching;
        targetResults = createMq(loadProteins(targetRelationsPath));
        decoyResults = createMq(loadProteins(decoyRelationsPath));
	}
	
	private Collection<Protein> loadProteins(String relationsPath) throws FileNotFoundException, IOException {
		logger.info("Loading peptides used by the search engine for Mq calculation ...");
		Map<String,Protein> map = new HashMap<>();
		CsvReader csv = new CsvReader(" ", false, true);
		csv.open(Streams.getTextReader(relationsPath));
		while( csv.readLine() != null ) {
			Peptide peptide = new Peptide();
			peptide.setUniqueString(csv.getField(0));
			peptide.setSequence(csv.getField(0).replaceAll("\\[.*?\\]",""));
			for( String acc : csv.getField(1).split(";") ) {
				Protein protein = map.get(acc);
				if( protein == null ) {
					protein = new Protein();
					protein.setAccession(acc);
					map.put(acc, protein);
				}
				protein.linkPeptide(peptide);
			}
		}
		csv.close();
		return map.values();
	}
	
	private Map<String, Result> createMq( Collection<Protein> proteins ) {
		Map<String, Result> results = new HashMap<>();
		for( Protein protein : proteins ) {
			double Mq = 0.0;
			double Nq = 0.0;
			Set<String> peptides = new HashSet<>();
			for( Peptide peptide : protein.getPeptides() ) {
				if( !peptides.add(peptide.getSequence().toLowerCase()) )
					continue;
				double tryptic = (double)getTryptic(peptide.getSequence());
				Nq += tryptic;
				Mq += tryptic/peptide.getProteins().size();                             
			}
			results.put(protein.getAccession(), new Result(Nq, Mq));
		}
		return results;
	}
	
	private long getTryptic( String peptide ) {
		if( searching.getVarMods().isEmpty() )
			return 1;
		int n = 0;
		for( Aminoacid aa : searching.getVarMods() )
			n += Math.min(countChars(peptide, aa), searching.getMaxMods())+1;
		return n;
	}
	
	private long countChars( String seq, Aminoacid aa ) {
		char ch = Character.toUpperCase(aa.letter);
		char[] chars = seq.toUpperCase().toCharArray();
		long count = 0;
		for( int i = 0; i < chars.length; i++ )
			if( chars[i] == ch )
				count++;
		return count;
	}

	@Override
	public Result getExpected(Protein protein) {
		return protein.isDecoy() ? decoyResults.get(protein.getAccession()) : targetResults.get(protein.getAccession());
	}

	private final static Logger logger = Logger.getLogger(AidedMatcher.class.getName());
	private Map<String, Result> targetResults, decoyResults;
	private final Searcher.Config searching;
}
