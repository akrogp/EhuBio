package es.ehubio.dubase.bl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.dl.EvScore;
import es.ehubio.dubase.dl.Evidence;

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
			results.add(result);
		}
		return results;
	}

}
