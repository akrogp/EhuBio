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
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.input.MethodType;
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
			.createQuery(
				"SELECT a.evidenceBean FROM Ambiguity a WHERE a.proteinBean.geneBean.aliases LIKE :gene" +
			    " AND ( a.evidenceBean.experimentBean.methodBean.type.id = :manual" +
					" OR ( a.evidenceBean.experimentBean.methodBean.type.id = :proteomics" +
						" AND (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t1 AND (s.value >= :s11 OR s.value <= :s12)) > 0" +
						" AND (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t2 AND s.value <= :s2) > 0" +
						" AND (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t3 AND s.value >= :s3) > 0" +
					" ) OR ( a.evidenceBean.experimentBean.methodBean.type.id = :ubiquitomics" +
						" AND (SELECT COUNT(m) FROM a.modifications m WHERE" +
							" (SELECT COUNT(s) FROM m.scores s WHERE s.scoreType.id = :t1 AND (s.value >= :s11 OR s.value <= :s12)) > 0" +
							" AND (SELECT COUNT(s) FROM m.scores s WHERE s.scoreType.id = :t2 AND s.value <= :s2) > 0" +
						" ) > 0" +
					" )" +
				" )",
				Evidence.class)
			.setParameter("gene", "%" + gene + "%")
			.setParameter("manual", MethodType.MANUAL.ordinal())
			.setParameter("proteomics", MethodType.PROTEOMICS.ordinal())
			.setParameter("ubiquitomics", MethodType.UBIQUITOMICS.ordinal())
			.setParameter("t1", ScoreType.FOLD_CHANGE.ordinal())
			.setParameter("s11", th.isUp() ? th.getFoldChange() : 1000)
			.setParameter("s12", th.isDown() ? 1.0/th.getFoldChange() : 0)
			.setParameter("t2", ScoreType.P_VALUE.ordinal())
			.setParameter("s2", th.getpValue())
			.setParameter("t3", ScoreType.UNIQ_PEPTS.ordinal())
			.setParameter("s3", (double)th.getMinPeptides())
			.getResultList();
	}
	
	public List<Evidence> searchEnzyme(String gene, Thresholds th) {
		return em
			.createQuery(
				"SELECT e FROM Evidence e WHERE e.experimentBean.enzymeBean.gene = :gene" +
				" AND ( e.experimentBean.methodBean.type.id = :manual" +
					" OR ( e.experimentBean.methodBean.type.id = :proteomics" +
						" AND (SELECT COUNT(s) FROM e.evScores s WHERE s.scoreType.id = :t1 AND (s.value >= :s11 OR s.value <= :s12)) > 0" +
						" AND (SELECT COUNT(s) FROM e.evScores s WHERE s.scoreType.id = :t2 AND s.value <= :s2) > 0" +
						" AND (SELECT COUNT(s) FROM e.evScores s WHERE s.scoreType.id = :t3 AND s.value >= :s3) > 0" +
					" ) OR ( e.experimentBean.methodBean.type.id = :ubiquitomics" +
						" AND (SELECT COUNT(a) FROM e.ambiguities a WHERE (SELECT COUNT(m) FROM a.modifications m WHERE" +
							" (SELECT COUNT(s) FROM m.scores s WHERE s.scoreType.id = :t1 AND (s.value >= :s11 OR s.value <= :s12)) > 0" +
							" AND (SELECT COUNT(s) FROM m.scores s WHERE s.scoreType.id = :t2 AND s.value <= :s2) > 0" +
						" ) > 0) > 0" +
					" )" +
				" )",
				Evidence.class)
			.setParameter("gene", gene)
			.setParameter("manual", MethodType.MANUAL.ordinal())
			.setParameter("proteomics", MethodType.PROTEOMICS.ordinal())
			.setParameter("ubiquitomics", MethodType.UBIQUITOMICS.ordinal())
			.setParameter("t1", ScoreType.FOLD_CHANGE.ordinal())
			.setParameter("s11", th.isUp() ? th.getFoldChange() : 1000)
			.setParameter("s12", th.isDown() ? 1.0/th.getFoldChange() : 0)
			.setParameter("t2", ScoreType.P_VALUE.ordinal())
			.setParameter("s2", th.getpValue())
			.setParameter("t3", ScoreType.UNIQ_PEPTS.ordinal())
			.setParameter("s3", (double)th.getMinPeptides())
			.getResultList();
	}
	
	public List<Enzyme> searchEnzymesWithData() {
		return em
			.createQuery("SELECT DISTINCT e.experimentBean.enzymeBean FROM Evidence e", Enzyme.class)
			.getResultList();
	}
	
	public List<Experiment> findExperiments() {
		return em.createNamedQuery("Experiment.findAll", Experiment.class).getResultList();
	}
}
