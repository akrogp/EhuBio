package es.ehubio.ubase.bl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.ehubio.ubase.bl.result.ModificationResult;
import es.ehubio.ubase.bl.result.PeptideResult;
import es.ehubio.ubase.bl.result.ProteinResult;
import es.ehubio.ubase.bl.result.ScoreResult;
import es.ehubio.ubase.dl.entities.GroupScore;
import es.ehubio.ubase.dl.entities.ModificationEvidence;
import es.ehubio.ubase.dl.entities.ModificationScore;
import es.ehubio.ubase.dl.entities.Peptide2Group;
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
		List<PeptideResult> results = new ArrayList<>();
		List<PeptideEvidence> pevs = em.createQuery("SELECT pev FROM PeptideEvidence pev WHERE pev.peptideBean.sequence = :seq")
				.setParameter("seq", pep)
				.getResultList();
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
		List<Peptide2Group> p2gs = em.createQuery("SELECT p2g FROM Peptide2Group p2g WHERE p2g.peptideEvidence = :pev")
				.setParameter("pev", pev)
				.getResultList();
		for( Peptide2Group p2g : p2gs ) {
			ProteinResult prot = new ProteinResult();
			prot.setAccession(p2g.getProteinGroupBean().getAccessions());
			prot.setName(p2g.getProteinGroupBean().getName());
			prot.setDescription(p2g.getProteinGroupBean().getDescription());
			result.getProts().add(prot);
			List<GroupScore> scores = em.createQuery("SELECT s FROM GroupScore s WHERE s.proteinGroupBean = :grp AND replicaBean IS NULL")
					.setParameter("grp", p2g.getProteinGroupBean())
					.getResultList();
			for( GroupScore score : scores ) {
				ScoreResult s = new ScoreResult();
				s.setName(score.getScore().getName());
				s.setDescription(score.getScore().getDescription());
				s.setValue(score.getValue());
				prot.getScores().put(s.getName(), s);
			}
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
	public List<PeptideResult> proteinSearch(String acc) {
		List<Peptide2Group> p2gs = em.createQuery("SELECT p2g FROM Peptide2Group p2g WHERE p2g.proteinGroupBean.accessions LIKE :acc OR p2g.proteinGroupBean.name LIKE :acc")
				.setParameter("acc", "%"+acc+"%")
				.getResultList();
		return group2Result(p2gs);
	}
	
	private List<PeptideResult> group2Result(List<Peptide2Group> p2gs) {
		List<PeptideResult> result = new ArrayList<>();
		Set<String> peps = new HashSet<>();
		for( Peptide2Group p2g : p2gs )
			peps.add(p2g.getPeptideEvidence().getPeptideBean().getSequence());
		for( String pep : peps )
			result.addAll(peptideSearch(pep));
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<PeptideResult> textSearch(String txt) {
		List<Peptide2Group> p2gs = em.createQuery("SELECT p2g FROM Peptide2Group p2g WHERE p2g.proteinGroupBean.name LIKE :txt OR p2g.proteinGroupBean.description LIKE :txt")
				.setParameter("txt", "%"+txt+"%")
				.getResultList();
		return group2Result(p2gs);
	}
}
