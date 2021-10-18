package es.ehubio.dubase.bl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.dl.entities.Clazz;
import es.ehubio.dubase.dl.entities.Enzyme;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.Superfamily;
import es.ehubio.dubase.dl.input.MethodType;
import es.ehubio.dubase.dl.input.ScoreType;

@LocalBean
@Stateless
public class Browser {
	@PersistenceContext
	private EntityManager em;
	
	public List<Clazz> getClasses() {
		return em.createNamedQuery("Clazz.findAll", Clazz.class).getResultList();
	}
	
	public List<Superfamily> getSuperfamiliesByClass(int classId) {
		return em
			.createQuery("SELECT s FROM Superfamily s WHERE s.clazz.id = :classId", Superfamily.class)
			.setParameter("classId", classId)
			.getResultList();
	}
	
	public List<Enzyme> getEnzymesBySuperfamily(int familyId) {
		return em
			.createQuery("SELECT e FROM Enzyme e WHERE e.superfamilyBean.id = :familyId", Enzyme.class)
			.setParameter("familyId", familyId)
			.getResultList();
	}
	
	public List<String> getEnzymesWithEvidences() {
		return em
			.createQuery("SELECT DISTINCT e.enzymeBean.gene FROM Experiment e", String.class)
			.getResultList();
	}
	
	public Map<String, Stats> getEnzymesStats() {
		Map<String, Stats> map = new HashMap<>();
		List<Stats> list = em
			.createQuery("SELECT NEW es.ehubio.dubase.bl.Stats(" + 
				" a.evidenceBean.experimentBean.enzymeBean.gene, COUNT(DISTINCT a.proteinBean.accession), COUNT(DISTINCT a.evidenceBean.experimentBean) )" +
				" FROM Ambiguity a GROUP BY a.evidenceBean.experimentBean.enzymeBean"
				, Stats.class)
			.getResultList();
		for( Stats stats : list )
			map.put(stats.getDub(), stats);
		return map;
	}
	
	public List<Experiment> getExperiments() {
		return em.createNamedQuery("Experiment.findAll", Experiment.class).getResultList();
	}
	
	public Set<String> getSubstrates(String dub, Map<Integer, Thresholds> mapTh) {
		Set<String> set = new HashSet<>();
		set.addAll(em.createQuery("SELECT DISTINCT a.proteinBean.geneBean.name FROM Ambiguity a" +
			" WHERE a.evidenceBean.experimentBean.enzymeBean.gene = :gene AND a.evidenceBean.experimentBean.methodBean.type.id = :manual"
			, String.class)
			.setParameter("gene", dub)
			.setParameter("manual", MethodType.MANUAL.ordinal())
			.getResultList()
		);
		for( Entry<Integer, Thresholds> entry : mapTh.entrySet() ) {
			int expId = entry.getKey();
			Thresholds th = entry.getValue();
			set.addAll(em.createQuery("SELECT DISTINCT a.proteinBean.geneBean.name FROM Ambiguity a" +
				" WHERE a.evidenceBean.experimentBean.id = :expId" +
				" AND a.evidenceBean.experimentBean.enzymeBean.gene = :gene" +
				" AND a.evidenceBean.experimentBean.methodBean.type.id = :proteomics" +
				" AND (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t1 AND (s.value >= :s11 OR s.value <= :s12)) > 0" +
				" AND (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t2 AND s.value <= :s2) > 0" +
				" AND ( (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t3 AND s.value >= :s3) > 0 OR (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t3) = 0)"
				, String.class)
				.setParameter("expId", expId)
				.setParameter("gene", dub)				
				.setParameter("proteomics", MethodType.PROTEOMICS.ordinal())
				.setParameter("t1", ScoreType.FOLD_CHANGE.ordinal())
				.setParameter("s11", th.isUp() ? th.getFoldChange() : 1000)
				.setParameter("s12", th.isDown() ? 1.0/th.getFoldChange() : 0)
				.setParameter("t2", ScoreType.P_VALUE.ordinal())
				.setParameter("s2", th.getpValue())
				.setParameter("t3", ScoreType.UNIQ_PEPTS.ordinal())
				.setParameter("s3", (double)th.getMinPeptides())
				.getResultList()
			);
		}
		return set;
	}
}
