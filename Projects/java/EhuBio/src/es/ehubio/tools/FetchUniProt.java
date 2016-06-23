package es.ehubio.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import es.ehubio.db.uniprot.Fetcher;
import es.ehubio.io.Streams;

public class FetchUniProt {
	public static void main(String[] args) throws FileNotFoundException, IOException {	
		PrintWriter pw = new PrintWriter(OUTPUT);
		for( String acc : Streams.readLines(INPUT) ) {
			logger.info(acc);
			pw.println(Fetcher.downloadFasta(acc));
		}
		pw.close();
	}
	
	private final static Logger logger = Logger.getLogger(FetchUniProt.class.getName());
	private static final String INPUT = "/home/gorka/Bio/Proyectos/NES/Estudios/Wregex3.0/Joint/CargoCancer-UniProt.txt";
	private static final String OUTPUT = "/home/gorka/Bio/Proyectos/NES/Estudios/Wregex3.0/Joint/CargoCancer-UniProt.fasta";
}
