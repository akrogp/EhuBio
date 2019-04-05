package es.ehubio.dubase.bl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.dl.entities.Enzyme;
import es.ehubio.dubase.dl.entities.Evidence;

@LocalBean
@Stateless
public class Searcher {
	@PersistenceContext
	private EntityManager em;
	
	public Set<EvidenceBean> search(String gene, Thresholds thresholds) {
		Set<EvidenceBean> set = new LinkedHashSet<>();
		set.addAll(searchEnzyme(gene, thresholds));
		set.addAll(searchSubstrate(gene, thresholds));
		return set;
	}

	public List<EvidenceBean> searchSubstrate(String gene, Thresholds th) {
		List<Evidence> evidences = em
			.createQuery("SELECT a.evidenceBean FROM Ambiguity a WHERE a.substrateBean.gene = :gene AND (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t1 AND ABS(s.value) > :s1) > 0 AND (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t2 AND s.value > :s2) > 0", Evidence.class)
			.setParameter("gene", gene)
			.setParameter("t1", Score.FOLD_CHANGE.ordinal())
			.setParameter("s1", th.getLog2FoldChange())
			.setParameter("t2", Score.P_VALUE.ordinal())
			.setParameter("s2", th.getLog10PValue())
			.getResultList();
		return DbUtils.buildEvidences(evidences);
	}
	
	public List<EvidenceBean> searchEnzyme(String gene, Thresholds th) {
		List<Evidence> evidences = em
			.createQuery("SELECT e FROM Evidence e WHERE e.experimentBean.enzymeBean.gene = :gene AND (SELECT COUNT(s) FROM e.evScores s WHERE s.scoreType.id = :t1 AND ABS(s.value) > :s1) > 0 AND (SELECT COUNT(s) FROM e.evScores s WHERE s.scoreType.id = :t2 AND s.value > :s2) > 0", Evidence.class)
			.setParameter("gene", gene)
			.setParameter("t1", Score.FOLD_CHANGE.ordinal())
			.setParameter("s1", th.getLog2FoldChange())
			.setParameter("t2", Score.P_VALUE.ordinal())
			.setParameter("s2", th.getLog10PValue())
			.getResultList();
		return DbUtils.buildEvidences(evidences);
	}
	
	public List<Enzyme> searchEnzymesWithData() {
		return em
			.createQuery("SELECT DISTINCT e.experimentBean.enzymeBean FROM Evidence e", Enzyme.class)
			.getResultList();
	}
}
