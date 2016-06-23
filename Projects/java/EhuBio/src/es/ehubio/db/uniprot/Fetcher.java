package es.ehubio.db.uniprot;

import javax.ws.rs.client.ClientBuilder;

public class Fetcher {
	public static String downloadFasta( String acc ) {
		return download(acc, "fasta");
	}
	
	private static String download( String acc, String ext ) {
		return ClientBuilder.newClient()
			.target(String.format("http://www.uniprot.org/uniprot/%s.%s", acc, ext))
			.request().get(String.class).trim();			
	}
}
