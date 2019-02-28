package es.ehubio.dubase.bl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.bl.beans.ClassBean;
import es.ehubio.dubase.bl.beans.EnzymeBean;
import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.bl.beans.ExperimentBean;
import es.ehubio.dubase.bl.beans.RepScoreBean;
import es.ehubio.dubase.bl.beans.ReplicateBean;
import es.ehubio.dubase.bl.beans.SuperfamilyBean;
import es.ehubio.dubase.bl.beans.TreeBean;
import es.ehubio.dubase.dl.Ambiguity;
import es.ehubio.dubase.dl.Author;
import es.ehubio.dubase.dl.Clazz;
import es.ehubio.dubase.dl.Enzyme;
import es.ehubio.dubase.dl.EvScore;
import es.ehubio.dubase.dl.Evidence;
import es.ehubio.dubase.dl.Experiment;
import es.ehubio.dubase.dl.Method;
import es.ehubio.dubase.dl.RepScore;
import es.ehubio.dubase.dl.Replicate;
import es.ehubio.dubase.dl.ScoreType;
import es.ehubio.dubase.dl.Substrate;
import es.ehubio.dubase.dl.Superfamily;

@LocalBean
@Stateless
public class Database {
	@PersistenceContext
	private EntityManager em;
	
	public TreeBean getTree() {
		TreeBean tree = new TreeBean();
		for( Clazz clazz : getClasses() ) {
			ClassBean classBean = new ClassBean(clazz);
			for( Superfamily family : getSuperfamiliesByClass(clazz.getId()) ) {
				SuperfamilyBean superfamilyBean = new SuperfamilyBean(family);
				for( Enzyme enzyme : getEnzymesBySuperfamily(family.getId()) ) {
					EnzymeBean enzymeBean = new EnzymeBean(enzyme);
					enzymeBean.getSubstrates().addAll(getSubstrateByEnzyme(enzyme.getId()));
					superfamilyBean.getEnzymes().add(enzymeBean);
				}
				classBean.getSuperfamilies().add(superfamilyBean);
			}
			tree.getClassess().add(classBean);
		}
		return tree;
	}

	@SuppressWarnings("unchecked")
	public List<Clazz> getClasses() {
		return em.createNamedQuery("Clazz.findAll").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Superfamily> getSuperfamiliesByClass(int classId) {
		return em
			.createQuery("SELECT s FROM Superfamily s WHERE s.clazz.id = :classId")
			.setParameter("classId", classId)
			.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Enzyme> getEnzymesBySuperfamily(int familyId) {
		return em
			.createQuery("SELECT e FROM Enzyme e WHERE e.superfamilyBean.id = :familyId")
			.setParameter("familyId", familyId)
			.getResultList();
	}
	
	private List<EvidenceBean> getSubstrateByEnzyme(int enzymeId) {
		List<EvidenceBean> results = new ArrayList<>();
		List<Evidence> evidences = em
			.createQuery("SELECT e FROM Evidence e WHERE e.experimentBean.enzymeBean.id = :enzymeId", Evidence.class)
			.setParameter("enzymeId", enzymeId)
			.getResultList();
		for( Evidence ev : evidences ) {
			EvidenceBean result = new EvidenceBean();
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
	
	public void saveExperiment(ExperimentBean experimentBean) throws IOException {
		Author author = new Author();
		author.setName(experimentBean.getContactName());
		author.setMail(experimentBean.getContactMail());
		author.setAffiliation(experimentBean.getContactAffiliation());
		em.persist(author);
		
		Method method = new Method();
		method.setOpenDescription(experimentBean.getMethod());
		em.persist(method);
		
		Enzyme enzyme = (Enzyme) em.createQuery("SELECT e FROM Enzyme e WHERE e.gene = :gene")
				.setParameter("gene", experimentBean.getEnzyme())
				.getSingleResult();
		
		Experiment experiment = new Experiment();
		experiment.setExpDate(experimentBean.getDate());
		experiment.setPubDate(new Date());
		experiment.setEnzymeBean(enzyme);
		experiment.setAuthorBean(author);
		experiment.setMethodBean(method);
		em.persist(experiment);
		
		saveEvidences(experiment, experimentBean.getEvidencesPath());
	}

	private void saveEvidences(Experiment experiment, String evidencesPath) throws IOException {
		for( EvidenceBean evBean : filter(EvidenceFile.loadEvidences(evidencesPath)) ) {
			Evidence ev = new Evidence();
			ev.setExperimentBean(experiment);
			em.persist(ev);
			
			saveScores(ev, evBean.getMapScores());
			saveReplicates(ev, evBean.getSamples(), false);
			saveReplicates(ev, evBean.getControls(), true);
			
			for( int i = 0; i < evBean.getGenes().size(); i++ ) {
				String gene = evBean.getGenes().get(i);
				Substrate subs;
				try {
					subs = em.createQuery("SELECT s FROM Substrate s WHERE s.gene = :gene", Substrate.class)
						.setParameter("gene", gene)
						.getSingleResult();
				} catch (NoResultException e) {
					subs = new Substrate();
					subs.setGene(gene);
					if( evBean.getGenes().size() == evBean.getDescriptions().size() )
						subs.setDescription(evBean.getDescriptions().get(i));
					em.persist(subs);
				}
				Ambiguity ambiguity = new Ambiguity();
				ambiguity.setEvidenceBean(ev);
				ambiguity.setSubstrateBean(subs);
				em.persist(ambiguity);
			}
		}
	}

	private void saveReplicates(Evidence ev, List<ReplicateBean> replicates, boolean control) {
		for( ReplicateBean repBean : replicates ) {
			Replicate rep = new Replicate();
			rep.setControl(control);
			rep.setEvidenceBean(ev);
			em.persist(rep);
			saveScores(rep, repBean.getMapScores());
		}
	}

	private List<EvidenceBean> filter(List<EvidenceBean> evidences) {
		evidences.removeIf(subs ->
			Math.abs(subs.getMapScores().get(Score.FOLD_CHANGE.ordinal())) < Thresholds.LOG2_FOLD_CHANGE ||
			subs.getMapScores().get(Score.P_VALUE.ordinal()) < Thresholds.LOG10_P_VALUE
		);
		return evidences;
	}

	private void saveScores(Evidence ev, Map<Integer, Double> mapScores) {
		for( Entry<Integer, Double> entry : mapScores.entrySet() ) {
			ScoreType type = em.find(ScoreType.class, entry.getKey());
			EvScore score = new EvScore();
			score.setEvidenceBean(ev);
			score.setScoreType(type);
			score.setValue(entry.getValue());
			em.persist(score);
		}	
	}
	
	private void saveScores(Replicate rep, Map<Integer, RepScoreBean> mapScores) {
		for( Entry<Integer, RepScoreBean> entry : mapScores.entrySet() ) {
			ScoreType type = em.find(ScoreType.class, entry.getKey());
			RepScore score = new RepScore();
			score.setReplicateBean(rep);
			score.setScoreType(type);
			score.setValue(entry.getValue().getValue());
			score.setImputed(entry.getValue().isImputed());
			em.persist(score);
		}
	}
}
