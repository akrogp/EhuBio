package es.ehubio.dubase.bl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.dl.entities.Enzyme;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.input.ScoreType;

@LocalBean
@Stateless
public class Searcher {
	@PersistenceContext
	private EntityManager em;
	
	public Set<Evidence> search(String gene, Thresholds thresholds) {
		Set<Evidence> set = new LinkedHashSet<>();
		set.addAll(searchEnzyme(gene, thresholds));
		set.addAll(searchSubstrate(gene, thresholds));
		return set;
	}

	public List<Evidence> searchSubstrate(String gene, Thresholds th) {
		return em
			.createQuery("SELECT a.evidenceBean FROM Ambiguity a WHERE a.proteinBean.geneBean.name = :gene AND (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t1 AND (s.value >= :s11 OR s.value <= :s12)) > 0 AND (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t2 AND s.value >= :s2) > 0", Evidence.class)
			.setParameter("gene", gene)
			.setParameter("t1", ScoreType.FOLD_CHANGE.ordinal())
			.setParameter("s11", th.isUp() ? th.getLog2FoldChange() : 1000)
			.setParameter("s12", th.isDown() ? -th.getLog2FoldChange() : -1000)
			.setParameter("t2", ScoreType.P_VALUE.ordinal())
			.setParameter("s2", th.getLog10PValue())
			.getResultList();
	}
	
	public List<Evidence> searchEnzyme(String gene, Thresholds th) {
		return em
			.createQuery("SELECT e FROM Evidence e WHERE e.experimentBean.enzymeBean.gene = :gene AND (SELECT COUNT(s) FROM e.evScores s WHERE s.scoreType.id = :t1 AND (s.value >= :s11 OR s.value <= :s12)) > 0 AND (SELECT COUNT(s) FROM e.evScores s WHERE s.scoreType.id = :t2 AND s.value >= :s2) > 0", Evidence.class)
			.setParameter("gene", gene)
			.setParameter("t1", ScoreType.FOLD_CHANGE.ordinal())
			.setParameter("s11", th.isUp() ? th.getLog2FoldChange() : 1000)
			.setParameter("s12", th.isDown() ? -th.getLog2FoldChange() : -1000)
			.setParameter("t2", ScoreType.P_VALUE.ordinal())
			.setParameter("s2", th.getLog10PValue())
			.getResultList();
	}
	
	public List<Enzyme> searchEnzymesWithData() {
		return em
			.createQuery("SELECT DISTINCT e.experimentBean.enzymeBean FROM Evidence e", Enzyme.class)
			.getResultList();
	}
}
