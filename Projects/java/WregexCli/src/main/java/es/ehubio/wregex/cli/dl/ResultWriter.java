package es.ehubio.wregex.cli.dl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import es.ehubio.wregex.Result;

public class ResultWriter {
	public static Writer open(String path) throws IOException {
		return path == null ? new OutputStreamWriter(System.out) : new FileWriter(path);
	}

	public static void write(String format, Writer writer, List<Result> results) {
		switch (format) {
			case "fasta":
				writeFasta(writer, results);
				break;
			case "aln":
				Result.saveAln(writer, results);
				break;
			default:
				writeCsv(writer, results);
		}
	}

	private static void writeCsv(Writer writer, List<Result> results) {
		PrintWriter pw = new PrintWriter(writer);
		pw.println("entry,start,end,match,alignment,score,combinations");
		for (Result result : results)
			pw.println(String.join(",",
					result.getEntry(),
					String.valueOf(result.getStart()),
					String.valueOf(result.getEnd()),
					result.getMatch(),
					result.getAlignment(),
					String.valueOf(result.getScore()),
					String.valueOf(result.getCombinations())));
		pw.flush();
	}

	private static void writeFasta(Writer writer, List<Result> results) {
		PrintWriter pw = new PrintWriter(writer);
		for (Result result : results) {
			pw.println(">" + result.getName() + " score=" + result.getScore());
			pw.println(result.getMatch());
		}
		pw.flush();
	}
}
