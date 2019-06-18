package es.ehubio.dubase.dl;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.input.ScoreType;
import es.ehubio.io.CsvUtils;

public class CsvExporter {
	private static final char SEP1 = ',';
	private static final char SEP2 = ';';
	
	public static void export(List<Evidence> results, PrintWriter pw) {
		pw.println(CsvUtils.getCsv(SEP1,
			"Experiment", "DUB", "Substrate", "Description",
			"Fold change (log2)", "p-value", "Peptide count (all)", "Peptide count (unique)",
			"Molecular weight (kDa)", "Sequence coverage (%)", "GlyGly (K) site positions",
			"Sample_1 LFQ (log2)", "Sample_2 LFQ (log2)", "Sample_3 LFQ (log2)",
			"Control_1 LFQ (log2)", "Control_2 LFQ (log2)", "Control_3 LFQ (log2)",
			"Imputed sample_1", "Imputed sample_2", "Imputed sample_3",
			"Imputed control_1", "Imputed control_2", "Imputed control_3"
		));
		for( Evidence ev : results ) {
			pw.print(CsvUtils.getCsv(SEP1,
				String.format("EXP%05d", ev.getExperimentBean().getId()),
				ev.getExperimentBean().getEnzymeBean().getGene(),
				CsvUtils.getCsv(SEP2, ev.getGenes().toArray()),
				CsvUtils.getCsv(SEP2, ev.getDescriptions().toArray()),
				ev.getScore(ScoreType.FOLD_CHANGE),
				ev.getScore(ScoreType.P_VALUE),
				ev.getScore(ScoreType.TOTAL_PEPTS).intValue(),
				ev.getScore(ScoreType.UNIQ_PEPTS).intValue(),
				ev.getScore(ScoreType.MOL_WEIGHT),
				ev.getScore(ScoreType.SEQ_COVERAGE),
				CsvUtils.getCsv(SEP2, ev.getModifications().stream().map(m->m.getPosition()).collect(Collectors.toList()).toArray())
			));
			pw.print(SEP1);
			printLfqs(pw, ev, false);
			printLfqs(pw, ev, true);
			printImputations(pw, ev, false);
			printImputations(pw, ev, true);
			pw.println();
		}
	}	

	private static void printLfqs(PrintWriter pw, Evidence ev, boolean control) {
		pw.print(ev.getRepScores().stream()
			.filter(s->s.getReplicateBean().getConditionBean().getControl() == control && s.getScoreType().getId() == ScoreType.LFQ_INTENSITY.ordinal())
			.map(s->String.valueOf(s.getValue()))
			.collect(Collectors.joining(SEP1+"")));
	}
	
	private static void printImputations(PrintWriter pw, Evidence ev, boolean control) {
		pw.print(ev.getRepScores().stream()
			.filter(s->s.getReplicateBean().getConditionBean().getControl() == control && s.getScoreType().getId() == ScoreType.LFQ_INTENSITY.ordinal())
			.map(s->String.valueOf(s.getImputed()))
			.collect(Collectors.joining(SEP1+"")));
	}
}
