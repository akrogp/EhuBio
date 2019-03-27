package es.ehubio.dubase.dl;

import java.io.PrintWriter;
import java.util.List;

import es.ehubio.dubase.bl.Score;
import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.bl.beans.ReplicateBean;
import es.ehubio.io.CsvUtils;

public class CsvExporter {
	private static final char SEP1 = ',';
	private static final char SEP2 = ';';
	
	public static void export(List<EvidenceBean> results, PrintWriter pw) {
		pw.println(CsvUtils.getCsv(SEP1,
			"Experiment", "DUB", "Substrate", "Description",
			"Fold change (log2)", "p-value", "Peptide count (all)", "Peptide count (unique)",
			"Molecular weight (kDa)", "Sequence coverage (%)", "GlyGly (K) site positions",
			"Sample_1 LFQ (log2)", "Sample_2 LFQ (log2)", "Sample_3 LFQ (log2)",
			"Control_1 LFQ (log2)", "Control_2 LFQ (log2)", "Control_3 LFQ (log2)",
			"Imputed sample_1", "Imputed sample_2", "Imputed sample_3",
			"Imputed control_1", "Imputed control_2", "Imputed control_3"
		));
		for( EvidenceBean ev : results ) {
			pw.print(CsvUtils.getCsv(SEP1,
				String.format("EXP%05d", ev.getExperiment().getId()),
				ev.getExperiment().getEnzymeBean().getGene(),
				CsvUtils.getCsv(SEP2, ev.getGenes().toArray()),
				CsvUtils.getCsv(SEP2, ev.getDescriptions().toArray()),
				ev.getMapScores().get(Score.FOLD_CHANGE.ordinal()),
				ev.getMapScores().get(Score.P_VALUE.ordinal()),
				ev.getMapScores().get(Score.TOTAL_PEPTS.ordinal()).intValue(),
				ev.getMapScores().get(Score.UNIQ_PEPTS.ordinal()).intValue(),
				ev.getMapScores().get(Score.MOL_WEIGHT.ordinal()),
				ev.getMapScores().get(Score.SEQ_COVERAGE.ordinal()),
				CsvUtils.getCsv(SEP2, ev.getModPositions().toArray())
			));
			pw.print(SEP1);
			printLfqs(pw, ev.getSamples());
			printLfqs(pw, ev.getControls());
			printImputations(pw, ev.getSamples());
			printImputations(pw, ev.getControls());
			pw.println();
		}
	}	

	private static void printLfqs(PrintWriter pw, List<ReplicateBean> reps) {
		for( ReplicateBean rep : reps ) {
			pw.print(rep.getMapScores().get(Score.LFQ_INTENSITY.ordinal()).getValue());
			pw.print(SEP1);
		}
	}
	
	private static void printImputations(PrintWriter pw, List<ReplicateBean> reps) {
		for( ReplicateBean rep : reps ) {
			pw.print(rep.getMapScores().get(Score.LFQ_INTENSITY.ordinal()).isImputed() ? 1 : 0);
			pw.print(SEP1);
		}
	}
}
