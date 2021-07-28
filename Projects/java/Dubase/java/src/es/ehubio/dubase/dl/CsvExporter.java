package es.ehubio.dubase.dl;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.Protein;
import es.ehubio.dubase.dl.input.ScoreType;
import es.ehubio.io.CsvUtils;

public class CsvExporter {
	private static final String SEP1 = ",";
	private static final String SEP2 = ";";
	private static final String SEP3 = " ";
	
	public static void export(List<Evidence> results, PrintWriter pw) {
		pw.println(CsvUtils.getCsv(SEP1,
			"Experiment", "Type", "DUB", "Genes", "Protein IDs", "Description",
			"Fold change (log2)", "p-value", "Peptide count (all)", "Peptide count (unique)",
			"Molecular weight (kDa)", "Sequence coverage (%)", "GlyGly (K) site positions",
			"Sample_1 LFQ (log2)", "Sample_2 LFQ (log2)", "Sample_3 LFQ (log2)",
			"Control_1 LFQ (log2)", "Control_2 LFQ (log2)", "Control_3 LFQ (log2)",
			"Imputed sample_1", "Imputed sample_2", "Imputed sample_3",
			"Imputed control_1", "Imputed control_2", "Imputed control_3"
		));
		for( Evidence ev : results ) {
			boolean proteomics = Boolean.TRUE.equals(ev.getExperimentBean().getMethodBean().getProteomic());
			pw.print(CsvUtils.getCsv(SEP1,
				String.format("EXP%05d", ev.getExperimentBean().getId()),
				proteomics ? "Proteomics" : "Manual curation",
				ev.getExperimentBean().getEnzymeBean().getGene(),
				CsvUtils.getCsv(SEP2, ev.getGenes().toArray()),
				CsvUtils.getCsv(SEP2, ev.getProteins().toArray()),
				CsvUtils.getCsv(SEP2, ev.getDescriptions().toArray()),
				proteomics ? ev.getScore(ScoreType.FOLD_CHANGE) : "",
				proteomics ? Math.pow(10,-ev.getScore(ScoreType.P_VALUE)) : "",
				proteomics ? ev.getScore(ScoreType.TOTAL_PEPTS).intValue() : "",
				proteomics ? ev.getScore(ScoreType.UNIQ_PEPTS).intValue() : "",
				proteomics ? ev.getScore(ScoreType.MOL_WEIGHT) : "",
				proteomics ? ev.getScore(ScoreType.SEQ_COVERAGE) : "",
				proteomics ? buildModString(ev, SEP2, SEP3) : ""
			));
			printLfqs(pw, ev, false);
			printLfqs(pw, ev, true);
			printImputations(pw, ev, false);
			printImputations(pw, ev, true);
			pw.println();
		}
	}	

	private static void printLfqs(PrintWriter pw, Evidence ev, boolean control) {
		if( ev.getRepScores() == null )
			return;
		pw.print(ev.getRepScores().stream()
			.filter(s->s.getReplicateBean().getConditionBean().getControl() == control && s.getScoreType().getId() == ScoreType.LFQ_INTENSITY.ordinal())
			.map(s->String.valueOf(s.getValue()))
			.collect(Collectors.joining(SEP1,SEP1,"")));
	}
	
	private static void printImputations(PrintWriter pw, Evidence ev, boolean control) {
		if( ev.getRepScores() == null )
			return;
		pw.print(ev.getRepScores().stream()
			.filter(s->s.getReplicateBean().getConditionBean().getControl() == control && s.getScoreType().getId() == ScoreType.LFQ_INTENSITY.ordinal())
			.map(s->String.valueOf(s.getImputed()))
			.collect(Collectors.joining(SEP1,SEP1,"")));
	}
	
	public static String buildModString(Evidence ev, String sep2, String sep3) {
		return ev.getProteinBeans().stream().map(p -> buildModString(p, sep3)).filter(s->!s.isEmpty()).collect(Collectors.joining(sep2));
	}

	private static String buildModString(Protein p, String sep3) {
		return p.getModifications().stream().map(m->m.getPosition()).sorted().map(pos->String.valueOf(pos)).collect(Collectors.joining(sep3));
	}
}
