package es.ehubio.ubase.bl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.ehubio.ubase.bl.result.ModificationResult;
import es.ehubio.ubase.bl.result.PeptideResult;
import es.ehubio.ubase.bl.result.ProteinResult;
import es.ehubio.ubase.bl.result.ScoreResult;
import es.ehubio.ubase.dl.entities.ModificationEvidence;
import es.ehubio.ubase.dl.entities.ModificationScore;
import es.ehubio.ubase.dl.entities.Peptide2Protein;
import es.ehubio.ubase.dl.entities.PeptideEvidence;
import es.ehubio.ubase.dl.entities.PeptideScore; 

@LocalBean
@Stateless
public class Usearch implements Serializable {
	private static final long serialVersionUID = 1L;
	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<PeptideResult> peptideSearch(String pep) {
		List<PeptideEvidence> pevs = em.createQuery("SELECT pev FROM PeptideEvidence pev WHERE pev.peptideBean.sequence = :seq")
				.setParameter("seq", pep)
				.getResultList();
		return pev2Result(pevs);
	}
	
	private List<PeptideResult> pev2Result(List<PeptideEvidence> pevs) {
		List<PeptideResult> results = new ArrayList<>();
		for( PeptideEvidence pev : pevs ) {
			PeptideResult result = new PeptideResult(pev);
			result.setExperiment(pev.getExperimentBean());
			results.add(result);
			addPeptideScores(pev, result);
			addModifications(pev, result);
			addProteins(pev, result);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	private void addProteins(PeptideEvidence pev, PeptideResult result) {
		List<Peptide2Protein> p2ps = em.createQuery("SELECT p2p FROM Peptide2Protein p2p WHERE p2p.peptideEvidence = :pev")
				.setParameter("pev", pev)
				.getResultList();
		for( Peptide2Protein p2p : p2ps ) {
			ProteinResult prot = new ProteinResult(p2p.getProteinBean());
			prot.setPosition(p2p.getPosition());
			result.getProts().add(prot);
		}
	}

	@SuppressWarnings("unchecked")
	private void addModifications(PeptideEvidence pev, PeptideResult result) {
		List<ModificationEvidence> mevs = em.createQuery("SELECT mev FROM ModificationEvidence mev WHERE mev.peptideEvidenceBean = :pev")
				.setParameter("pev", pev)
				.getResultList();
		for( ModificationEvidence mev : mevs ) {
			ModificationResult mod = new ModificationResult();
			mod.setName(mev.getModificationBean().getName());
			mod.setDescription(mev.getModificationBean().getDescription());
			mod.setDeltaMass(mev.getDeltaMass());
			mod.setPosition(mev.getPosition());
			result.getMods().add(mod);
			List<ModificationScore> scores = em.createQuery("SELECT s FROM ModificationScore s WHERE s.modificationEvidence = :mev")
					.setParameter("mev", mev)
					.getResultList();
			for( ModificationScore score : scores ) {
				ScoreResult s = new ScoreResult();
				s.setName(score.getScore().getName());
				s.setDescription(score.getScore().getDescription());
				s.setValue(score.getValue());
				mod.getScores().put(s.getName(), s);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void addPeptideScores(PeptideEvidence pev, PeptideResult result) {
		List<PeptideScore> scores = em.createQuery("SELECT s FROM PeptideScore s WHERE s.peptideEvidence = :pev")
				.setParameter("pev", pev)
				.getResultList();
		for( PeptideScore score : scores ) {
			ScoreResult s = new ScoreResult();
			s.setName(score.getScore().getName());
			s.setDescription(score.getScore().getDescription());
			s.setValue(score.getValue());
			result.getScores().put(s.getName(), s);
		}
	}

	@SuppressWarnings("unchecked")
	public List<PeptideResult> textSearch(String acc) {
		List<PeptideEvidence> pevs = em
				.createQuery("SELECT p2p.peptideEvidence FROM Peptide2Protein p2p WHERE p2p.proteinBean.accession LIKE :acc OR p2p.proteinBean.entry LIKE :acc OR p2p.proteinBean.name LIKE :acc OR p2p.proteinBean.gene LIKE :acc")
				.setParameter("acc", "%"+acc+"%")
				.getResultList();
		return pev2Result(pevs);
	}
	
	@SuppressWarnings("unchecked")
	public List<PeptideResult> expSearch(String expAccession) {
		List<PeptideEvidence> pevs = em.createQuery("SELECT pev FROM PeptideEvidence pev WHERE pev.experimentBean.accession = :exp")
				.setParameter("exp", expAccession)
				.getResultList();
		return pev2Result(pevs);
	}
}
