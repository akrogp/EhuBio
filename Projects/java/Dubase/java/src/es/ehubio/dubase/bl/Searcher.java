package es.ehubio.dubase.bl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.dl.entities.Ambiguity;
import es.ehubio.dubase.dl.entities.Enzyme;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.ModScore;
import es.ehubio.dubase.dl.entities.Modification;
import es.ehubio.dubase.dl.input.MethodType;
import es.ehubio.dubase.dl.input.ScoreType;

@LocalBean
@Stateless
public class Searcher {
	@PersistenceContext
	private EntityManager em;
	
	public List<Evidence> search(String gene, Map<Integer, Thresholds> mapTh) {
		List<Evidence> evs = new ArrayList<>();
		evs.addAll(searchEnzyme(gene, mapTh));
		evs.addAll(searchSubstrate(gene, mapTh));
		return evs;
	}
	
	public List<Evidence> searchEnzyme(String gene, Map<Integer, Thresholds> mapTh) {
		List<Evidence> evs = new ArrayList<>();
		evs.addAll(search(	// Manual
			"SELECT e FROM Evidence e" +
			" WHERE e.experimentBean.enzymeBean.gene = :gene" +
			" AND e.experimentBean.methodBean.type.id = :manual",
			gene, 0, null));
		for( Entry<Integer, Thresholds> entry : mapTh.entrySet() )
			evs.addAll(search(
				"SELECT e FROM Evidence e WHERE e.experimentBean.id = :expId AND e.experimentBean.enzymeBean.gene = :gene" +
				" AND ( ( e.experimentBean.methodBean.type.id = :proteomics" +
					" AND (SELECT COUNT(s) FROM e.evScores s WHERE s.scoreType.id = :t1 AND (s.value >= :s11 OR s.value <= :s12)) > 0" +
					" AND (SELECT COUNT(s) FROM e.evScores s WHERE s.scoreType.id = :t2 AND s.value <= :s2) > 0" +
					" AND ( (SELECT COUNT(s) FROM e.evScores s WHERE s.scoreType.id = :t3 AND s.value >= :s3) > 0" +
						" OR (SELECT COUNT(s) FROM e.evScores s WHERE s.scoreType.id = :t3) = 0)"+
				" ) OR ( e.experimentBean.methodBean.type.id = :ubiquitomics" +
					" AND (SELECT COUNT(a) FROM e.ambiguities a WHERE (SELECT COUNT(m) FROM a.modifications m WHERE" +
						" (SELECT COUNT(s) FROM m.scores s WHERE s.scoreType.id = :t1 AND (s.value >= :s11 OR s.value <= :s12)) > 0" +
						" AND (SELECT COUNT(s) FROM m.scores s WHERE s.scoreType.id = :t2 AND s.value <= :s2) > 0" +
					" ) > 0) > 0" +
				" ) )",
				gene, entry.getKey(), entry.getValue()));
		return evs;
	}
	
	public List<Evidence> searchSubstrate(String gene, Map<Integer, Thresholds> mapTh) {
		gene = "%"+gene+"%";
		List<Evidence> evs = new ArrayList<>();
		evs.addAll(search(	// Manual
			"SELECT a.evidenceBean FROM Ambiguity a" +
			" WHERE a.proteinBean.geneBean.aliases LIKE :gene" +
		    " AND a.evidenceBean.experimentBean.methodBean.type.id = :manual",
			gene, 0, null));
		for( Entry<Integer, Thresholds> entry : mapTh.entrySet() )
			evs.addAll(search(
				"SELECT a.evidenceBean FROM Ambiguity a WHERE a.evidenceBean.experimentBean.id = :expId AND a.proteinBean.geneBean.aliases LIKE :gene" +
			    " AND ( ( a.evidenceBean.experimentBean.methodBean.type.id = :proteomics" +
					" AND (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t1 AND (s.value >= :s11 OR s.value <= :s12)) > 0" +
					" AND (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t2 AND s.value <= :s2) > 0" +
					" AND ( (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t3 AND s.value >= :s3) > 0" +
						" OR (SELECT COUNT(s) FROM a.evidenceBean.evScores s WHERE s.scoreType.id = :t3) = 0)"+
				" ) OR ( a.evidenceBean.experimentBean.methodBean.type.id = :ubiquitomics" +
					" AND (SELECT COUNT(m) FROM a.modifications m WHERE" +
						" (SELECT COUNT(s) FROM m.scores s WHERE s.scoreType.id = :t1 AND (s.value >= :s11 OR s.value <= :s12)) > 0" +
						" AND (SELECT COUNT(s) FROM m.scores s WHERE s.scoreType.id = :t2 AND s.value <= :s2) > 0" +
					" ) > 0" +
				" ) )",
				gene, entry.getKey(), entry.getValue()));
		return evs;
	}
	
	private List<Evidence> search(String queryString, String gene, int expId, Thresholds th) {
		TypedQuery<Evidence> query = em.createQuery(queryString, Evidence.class)
			.setParameter("gene", gene);
		if( expId > 0 )
			query.setParameter("expId", expId);
		if( th == null ) {
			query.setParameter("manual", MethodType.MANUAL.ordinal());
			return query.getResultList();
		}
		query
			.setParameter("proteomics", MethodType.PROTEOMICS.ordinal())
			.setParameter("ubiquitomics", MethodType.UBIQUITOMICS.ordinal())
			.setParameter("t1", ScoreType.FOLD_CHANGE.ordinal())
			.setParameter("s11", th.isUp() ? th.getFoldChange() : 1000)
			.setParameter("s12", th.isDown() ? 1.0/th.getFoldChange() : 0)
			.setParameter("t2", ScoreType.P_VALUE.ordinal())
			.setParameter("s2", th.getpValue())
			.setParameter("t3", ScoreType.UNIQ_PEPTS.ordinal())
			.setParameter("s3", (double)th.getMinPeptides());
		return filterMods(query.getResultList(), th);
	}	
		
	private List<Evidence> filterMods(List<Evidence> evs, Thresholds th) {
		for( Evidence ev : evs )
			for( Ambiguity a : ev.getAmbiguities() ) {
				List<Modification> remove = null;
				for( Modification mod : a.getModifications() ) {
					ModScore foldChange = mod.getScores().stream().filter(s->s.getScoreType().getId()==ScoreType.FOLD_CHANGE.ordinal()).findFirst().orElse(null);
					ModScore pValue = mod.getScores().stream().filter(s->s.getScoreType().getId()==ScoreType.P_VALUE.ordinal()).findFirst().orElse(null);
					boolean changed = checkChanged(foldChange, th);
					boolean significative = checkSignificative(pValue, th);
					if( !changed || !significative ) {
						if( remove == null )
							remove = new ArrayList<>();
						remove.add(mod);
					}
				}
				if( remove != null )
					a.getModifications().removeAll(remove);
			}
		return evs;
	}

	private boolean checkSignificative(ModScore pValue, Thresholds th) {
		return pValue == null || pValue.getValue() <= th.getpValue();
	}

	private boolean checkChanged(ModScore foldChange, Thresholds th) {
		return foldChange == null || (th.isUp() && foldChange.getValue() >= th.getFoldChange()) || (th.isDown() && foldChange.getValue() <= 1/th.getFoldChange());
	}

	public List<Enzyme> searchEnzymesWithData() {
		return em
			.createQuery("SELECT DISTINCT e.experimentBean.enzymeBean FROM Evidence e", Enzyme.class)
			.getResultList();
	}
	
	public List<Experiment> findExperiments() {
		return em.createNamedQuery("Experiment.findAll", Experiment.class).getResultList();
	}
	
	
	public List<Experiment> findExperimentsWithThreholds() {
		return em.
			createQuery("SELECT e FROM Experiment e WHERE e.methodBean.type.id != :manual", Experiment.class)
			.setParameter("manual", MethodType.MANUAL.ordinal())
			.getResultList();
	}
}
