package es.ehubio.tools;

import java.util.logging.Logger;

import es.ehubio.db.ensembl.Ensembl;
import es.ehubio.db.uniprot.UniProtUtils;
import es.ehubio.io.CsvReader;

public class Untraslator {
	public static void main(String[] args) throws Exception {
		CsvReader input = new CsvReader(",", false);
		input.open(INPUT);
		while( input.readLine() != null ) {
			String acc = input.getField(0);//input.getField(0).split("\\|")[1];
			String symbol = UniProtUtils.canonicalAccesion(acc); 
			String protSeq = input.getField(1).toUpperCase();
			String dna = Ensembl.untranslate(symbol, protSeq);
			if( dna == null )
				log.severe(String.format("Could not find original CDS for %s", acc));
			else
				System.out.println(String.format("%s:\t%s", acc, dna));
				//System.out.println(dna);
		}
		input.close();
	}
	
	private static final Logger log = Logger.getLogger(Untraslator.class.getName());
	private static final String INPUT = "/home/gorka/input.csv";
}
