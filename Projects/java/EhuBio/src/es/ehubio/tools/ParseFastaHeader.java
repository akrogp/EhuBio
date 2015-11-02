package es.ehubio.tools;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.HeaderParser;

public class ParseFastaHeader implements Command.Interface {

	@Override
	public String getUsage() {
		return "<fasta header>";
	}

	@Override
	public int getMinArgs() {
		return 1;
	}

	@Override
	public int getMaxArgs() {
		return 1;
	}

	@Override
	public void run(String[] args) throws Exception {
		HeaderParser header = Fasta.guessParser(args[0]);
		if( header == null )
			System.out.println("Unknown header");
		else {
			System.out.println("Accession: "+header.getAccession());
			System.out.println("Name: "+header.getProteinName());
			System.out.println("Description: "+header.getDescription());
		}
	}

}
