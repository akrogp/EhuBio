package es.ehubio.wregex.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;

import es.ehubio.db.cosmic.CosmicStats;
import es.ehubio.db.cosmic.Locus;
import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.dbptm.ProteinPtms;
import es.ehubio.dbptm.Ptm;
import es.ehubio.model.Aminoacid;
import es.ehubio.wregex.InputGroup;
import es.ehubio.wregex.Pssm;
import es.ehubio.wregex.PssmBuilder.PssmBuilderException;
import es.ehubio.wregex.Result;
import es.ehubio.wregex.ResultGroup;
import es.ehubio.wregex.Wregex;

public class Services {
	private final ExternalContext context;
	
	public Services( ExternalContext context ) {
		this.context = context;
	}
	
	public static List<ResultGroupEx> search(
			Wregex wregex, MotifInformation motif, List<InputGroup> inputGroups, boolean assayScores, long tout ) throws Exception {
		List<ResultGroupEx> resultGroupsEx = new ArrayList<>();
		List<ResultGroup> resultGroups;
		ResultGroupEx resultGroupEx;
		long wdt = System.currentTimeMillis() + tout;
		for( InputGroup inputGroup : inputGroups ) {
			try {
				if( assayScores )
					resultGroups = wregex.searchGroupingAssay(inputGroup);
				else
					resultGroups = wregex.searchGrouping(inputGroup.getFasta());
			} catch( Exception e ) {
				continue;
			}
			for( ResultGroup resultGroup : resultGroups ) {
				resultGroupEx = new ResultGroupEx(resultGroup);
				resultGroupEx.setWregex(wregex);
				if( motif != null ) {
					resultGroupEx.setMotif(motif.getName());
					resultGroupEx.setMotifUrl(motif.getReferences().get(0).getLink());
				}
				resultGroupsEx.add(resultGroupEx);
			}
			if( tout > 0 && System.currentTimeMillis() >= wdt )
				throw new Exception("Too intensive search, try a more strict regular expression or a smaller fasta file");
		}
		return resultGroupsEx;
	}
	
	public List<ResultGroupEx> searchAll(
			List<MotifInformation> motifs, List<InputGroup> inputGroups, long tout ) throws Exception {
		List<ResultGroupEx> results = new ArrayList<>();
		MotifDefinition def;
		Pssm pssm;
		Wregex wregex;
		for( MotifInformation motif : motifs ) {
			def = motif.getDefinitions().get(0);
			pssm = getPssm(def.getPssm());
			wregex = new Wregex(def.getRegex(), pssm);
			results.addAll(search(wregex, motif, inputGroups, false, tout));
		}
		return results;
	}
		
	public static List<ResultEx> expand(List<ResultGroupEx> resultGroups, boolean grouping) {
		List<ResultEx> results = new ArrayList<>();
		for( ResultGroupEx resultGroup : resultGroups ) {
			if( grouping )
				results.add(resultGroup.getRepresentative());
			else
				for( ResultEx r : resultGroup )
					results.add(r);
		}
		return results;
	}
	
	public static void searchAux(Wregex wregex, List<ResultEx> results) {
		for( ResultEx result : results ) {
			List<ResultGroup> groups = wregex.searchGrouping(result.getResult().getFasta());
			if( wregex.getPssm() == null ) {
				result.setAuxScore((double)groups.size());
				continue;
			}
			double max = 0.0;
			for( ResultGroup group : groups )
				if( group.getScore() > max )
					max = group.getScore();
			result.setAuxScore(max);
		}
	}
	
	public static void searchCosmic(Map<String,CosmicStats> cosmic, List<ResultEx> results, boolean expand ) {
		if( expand )
			searchCosmicExt(cosmic, results);
		else
			searchCosmicBasic(cosmic, results);			
	}
	
	public static void searchCosmicBasic(Map<String,CosmicStats> cosmic, List<ResultEx> results) {
		int missense;
		boolean invalid;
		for( ResultEx result : results ) {
			CosmicStats stats = cosmic.get(result.getGene());
			if( stats == null )
				continue;			
			missense = 0;
			invalid = false;
			for( Locus locus : stats.getLoci().values() ) {
				if( !checkValid(locus, result) ) {
					invalid = true;
					break;
				}
				if( locus.getPosition() >= result.getStart() && locus.getPosition() <= result.getEnd() )
					missense += locus.getTotalMutationCount();
			}
			if( invalid )
				result.setCosmicUrl(getCosmicUrl(result));
			else {
				result.setCosmicUrl(getCosmicUrlZoom(result));
				result.setCosmicMissense(missense);
			}
		}
	}
	
	private static boolean checkValid( Locus locus, ResultEx result ) {
		if( locus.getPosition() > result.getFasta().getSequence().length() ||
			locus.getOriginal() != Aminoacid.parseLetter(result.getFasta().getSequence().charAt(locus.getPosition()-1)) )
			return false;
		return true;
	}
	
	private static String getCosmicUrl(ResultEx result) {
		return String.format("http://cancer.sanger.ac.uk/cosmic/gene/analysis?ln=%s&mut=%s",
			result.getGene(), "substitution_missense");
	}
	
	private static String getCosmicUrlZoom(ResultEx result) {
		return String.format("http://cancer.sanger.ac.uk/cosmic/gene/analysis?ln=%s&start=%d&end=%d&mut=%s",
			result.getGene(), result.getStart(), result.getEnd(), "substitution_missense");
	}
	
	public static void searchCosmicExt(Map<String,CosmicStats> cosmic, List<ResultEx> orig) {
		List<ResultEx> results = new ArrayList<>();
		boolean invalid;
		for( ResultEx item : orig ) {
			CosmicStats stats = cosmic.get(item.getGene());
			if( stats == null ) {
				results.add(item);
				continue;
			}
			invalid = false;
			for( Locus locus : stats.getLoci().values() ) {
				if( !checkValid(locus, item) ) {
					invalid = true;
					break;
				}
				if( locus.getPosition() >= item.getStart() && locus.getPosition() <= item.getEnd() ) {
					results.addAll(searchMutations(locus, item));
				}
			}
			if( invalid )
				item.setCosmicUrl(getCosmicUrl(item));
		}
		orig.clear();
		orig.addAll(results);
	}
	
	private static List<ResultEx> searchMutations( Locus locus, ResultEx item ) {
		List<ResultEx> results = new ArrayList<>();
		//result.add(item);
		for( Aminoacid aa : locus.getMutations() ) {
			Wregex wregex = item.getWregex();
			StringBuilder str = new StringBuilder(item.getMatch().toLowerCase());
			str.setCharAt(locus.getPosition()-item.getStart(), aa.letter);
			try {
				ResultEx result = new ResultEx(item);
				Fasta fasta = new Fasta(item.getFasta().getHeader(), str.toString(), SequenceType.PROTEIN);
				List<ResultGroup> groups = wregex.searchGrouping(fasta);				
				if( groups == null || groups.isEmpty() ) {
					result.setMutSequence(String.format("lost! -> %s", fasta.getSequence()));
					result.setMutScore(-item.getScore());
				} else {
					Result mut = groups.iterator().next().getRepresentative();
					result.setMutSequence(mut.getAlignment());
					result.setMutScore(mut.getScore()-item.getScore());
				}												
				result.setCosmicUrl(getCosmicUrlZoom(result));
				result.setCosmicMissense(locus.getMutationCount(aa));				
				results.add(result);
			} catch (InvalidSequenceException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
	
	public static void searchDbPtm(Map<String, ProteinPtms> dbPtm, List<ResultEx> results) {
		int count;
		for( ResultEx result : results ) {
			ProteinPtms ptms = dbPtm.get(result.getAccession());
			if( ptms == null )
				continue;
			count = 0;
			for( Ptm ptm : ptms.getPtms().values() )
				if( ptm.position >= result.getStart() && ptm.position <= result.getEnd() )
					count += ptm.count;
			result.setDbPtmUrl(String.format(
				"http://dbptm.mbc.nctu.edu.tw/search_result.php?search_type=db_id&swiss_id=%s",ptms.getId()));
			result.setDbPtms(count);			
		}
	}
	
	public Reader getResourceReader( String resource ) {
		return new InputStreamReader(context.getResourceAsStream("/resources/"+resource));
	}
	
	public Pssm getPssm( String name ) throws IOException, PssmBuilderException {
		if( name == null )
			return null;
		Reader rd = getResourceReader("data/"+name);
		Pssm pssm = Pssm.load(rd, true);
		rd.close();
		return pssm;
	}
	
	public long getInitNumber( String param ) {
		return Long.parseLong(context.getInitParameter(param));
	}	
}
