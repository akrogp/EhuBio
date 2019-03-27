package es.ehubio.dubase.bl;

import java.io.IOException;
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
import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.bl.beans.ExperimentBean;
import es.ehubio.dubase.bl.beans.RepScoreBean;
import es.ehubio.dubase.bl.beans.ReplicateBean;
import es.ehubio.dubase.dl.entities.Ambiguity;
import es.ehubio.dubase.dl.entities.Author;
import es.ehubio.dubase.dl.entities.Enzyme;
import es.ehubio.dubase.dl.entities.EvScore;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.Method;
import es.ehubio.dubase.dl.entities.ModType;
import es.ehubio.dubase.dl.entities.Modification;
import es.ehubio.dubase.dl.entities.RepScore;
import es.ehubio.dubase.dl.entities.Replicate;
import es.ehubio.dubase.dl.entities.ScoreType;
import es.ehubio.dubase.dl.entities.Substrate;

@LocalBean
@Stateless
public class Importer {
	@PersistenceContext
	private EntityManager em;
	private static final int GLYGLY = 1;
	
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
		ModType modType = em.find(ModType.class, GLYGLY);
		for( EvidenceBean evBean : filter(EvidenceFile.loadEvidences(evidencesPath)) ) {
			Evidence ev = new Evidence();
			ev.setExperimentBean(experiment);
			em.persist(ev);
			
			saveScores(ev, evBean.getMapScores());
			saveReplicates(ev, evBean.getSamples(), false);
			saveReplicates(ev, evBean.getControls(), true);
			saveModifications(ev, modType, evBean.getModPositions());
			
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

	private void saveModifications(Evidence ev, ModType modType, List<Integer> positions) {
		for( Integer pos : positions ) {
			Modification mod = new Modification();
			mod.setEvidenceBean(ev);
			mod.setModType(modType);
			mod.setPosition(pos);
			em.persist(mod);
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
		evidences.removeIf(ev -> {
			if( Math.abs(ev.getMapScores().get(Score.FOLD_CHANGE.ordinal())) < Thresholds.LOG2_FOLD_CHANGE )
				return true;
			if( ev.getMapScores().get(Score.P_VALUE.ordinal()) < Thresholds.LOG10_P_VALUE )
				return true;
			int samplesImputed = countImputed(ev.getSamples());
			int controlsImputed = countImputed(ev.getControls());
			if( samplesImputed != 0 && controlsImputed != 0 )
				if( samplesImputed > Thresholds.MAX_IMPUTATIONS || controlsImputed > Thresholds.MAX_IMPUTATIONS )
					return true;
			return false;
		});
		return evidences;
	}

	private int countImputed(List<ReplicateBean> reps) {
		int count = 0;
		for( ReplicateBean rep : reps )
			if( rep.getMapScores().get(Score.LFQ_INTENSITY.ordinal()).isImputed() )
				count++;
		return count;
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
