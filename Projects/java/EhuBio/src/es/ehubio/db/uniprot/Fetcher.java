package es.ehubio.db.uniprot;

import java.io.IOException;

import javax.ws.rs.client.ClientBuilder;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;

public class Fetcher {
	public static Fasta downloadFasta( String acc, SequenceType type ) throws InvalidSequenceException {
		String str = downloadFasta(acc);
		String[] lines = str.split("\\n");
		String header = lines[0].substring(1);
		StringBuilder seq = new StringBuilder();
		for( int i = 1; i < lines.length; i++ )
			seq.append(lines[i]);
		return new Fasta(header, seq.toString(), type);
	}
	
	public static String downloadFasta( String acc ) {
		return download(acc, "fasta");
	}
	
	public static String queryFasta(String query) throws IOException {
		return ClientBuilder.newClient()
			.target("https://rest.uniprot.org/uniprotkb/stream")
			//.queryParam("compressed", "true")
			.queryParam("format", "fasta")
			.queryParam("query", String.format("(%s)", query))			
			.request()
			.get(String.class);
	}
	
	private static String download( String acc, String ext ) {
		return ClientBuilder.newClient()
			.target(String.format("https://www.uniprot.org/uniprot/%s.%s", acc, ext))
			.request().get(String.class).trim();			
	}
}
