package es.ehubio.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.io.CsvReader;

public class FastaCutter {
	public static void main(String[] args) throws Exception {
		List<Fasta> fastas = Fasta.readEntries(FASTA, SequenceType.PROTEIN);
		Map<String, Fasta> map = new HashMap<>(fastas.size());
		for( Fasta fasta : fastas )
			map.put(fasta.getEntry(), fasta);
		CsvReader input = new CsvReader(",", false);
		input.open(INPUT);
		while( input.readLine() != null ) {
			Fasta fasta = map.get(input.getField(0));
			System.out.println(fasta.getSequence().substring(input.getIntField(1)-1, input.getIntField(2)));
		}
		input.close();
	}
	
	private static final String FASTA = "/home/gorka/Bio/Proyectos/NES/Estudios/Wregex3.0/Joint/CargoCancer-UniProt.fasta";
	private static final String INPUT = "/home/gorka/input.csv";
}
