package es.ehubio.ubase.bl;

import static es.ehubio.ubase.Constants.EXP_PREFIX;
import static es.ehubio.ubase.Constants.META_FILE;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.FileUtils;

import es.ehubio.ubase.Locator;
import es.ehubio.ubase.dl.entities.ExpCondition;
import es.ehubio.ubase.dl.entities.Experiment;
import es.ehubio.ubase.dl.entities.GroupScore;
import es.ehubio.ubase.dl.entities.ModificationEvidence;
import es.ehubio.ubase.dl.entities.ModificationScore;
import es.ehubio.ubase.dl.entities.Peptide2Group;
import es.ehubio.ubase.dl.entities.PeptideEvidence;
import es.ehubio.ubase.dl.entities.PeptideScore;
import es.ehubio.ubase.dl.entities.Replica;
import es.ehubio.ubase.dl.input.Metadata;
import es.ehubio.ubase.dl.input.Metafile;
import es.ehubio.ubase.dl.providers.Dao;
import es.ehubio.ubase.dl.providers.Provider; 

@LocalBean
@Stateless
public class Ubase implements Serializable {
	private static final long serialVersionUID = 1L;
	@PersistenceContext
	private EntityManager em;

	public void submit(Metadata metadata, Provider provider, File data) throws Exception {
		metadata.setProvider(provider);
		metadata.setData(data);
		metadata.setSubDate(new Date());
		Metafile.save(metadata, new File(data, META_FILE));
	}
	
	public List<Metadata> getPendingSubmissions() throws Exception {
		List<Metadata> results = new ArrayList<>();
		File dir = new File(Locator.getConfiguration().getSubmissionPath());
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(EXP_PREFIX);
			}
		});
		for( File file : files ) {
			File metafile = new File(file,META_FILE);
			if( !file.exists() )
				continue;
			Metadata metadata = Metafile.load(metafile);
			metadata.setData(file);
			results.add(metadata);
		}
		return results;
	}

	public void publish(Metadata metadata) throws Exception {
		metadata.setPubDate(new Date());		
		Experiment exp = meta2exp(metadata);
		Map<String, Replica> replicas = new HashMap<>();
		em.persist(exp);
		for( es.ehubio.ubase.dl.input.Condition cond : metadata.getConditions() ) {
			ExpCondition condition = new ExpCondition();
			condition.setName(cond.getName());
			condition.setDescription(cond.getDescription());
			condition.setExperimentBean(exp);
			em.persist(condition);
			for( String repName : cond.getReplicas() ) {
				Replica replica = new Replica();
				replica.setName(repName);
				replica.setExpConditionBean(condition);
				em.persist(replica);
				replicas.put(repName, replica);
			}
		}
		
		Dao dao = metadata.getProvider().getDao().newInstance();
		dao.persist(em, exp, replicas, metadata.getData());
		
		File dst = new File(Locator.getConfiguration().getArchivePath(), metadata.getData().getName());
		FileUtils.moveDirectory(metadata.getData(), dst);		
		Metafile.save(metadata, new File(dst, META_FILE));
	}
	
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

	public List<Peptide2Group> proteinSearch(String acc) {
		return null;
	}
	
	public List<Peptide2Group> textSearch(String text) {
		return null;
	}
	
	private Experiment meta2exp(Metadata metadata) {
		Experiment exp = new Experiment();
		exp.setAccession(metadata.getData().getName());
		exp.setTitle(metadata.getTitle());
		exp.setContactName(metadata.getContactName());
		exp.setContactMail(metadata.getContactMail());
		exp.setAffiliation(metadata.getAffiliation());
		exp.setDbVersion(metadata.getDbVersion());
		exp.setDescription(metadata.getDescription());
		exp.setOrganism(metadata.getOrganism());
		exp.setInstrument(metadata.getInstrument());
		exp.setSubDate(metadata.getSubDate());
		exp.setExpDate(metadata.getExpDate());
		exp.setPubDate(metadata.getPubDate());
		return exp;
	}
}
