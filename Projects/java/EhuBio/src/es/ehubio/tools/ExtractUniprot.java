package es.ehubio.tools;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.io.Streams;

public class ExtractUniprot {
	public static void main(String[] args) throws Exception {
		if ( args.length != 3 ) {
			System.out.println(String.format("Usage:\n%s <acc_list.txt> <file1.fasta> [<file2.fasta> ...]", ExtractUniprot.class.getName()));
			return;
		}
		String accPath = args[0];
		
		Map<String, Fasta> map = new HashMap<>();
		for( int i = 1; i < args.length; i++ )
			for( Fasta fasta : Fasta.readEntries(args[i], SequenceType.PROTEIN) )
				map.put(fasta.getAccession(), fasta);
		
		List<String> notFound = new ArrayList<>();
		try( BufferedReader br = new BufferedReader(Streams.getTextReader(accPath)) ) {
			String line;
			while( (line=br.readLine()) != null ) {
				Fasta fasta = map.get(line);
				if( fasta == null )
					notFound.add(line);
				else {
					System.out.print(">");
					System.out.println(fasta.getHeader());
					System.out.println(fasta.getSequence());
				}
			}
		}
		if( !notFound.isEmpty() ) {
			System.err.println("Entries not found:");
			for( String acc : notFound )
				System.err.println(acc);
		}
	}

}
