package es.ehubio.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.ehubio.collections.Identificator;
import es.ehubio.collections.Utils;
import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.io.CsvReader;
import es.ehubio.io.CsvUtils;

public class NesCombiner {
	private static class Result {
		public String getEntry() {
			return entry;
		}
		public void setEntry(String entry) {
			this.entry = entry;
		}
		public int getBegin() {
			return begin;
		}
		public void setBegin(int begin) {
			this.begin = begin;
		}
		public int getEnd() {
			return end;
		}
		public void setEnd(int end) {
			this.end = end;
		}
		public String getSeq() {
			return seq;
		}
		public void setSeq(String seq) {
			this.seq = seq;
		}
		public double getScore() {
			return score;
		}
		public void setScore(double score) {
			this.score = score;
		}
		private String entry;
		private int begin;
		private int end;
		private String seq;
		private double score;
	}
	
	private static List<Result> readWregex( String path ) throws IOException {		
		CsvReader csv = new CsvReader(",", true);
		csv.open(path);
		List<Result> results = new ArrayList<>();
		while( csv.readLine() != null ) {
			Result result = new Result();
			result.setEntry(csv.getField("Entry"));
			result.setBegin(csv.getIntField("Begin"));
			result.setEnd(csv.getIntField("End"));
			result.setSeq(csv.getField("Sequence"));
			result.setScore(csv.getDoubleField("Score"));
			results.add(result);
		}
		csv.close();
		return results;
	}
	
	private static List<Result> readNesMapper( String path ) throws IOException {		
		CsvReader csv = new CsvReader("\t", true);
		csv.open(path);
		List<Result> results = new ArrayList<>();
		while( csv.readLine() != null ) {
			if( csv.getLine().startsWith("Query") )
				break;
			Result result = new Result();
			result.setEntry(csv.getField("<query>"));
			result.setBegin(csv.getIntField("<pos>"));			
			result.setSeq(csv.getField("<nes>"));
			result.setScore(csv.getDoubleField("<score>"));
			result.setEnd(result.getBegin()+result.getSeq().length()-1);
			results.add(result);
		}
		csv.close();
		return results;
	}
	
	public static void main(String[] args) throws Exception {
		Map<String,Fasta> map = readFasta(REV_FASTA);
		List<Result> wregex = readWregex(WREGEX);
		List<Result> nesMapper = readNesMapper(NES_MAPPER);
		Set<Result> excluded = new HashSet<>();
		PrintWriter out = new PrintWriter(OUTPUT);
		printHeader(out);
		for( Result result : wregex ) {
			Result overlap = getOverlap(result,nesMapper);
			if( overlap != null )
				excluded.add(overlap);
			printResult(out,result,overlap,map);
		}
		for( Result result : nesMapper )
			if( !excluded.contains(result) )
				printResult(out,null,result,map);
		out.close();
	}	

	private static Map<String, Fasta> readFasta(String revFasta) throws Exception {
		if( revFasta == null )
			return null;
		Map<String, Fasta> map = Utils.getMap(Fasta.readEntries(REV_FASTA, SequenceType.PROTEIN),new Identificator<Fasta>() {
			@Override
			public String getId(Fasta fasta) {
				return fasta.getEntry();
			}
		});
		return map;
	}

	private static void printHeader(PrintWriter out) {
		out.print(CsvUtils.getCsv(SEP,
				"Entry", "Wregex pos", "NESmapper pos", "Wregex seq", "NESmapper seq", "Wregex score", "NESmapper score"));
		if( REV_FASTA != null ) {
			out.print(SEP);
			out.print("Wregex pos (orig)");
			out.print(SEP);
			out.print("NESmapper pos (orig)");
		}
		out.println();
	}

	private static void printResult(PrintWriter out, Result wregex, Result nesMapper, Map<String, Fasta> map) {
		String entry = wregex == null ? nesMapper.getEntry() : wregex.getEntry(); 
		out.print(CsvUtils.getCsv(SEP, entry,
				wregex == null ? "" : String.format("%d..%d", wregex.getBegin(), wregex.getEnd()),
				nesMapper == null ? "" : String.format("%d..%d", nesMapper.getBegin(), nesMapper.getEnd()),
				wregex == null ? "" : wregex.getSeq(),
				nesMapper == null ? "" : nesMapper.getSeq(),
				wregex == null ? "" : wregex.getScore(),
				nesMapper == null ? "" : nesMapper.getScore()
		));
		if( map != null ) {
			int len = map.get(entry).getSequence().length();
			out.print(SEP);
			out.print(wregex == null ? "" : String.format("%d..%d", len-wregex.getEnd()+1, len-wregex.getBegin()+1));
			out.print(SEP);
			out.print(nesMapper == null ? "" : String.format("%d..%d", len-nesMapper.getEnd()+1, len-nesMapper.getBegin()+1));
		}
		out.println();
	}

	private static Result getOverlap(Result item, List<Result> results) {
		for( Result result : results )
			if( result.getEntry().equals(item.getEntry()) &&
				(
					(result.getBegin() >= item.getBegin() && result.getBegin() <= item.getEnd())
					||
					(result.getEnd() >= item.getBegin() && result.getEnd() <= item.getEnd())
				) )
				return result;
		return null;
	}

	private static final String WREGEX = "/home/gorka/Bio/Proyectos/NES/Estudios/Wregex3.0/Joint/WregexRev.csv";
	private static final String NES_MAPPER = "/home/gorka/Bio/Proyectos/NES/Estudios/Wregex3.0/Joint/NESmapperRev.csv";
	private static final String OUTPUT = "/home/gorka/Bio/Proyectos/NES/Estudios/Wregex3.0/Joint/JoinedRev.csv";
	private static final String REV_FASTA = "/home/gorka/Bio/Proyectos/NES/Estudios/Wregex3.0/Joint/CargoCancer-UniProt.rev.fasta"; 
	private static final char SEP = '\t';
}
