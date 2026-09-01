package es.ehubio.wregex.cli.pl;

import java.io.PrintStream;
import java.util.List;

import es.ehubio.wregex.cli.dl.MotifDefinition;
import es.ehubio.wregex.cli.dl.MotifInformation;

public class MotifCatalogPrinter {
	public static void print(List<MotifInformation> motifs, PrintStream out) {
		boolean first = true;
		for (MotifInformation motif : motifs) {
			if (!first)
				out.println();
			first = false;

			out.print(motif.getName());
			if (motif.getSummary() != null)
				out.print(": " + motif.getSummary());
			out.println();

			for (MotifDefinition definition : motif.getDefinitions()) {
				out.print("    @" + definition.getName());
				if (definition.getDescription() != null)
					out.print(": " + definition.getDescription());
				out.println();
			}
		}
	}
}
