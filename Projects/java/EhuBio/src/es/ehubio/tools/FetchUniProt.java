package es.ehubio.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import es.ehubio.db.uniprot.Fetcher;
import es.ehubio.io.Streams;

public class FetchUniProt {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		if ( args.length != 1 ) {
			System.err.println(String.format("Usage:\n%s <acc_list.txt>", FetchUniProt.class.getName()));
			return;
		}
		
		for( String acc : Streams.readLines(args[0]) ) {
			logger.info(acc);
			System.out.println(Fetcher.downloadFasta(acc));
		}
	}
	
	private final static Logger logger = Logger.getLogger(FetchUniProt.class.getName());
}
