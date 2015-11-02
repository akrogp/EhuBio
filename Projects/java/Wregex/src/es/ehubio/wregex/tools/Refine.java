package es.ehubio.wregex.tools;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.io.Streams;
import es.ehubio.wregex.Pssm;
import es.ehubio.wregex.ResultGroup;
import es.ehubio.wregex.Wregex;

public class Refine {
	public static void main( String[] args ) throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		
		String fastaPath = "/media/data/Sequences/UniProt/current/SP_HUMAN.fasta.gz";
		String inputPath = "/home/gorka/Bio/Proyectos/NES/Estudios/Wregex2.0/candidates2/Wregex/SP_HUMAN.csv.gz";
		String outputPath = "/home/gorka/Bio/Proyectos/NES/Estudios/Wregex2.0/candidates2/Wregex/out2.csv";
		
		Map<String, String> mapSequences = new HashMap<>();
		for( Fasta fasta : Fasta.readEntries(fastaPath, SequenceType.PROTEIN) )
			mapSequences.put(fasta.getEntry(), fasta.getSequence());
		
		String regex = "([DEQS].{0,1})([LIMA])(.{2,3})([LIVMF])([^P]{2,3})([LMVF])([^P])([LMIV])(.{0,3}[DEQ])";
		Pssm pssm = Pssm.load("/home/gorka/MyProjects/EhuBio/Projects/java/Wregex/WebContent/resources/data/NES-total.pssm", true);
		Wregex wregex = new Wregex(regex, pssm);
		
		BufferedReader br = new BufferedReader(Streams.getTextReader(inputPath));
		PrintWriter pw = new PrintWriter(Streams.getTextWriter(outputPath));
		String line;
		String[] fields;
		boolean first = true;
		while( (line=br.readLine()) != null ) {			
			if( first ) {
				first = false;
				line += ",Extended sequence,Estrict score";
			} else {				
				fields = line.split(",");
				String seq = mapSequences.get(fields[2]);
				int from = Integer.parseInt(fields[4])-1-5;
				int to = Integer.parseInt(fields[5])-1+5;
				if( from < 0 ) from = 0;
				if( to > seq.length() ) to = seq.length();
				seq = seq.substring(from, to);
				List<ResultGroup> results = wregex.searchGrouping(new Fasta("", "", seq, SequenceType.PROTEIN));
				double score = -1;
				if( !results.isEmpty() )
					score = results.get(0).getScore();
				line = String.format("%s,%s,%f", line, seq, score);
			}
			pw.println(line);
		}
		br.close();
		pw.close();
		System.out.println("finished!");
	}
}
