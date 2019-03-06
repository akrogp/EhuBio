package es.ehubio.dubase.bl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.bl.beans.RepScoreBean;
import es.ehubio.dubase.bl.beans.ReplicateBean;
import es.ehubio.dubase.dl.EvScore;
import es.ehubio.dubase.dl.Evidence;
import es.ehubio.dubase.dl.RepScore;
import es.ehubio.dubase.dl.Replicate;

public class DbUtils {

	public static List<EvidenceBean> fillEvidences(EntityManager em, List<Evidence> evidences) {
		List<EvidenceBean> results = new ArrayList<>();
		for( Evidence ev : evidences ) {
			EvidenceBean result = new EvidenceBean();
			result.setExperiment(ev.getExperimentBean());
			result.getGenes().addAll(em
				.createQuery("SELECT a.substrateBean.gene FROM Ambiguity a WHERE a.evidenceBean = :ev", String.class)
				.setParameter("ev", ev)
				.getResultList());
			List<EvScore> scores = em
				.createQuery("SELECT s FROM EvScore s WHERE s.evidenceBean = :ev", EvScore.class)
				.setParameter("ev", ev)
				.getResultList();
			for( EvScore score : scores )
				result.putScore(Score.values()[score.getScoreType().getId()], score.getValue());
			result.getSamples().addAll(fillSamples(em, ev, false));
			result.getControls().addAll(fillSamples(em, ev, true));
			results.add(result);
		}
		return results;
	}

	private static List<ReplicateBean> fillSamples(EntityManager em, Evidence ev, boolean ctrl) {		
		List<Replicate> reps = em
			.createQuery("SELECT r FROM Replicate r WHERE r.evidenceBean = :ev AND r.control = :ctrl", Replicate.class)
			.setParameter("ev", ev)
			.setParameter("ctrl", ctrl)
			.getResultList();
		List<ReplicateBean> results = new ArrayList<>(reps.size());
		
		for( Replicate rep : reps ) {
			ReplicateBean result = new ReplicateBean();			
			List<RepScore> scores = em
				.createQuery("SELECT s FROM RepScore s WHERE s.replicateBean = :rep", RepScore.class)
				.setParameter("rep", rep)
				.getResultList();
			for( RepScore score : scores ) {
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
