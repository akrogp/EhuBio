package es.ehubio.proteomics.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;

public class Inferer {
	//private final static Logger logger = Logger.getLogger(Inferer.class.getName());
	
	public static void relink(MsMsData data, String fastaPath, Digester.Config digestion, Searcher.Config searching) throws IOException, InvalidSequenceException{
		//logger.info("Updating peptide-protein relations in MS/MS Data ...");
		Map<String, List<Protein>> map = getMap(fastaPath, digestion, searching);		
		data.getProteins().clear();
		data.getGroups().clear();
		for( Peptide peptide : data.getPeptides() ) {
			if( peptide.getSequence().length() > searching.getMaxLength() || peptide.getSequence().length() < searching.getMinLength() )
				continue;
			//int prev = peptide.getProteins().size();
			peptide.getProteins().clear();
			for( Protein protein : map.get(peptide.getSequence().toLowerCase()) )
				protein.linkPeptide(peptide);
			/*if( peptide.getProteins().size() != prev )
				logger.info(String.format("Peptide %s: %d->%d", peptide.getSequence(), prev, peptide.getProteins().size()));*/
			data.getProteins().addAll(peptide.getProteins());
		}
	}
	
	private static List<Protein> loadFasta( String fastaPath ) throws IOException, InvalidSequenceException {
		//logger.info("Loading proteins from fasta file ...");
		List<Protein> proteins = new ArrayList<>();
		List<Fasta> fastas = Fasta.readEntries(fastaPath, SequenceType.PROTEIN);
		for( Fasta fasta : fastas ) {
			Protein protein = new Protein();
			protein.setFasta(fasta);
			proteins.add(protein);
		}
		return proteins;
	}
	
	private static Map<String, List<Protein>> getMap( String fastaPath, Digester.Config digestion, Searcher.Config searching ) throws IOException, InvalidSequenceException {
		List<Protein> proteins = loadFasta(fastaPath);
		return createMap(proteins, digestion, searching);
		/*String cachePath = String.format("%s.relations.gz", fastaPath.replaceAll("\\..*", ""));
		Map<String, List<Protein>> map = loadCache(cachePath,proteins,config);
		if( map == null ) {
			map = createMap(proteins, config);
			saveCache(cachePath,map,config);
		}
		return map;*/		
	}
	
	/*private static Map<String, List<Protein>> loadCache(String cachePath, List<Protein> proteins, Digester.Config config) throws IOException {
		File file = new File(cachePath);
		if( !file.exists() )
			return null;
		Map<String, List<Protein>> mapPeptides = null;
		BufferedReader rd = new BufferedReader(Streams.getTextReader(file));
		if( !rd.readLine().equals("Relations version:1.0") ||
			!rd.readLine().equals(String.format("enzyme:%s", config.getEnzyme().getDescription())) ||
			!rd.readLine().equals(String.format("missCleavages:%s", config.getMissedCleavages())) ||
			!rd.readLine().equals(String.format("Asp-Pro:%s", config.isUsingDP())) ||
			!rd.readLine().equals(String.format("N-term cut MX:%d", config.getCutNterm())) ) {
			logger.info("Discarded saved peptide-protein relations");
		} else {
			logger.info("Loading saved peptide-protein relations ...");
			Map<String, Protein> mapProteins = new HashMap<>();
			for( Protein protein : proteins )
				mapProteins.put(protein.getAccession(), protein);
			mapPeptides = new HashMap<>();
			String line, fields[];
			while( (line=rd.readLine()) != null ) {
				fields = line.split(":");
				List<Protein> list = new ArrayList<>();
				for( String protein : fields[1].split(",") )
					list.add(mapProteins.get(protein));
				mapPeptides.put(fields[0], list);
			}
		}
		rd.close();
		return mapPeptides;
	}

	private static void saveCache(String cachePath, Map<String, List<Protein>> map, Digester.Config config) throws FileNotFoundException, IOException {
		logger.info("Saving peptide-protein relations for future use ...");
		PrintWriter pw = new PrintWriter(new BufferedWriter(Streams.getTextWriter(cachePath)));
		pw.println("Relations version:1.0");
		pw.println(String.format("enzyme:%s", config.getEnzyme().getDescription()));
		pw.println(String.format("missCleavages:%s", config.getMissedCleavages()));
		pw.println(String.format("Asp-Pro:%s", config.isUsingDP()));
		pw.println(String.format("N-term cut MX:%d", config.getCutNterm()));
		for( Entry<String, List<Protein>> entry : map.entrySet() )
			pw.println(String.format("%s:%s", entry.getKey(), CsvUtils.getCsv(',', entry.getValue().toArray())));
		pw.close();
	}*/

	private static Map<String, List<Protein>> createMap( List<Protein> proteins, Digester.Config digestion, Searcher.Config searching ) {
		Map<String, List<Protein>> map = new HashMap<>();
		for( Protein protein : proteins )
			for( String pepSeq : Digester.digestSequence(protein.getSequence(), digestion) ) {
				if( pepSeq.length() < searching.getMinLength() || pepSeq.length() > searching.getMaxLength() )
					continue;
				List<Protein> list = map.get(pepSeq.toLowerCase());
				if( list == null ) {
					list = new ArrayList<>();
					map.put(pepSeq.toLowerCase(), list);
				}
				list.add(protein);
			}
		return map;
	}
}
