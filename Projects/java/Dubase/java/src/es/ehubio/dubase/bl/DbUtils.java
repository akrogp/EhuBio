package es.ehubio.dubase.bl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.bl.beans.RepScoreBean;
import es.ehubio.dubase.bl.beans.ReplicateBean;
import es.ehubio.dubase.dl.entities.EvScore;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.RepScore;
import es.ehubio.dubase.dl.entities.Replicate;

public class DbUtils {

	public static List<EvidenceBean> buildEvidences(List<Evidence> evidences) {
		List<EvidenceBean> results = new ArrayList<>();
		for( Evidence ev : evidences ) {
			EvidenceBean result = new EvidenceBean();
			result.setExperiment(ev.getExperimentBean());
			result.getGenes().addAll(
					ev.getAmbiguities().stream().map(a -> a.getSubstrateBean().getGene()).collect(Collectors.toList()));
			result.getDescriptions().addAll(
					ev.getAmbiguities().stream().map(a -> a.getSubstrateBean().getDescription()).collect(Collectors.toList()));
			for( EvScore score : ev.getEvScores() )
				result.putScore(Score.values()[score.getScoreType().getId()], score.getValue());
			result.getModPositions().addAll(ev.getModifications().stream().map(mod -> mod.getPosition()).collect(Collectors.toList()));
			result.getSamples().addAll(buildSamples(ev, false));
			result.getControls().addAll(buildSamples(ev, true));
			results.add(result);
		}
		return results;
	}
	
	public static List<EvidenceBean> filter(List<EvidenceBean> evidences, Thresholds thresholds) {
		final Thresholds th = thresholds == null ? new Thresholds() : thresholds;
		evidences = evidences.stream()
			.filter(ev ->
				Math.abs(ev.getMapScores().get(Score.FOLD_CHANGE.ordinal())) >= th.getLog2FoldChange() &&
				ev.getMapScores().get(Score.P_VALUE.ordinal()) >= th.getLog10PValue())
			.collect(Collectors.toList());
		return evidences;
	}

	private static List<ReplicateBean> buildSamples(Evidence ev, boolean ctrl) {		
		List<ReplicateBean> results = new ArrayList<>(ev.getReplicates().size());
		
		for( Replicate rep : ev.getReplicates() ) {
			if( rep.isControl() != ctrl )
				continue;
			ReplicateBean result = new ReplicateBean();			
			for( RepScore score : rep.getRepScores() ) {
				RepScoreBean bean = new RepScoreBean();
				bean.setScore(score.getScoreType().getId());
				bean.setValue(score.getValue());
				bean.setImputed(score.getImputed());
				result.getMapScores().put(bean.getScore(), bean);
			}
			results.add(result);
		}
				
		return results;
	}

}
