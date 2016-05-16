package es.ehubio.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.io.Streams;

public class MotifExtender {
	public static void main( String[] args ) throws IOException, InvalidSequenceException {
		List<Fasta> fastas = Fasta.readEntries(FASTA, SequenceType.PROTEIN);
		Map<String,Fasta> map = new HashMap<>(fastas.size());
		for( Fasta fasta : fastas )
			map.put(fasta.getEntry(), fasta);
		for( String id : Streams.readLines(DATA) ) {
			String[] fields = id.split("@");
			Fasta fasta = map.get(fields[0]);
			fields = fields[1].split("-");
			int from = Integer.parseInt(fields[0])-1;
			int to = Integer.parseInt(fields[1])-1;
			int more = SIZE-(to-from+1);
			if( more > 0 ) {
				from -= more/2;
				if( from < 0 )
					from = 0;
				to = from+SIZE-1;
			}
			System.out.println(fasta.getSequence().substring(from, to+1));
		}
		//positives();
	}
	
	public static void positives() throws FileNotFoundException, IOException {
		Map<String,String> map = new HashMap<>();
		for( String positive : Streams.readLines(POSITIVES) ) {
			String[] fields = positive.split("@");
			map.put(fields[0], fields[1]);
		}
		for( String candidate : Streams.readLines(DATA) ) {
			String positive = map.get(candidate.split("\\|")[1]);
			int ok = positive == null ? 0 : checkRange(candidate.split("@")[1],positive);
			//System.out.println(String.format("%s\t%d", candidate, ok));
			System.out.println(ok);
		}
	}

	private static int checkRange(String candidate, String positives) {
		String[] fields = candidate.split("-");
		int cfrom = Integer.parseInt(fields[0]);
		int cto = Integer.parseInt(fields[1]);
		for( String positive : positives.split(";") ) {
			fields = positive.split("-");
			int pfrom = Integer.parseInt(fields[0]);
			int pto = Integer.parseInt(fields[1]);
			if( cfrom >= pfrom && cfrom <= pto )
				return 1;
			if( cto >= pfrom && cto <= pto)
				return 1;
		}
		return 0;
	}

	private static final String FASTA = "/home/gorka/Bio/Proyectos/NES/Data/NES/ValidNES.fasta";
	private static final String DATA = "/home/gorka/Bio/Proyectos/NES/Estudios/Wregex2.0/vsNESsential/ids.txt";
	private static final String POSITIVES = "/home/gorka/Bio/Proyectos/NES/Estudios/Wregex2.0/vsNESsential/positives.txt";
	private static final int SIZE = 20;
}
