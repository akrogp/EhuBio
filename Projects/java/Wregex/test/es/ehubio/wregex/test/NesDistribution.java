package es.ehubio.wregex.test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;

public class NesDistribution {
	public static void main(String[] args) throws Exception {
		Pattern pattern = Pattern.compile("[a-z]+");		
		List<Fasta> fastas = Fasta.readEntries(PATH, SequenceType.PROTEIN);
		for( Fasta fasta : fastas ) {
			if( fasta.getSequence().length() < 300 || fasta.getSequence().length() > 500 )
				continue;
			/*if( fasta.getSequence().length() < 500 || fasta.getSequence().length() > 700 )
				continue;*/
			Matcher matcher = pattern.matcher(fasta.getSequence());
			/*if( matcher.find() )
				System.out.println(String.format("%s %d", fasta.getEntry(), fasta.getSequence().length()));*/
			int pos = 0;
			while( matcher.find(pos) ) {
				pos = matcher.start();
				System.out.println(String.format("%s %s %d %d", fasta.getEntry(), matcher.group(), pos, fasta.getSequence().length()));
				pos = matcher.end();
			}
		}
	}
	
	private static final String PATH = "/home/gorka/Bio/Proyectos/NES/Data/NES/NESdb_formatted.fasta";
}
