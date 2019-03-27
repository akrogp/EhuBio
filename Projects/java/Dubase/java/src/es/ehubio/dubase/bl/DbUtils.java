package es.ehubio.dubase.bl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.bl.beans.RepScoreBean;
import es.ehubio.dubase.bl.beans.ReplicateBean;
import es.ehubio.dubase.dl.entities.EvScore;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.Modification;
import es.ehubio.dubase.dl.entities.RepScore;
import es.ehubio.dubase.dl.entities.Replicate;
import es.ehubio.dubase.dl.entities.Substrate;

public class DbUtils {

	public static List<EvidenceBean> fillEvidences(EntityManager em, List<Evidence> evidences) {
		List<EvidenceBean> results = new ArrayList<>();
		for( Evidence ev : evidences ) {
			EvidenceBean result = new EvidenceBean();
			result.setExperiment(ev.getExperimentBean());
			List<Substrate> genes = em
					.createQuery("SELECT a.substrateBean FROM Ambiguity a WHERE a.evidenceBean = :ev", Substrate.class)
					.setParameter("ev", ev)
					.getResultList();
			result.getGenes().addAll(
					genes.stream().map(s -> s.getGene()).collect(Collectors.toList()));
			result.getDescriptions().addAll(
					genes.stream().map(s -> s.getDescription()).collect(Collectors.toList()));
			List<EvScore> scores = em
				.createQuery("SELECT s FROM EvScore s WHERE s.evidenceBean = :ev", EvScore.class)
				.setParameter("ev", ev)
				.getResultList();
			for( EvScore score : scores )
				result.putScore(Score.values()[score.getScoreType().getId()], score.getValue());
			List<Modification> mods = em
					.createQuery("SELECT m FROM Modification m WHERE m.evidenceBean = :ev", Modification.class)
					.setParameter("ev", ev)
					.getResultList();
			result.getModPositions().addAll(mods.stream().map(mod -> mod.getPosition()).collect(Collectors.toList()));
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
