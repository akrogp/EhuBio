package es.ehubio.tools;

import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.io.GffFile;
import es.ehubio.io.GffFile.Feature;

public class Gff2Fasta {
	public static void main(String[] args) throws Exception {
		if( args.length != 1 ) {
			System.err.println(String.format("Usage:\n\t%s </path/file.gff>", Gff2Fasta.class.getName()));
			return;
		}
		
		GffFile gff = new GffFile();
		gff.readFile(args[0], SequenceType.PROTEIN);
		int i = 1;
		for( Feature f : gff.getFeatures() ) {
			System.out.println(String.format(">TRAIN_%03d %s", i, f.getSeqid()));
			System.out.println(gff.getFasta(f.getSeqid()).getSequence().substring(f.getStart()-1, f.getEnd()));
			i++;
		}
	}
}
