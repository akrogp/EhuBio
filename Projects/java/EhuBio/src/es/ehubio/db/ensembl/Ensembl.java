package es.ehubio.db.ensembl;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.dna.DnaUtils;
import es.ehubio.dna.GeneticCode;

public class Ensembl {
	public static EnsemblIds resolveSymbol( String symbol ) {
		JSONArray array = new JSONArray(
			newTarget("xrefs","symbol","homo_sapiens")
				.path(symbol)
				.request(MediaType.APPLICATION_JSON)
				.get(String.class)
			);
		EnsemblIds ids = new EnsemblIds();
		for( int i = 0; i < array.length(); i++ ) {
			JSONObject json = array.getJSONObject(i);
			String id = json.getString("id");
			if( !id.startsWith("ENS") )
				continue;
			String type = json.getString("type");			
			if( type.equals("gene") )
				ids.setGene(id);
			else if( type.equals("transcript") )
				ids.getTranscripts().add(id);
			else if( type.equals("translation") )
				ids.getProteins().add(id);
		}
		return ids;
	}
	
	public static String findCds( String enst ) {
		JSONArray array = new JSONArray(
				newTarget("xrefs","id")
					.path(enst)
					.queryParam("external_db", "CCDS")
					.request(MediaType.APPLICATION_JSON)
					.get(String.class)
				);
		if( array.length() == 0 )
			return null;
		JSONObject json = array.getJSONObject(0);
		return json.getString("display_id");
	}
	
	public static Fasta getCdsFasta( String cds ) throws InvalidSequenceException {
		String result = newTarget("sequence","id")
				.path(cds)
				.queryParam("object_type", "transcript")
				.queryParam("db_type", "otherfeatures")
				.queryParam("type", "cds")
				.queryParam("species", "human")
				.request("text/x-fasta")
				.get(String.class);
		Fasta fasta = new Fasta(result, SequenceType.DNA);
		return fasta;
	}
	
	public static String untranslate(String symbol, String protSequence) throws InvalidSequenceException {
		for( String enst : resolveSymbol(symbol).getTranscripts() ) {
			String cds = findCds(enst);
			if( cds == null )
				continue;
			Fasta fasta = getCdsFasta(cds);
			String translation = DnaUtils.translate(fasta.getSequence(),GeneticCode.getStandard());
			int pos = translation.indexOf(protSequence);					
			if( pos >= 0 ) {
				int end = (pos+protSequence.length())*3;
				return fasta.getSequence().substring(pos*3,end);
			}
		}
		return null;
	}
	
	private static WebTarget newTarget( String... paths) {
		WebTarget target = ClientBuilder.newClient().target(ENDPOINT);
		for( String path : paths )
			target = target.path(path);
		return target;
	}
	
	private static final String ENDPOINT = "https://rest.ensembl.org"; 
}
