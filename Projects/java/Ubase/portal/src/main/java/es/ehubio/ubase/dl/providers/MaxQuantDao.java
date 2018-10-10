package es.ehubio.ubase.dl.providers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import es.ehubio.io.CsvReader;
import es.ehubio.ubase.dl.entities.Experiment;
import es.ehubio.ubase.dl.entities.GroupScore;
import es.ehubio.ubase.dl.entities.Modification;
import es.ehubio.ubase.dl.entities.ModificationEvidence;
import es.ehubio.ubase.dl.entities.ModificationScore;
import es.ehubio.ubase.dl.entities.Peptide;
import es.ehubio.ubase.dl.entities.Peptide2Group;
import es.ehubio.ubase.dl.entities.PeptideEvidence;
import es.ehubio.ubase.dl.entities.PeptideScore;
import es.ehubio.ubase.dl.entities.ProteinGroup;
import es.ehubio.ubase.dl.entities.Replica;
import es.ehubio.ubase.dl.entities.Score;

public class MaxQuantDao implements Dao {

	@Override
	public List<FileType> getInputFiles() {
		if( types == null ) {
			types = new ArrayList<>();
			types.add(new CsvFileType(FILE_PEP, null, PEP_ID, PEP_SEQ, PEP_BEFORE, PEP_AFTER, PEP_MISSED, PEP_MASS, PEP_UGROUP, PEP_UPROT, PEP_PEP, PEP_SCORE, PEP_GIDS, PEP_GLY));
			types.add(new CsvFileType(FILE_GRP, null, GRP_GID, GRP_PIDS, GRP_DESC, GRP_NAME, GRP_QVAL, GRP_SCORE, GRP_GLY));
			types.add(new CsvFileType(FILE_GLY, null, GLY_PROB, GLY_SCORE, GLY_PIDS, GLY_SEQ, GLY_POS));
		}
		return types;
	}
	
	@Override
	public List<String> getSamples(File data) {
		List<String> samples = new ArrayList<>();
		File file = new File(data, "peptides.txt");
		try( BufferedReader br = new BufferedReader(new FileReader(file)) ) {
			String[] fields = br.readLine().split("\\t");
			for( String field : fields )
				if( field.startsWith("Experiment ") )
					samples.add(field.substring(11));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return samples;
	}
	
	@Override
	public void persist(EntityManager em, Experiment exp, Map<String, Replica> replicas, File data) throws Exception {
		Map<String, ProteinGroup> mapGroup = saveGroups(em, exp, replicas, data);
		Map<String, PeptideEvidence> mapPev = savePeptides(em, exp, replicas, mapGroup, data);
		saveModifications(em, replicas, mapPev, data);
	}
	
	private void saveModifications(EntityManager em, Map<String, Replica> replicas, Map<String, PeptideEvidence> mapPev, File dir) throws Exception {
		CsvReader csv = new CsvReader("\t", true, false);
		csv.open(new File(dir, FILE_GLY).getAbsolutePath());
		int iProb = csv.getIndex(GLY_PROB), iScore = csv.getIndex(GLY_SCORE);
		int iSeq = csv.getIndex(GLY_SEQ), iPos = csv.getIndex(GLY_POS);
		int iPids = csv.getIndex(GLY_PIDS);
		Score probScore = em.find(Score.class, ScoreType.LOC_PROB.getId());
		Score modScore = em.find(Score.class, ScoreType.MQ_LOC_SCORE.getId());
		Modification glyMod = em.find(Modification.class, ModificationType.GLYGLY.getId());
		while( csv.readLine() != null ) {
			String seq = MOD_PATTERN.matcher(csv.getField(iSeq)).replaceAll("");
			int pos = csv.getIntField(iPos);
			String[] pids = csv.getField(iPids).split(";");
			for( String pid : pids ) {
				PeptideEvidence pev = mapPev.get(pid);
				if( pev == null )
					continue;
				int offset = strOffset(pev.getPeptideBean().getSequence(), seq);
				ModificationEvidence mev = new ModificationEvidence();
				mev.setModificationBean(glyMod);
				mev.setPeptideEvidenceBean(pev);
				mev.setPosition(pos+offset);
				em.persist(mev);
				saveModScore(em, mev, probScore, csv, iProb);
				saveModScore(em, mev, modScore, csv, iScore);
			}
		}
		csv.close();
	}

	private int strOffset(String seq1, String seq2) throws Exception {
		int offset = strSemiOffset(seq1, seq2);
		if( offset >= 0 )
			return offset;
		offset = strSemiOffset(seq2, seq1);
		if( offset < 0 )
			throw new Exception(String.format("No overlap found between %s and %s",seq1,seq2));
		return -offset;
	}

	private void saveModScore(EntityManager em, ModificationEvidence mev, Score type, CsvReader csv, int i) {
		ModificationScore score = new ModificationScore();
		score.setModificationEvidence(mev);
		score.setScore(type);
		score.setValue(csv.getDoubleField(i));
		em.persist(score);
	}

	private int strSemiOffset(String seq1, String seq2) {
		if( seq2.length() > seq1.length() )
			seq2 = seq2.substring(0, seq1.length());
		int offset;
		do {
			offset = seq1.indexOf(seq2);
			if( offset < 0 )
				seq2 = seq2.substring(0, seq2.length()-1);
		} while( offset < 0 && seq2.length() > 1 );
		return offset;
	}

	private Map<String, PeptideEvidence> savePeptides(EntityManager em, Experiment exp, Map<String, Replica> replicas,
			Map<String, ProteinGroup> mapGroup, File dir) throws IOException {
		Map<String, PeptideEvidence> result = new HashMap<>();
		CsvReader csv = new CsvReader("\t", true, false);
		csv.open(new File(dir, FILE_PEP).getAbsolutePath());
		int iId = csv.getIndex(PEP_ID), iSeq = csv.getIndex(PEP_SEQ);
		int iBefore = csv.getIndex(PEP_BEFORE), iAfter = csv.getIndex(PEP_AFTER);
		int iMissed = csv.getIndex(PEP_MISSED), iMass = csv.getIndex(PEP_MASS);
		int iUgrp = csv.getIndex(PEP_UGROUP), iUprot = csv.getIndex(PEP_UPROT);
		int iPep = csv.getIndex(PEP_PEP), iScore = csv.getIndex(PEP_SCORE);
		int iGids = csv.getIndex(PEP_GIDS), iGly = csv.getIndex(PEP_GLY);
		Score pepScore = em.find(Score.class, ScoreType.PEP.getId());
		Score mqScore = em.find(Score.class, ScoreType.MQ_SCORE.getId());
		
		while( csv.readLine() != null ) {
			PeptideEvidence pev = new PeptideEvidence();
			pev.setUniqueGroup("yes".equals(csv.getField(iUgrp)));
			pev.setUniqueProtein("yes".equals(csv.getField(iUprot)));
			if( !Boolean.TRUE.equals(pev.getUniqueGroup()) && !Boolean.TRUE.equals(pev.getUniqueProtein()) )
				continue;
			String mods = csv.getField(iGly);
			if( mods == null || mods.isEmpty() )
				continue;
			pev.setPeptideBean(findPeptide(em, csv.getField(iSeq).toUpperCase()));
			pev.setMissedCleavages(csv.getIntField(iMissed));
			pev.setMass(csv.getDoubleField(iMass));
			pev.setPrev(getChar(csv, iBefore));
			pev.setAfter(getChar(csv, iAfter));
			pev.setExperimentBean(exp);
			em.persist(pev);
			savePeptideScore(em, pev, pepScore, csv, iPep);
			savePeptideScore(em, pev, mqScore, csv, iScore);
			if( !savePep2Grp(em, mapGroup, pev, csv, iGids) )
				em.remove(pev);
			else
				result.put(csv.getField(iId), pev);
		}
		
		csv.close();
		return result;
	}

	private boolean savePep2Grp(EntityManager em, Map<String, ProteinGroup> mapGroup, PeptideEvidence pev, CsvReader csv, int i) {
		String[] gids = csv.getField(i).split(";");
		boolean hasGroups = false;
		for( String gid : gids ) {
			ProteinGroup grp = mapGroup.get(gid);
			if( grp == null ) {
				//LOG.info("Could not find group: " + gid);
				continue;
			}
			hasGroups = true;
			Peptide2Group p2g = new Peptide2Group();
			p2g.setPeptideEvidence(pev);
			p2g.setProteinGroupBean(grp);
			em.persist(p2g);
		}
		return hasGroups;
	}

	private Character getChar(CsvReader csv, int i) {
		String str = csv.getField(i);
		if( str.length() != 1 )
			return null;
		return str.toUpperCase().charAt(0);
	}

	private Peptide findPeptide(EntityManager em, String seq) {
		Peptide pep = null;
		try {
			pep = (Peptide)em.createNamedQuery("Peptide.findSeq").setParameter("seq", seq).getSingleResult();
		} catch (NoResultException e) {
		}
		if( pep == null ) {
			pep = new Peptide();
			pep.setSequence(seq);
			em.persist(pep);
		}
		return pep;
	}

	private void savePeptideScore(EntityManager em, PeptideEvidence pev, Score type, CsvReader csv, int i) {
		PeptideScore pepScore = new PeptideScore();
		pepScore.setPeptideEvidence(pev);
		pepScore.setScore(type);
		pepScore.setValue(csv.getDoubleField(i));
		em.persist(pepScore);
	}

	private Map<String, ProteinGroup> saveGroups(EntityManager em, Experiment exp, Map<String, Replica> replicas, File dir) throws IOException {
		Map<String, ProteinGroup> mapGroup = new HashMap<>();
		CsvReader csv = new CsvReader("\t", true, false);
		csv.open(new File(dir, FILE_GRP).getAbsolutePath());
		int iGid = csv.getIndex(GRP_GID), iGly = csv.getIndex(GRP_GLY);
		int iPids = csv.getIndex(GRP_PIDS), iDesc = csv.getIndex(GRP_DESC), iName = csv.getIndex(GRP_NAME);
		int iQval = csv.getIndex(GRP_QVAL), iScore = csv.getIndex(GRP_SCORE);
		Score qValue = em.find(Score.class, ScoreType.Q_VALUE.getId());
		Score mqScore = em.find(Score.class, ScoreType.MQ_SCORE.getId());
		Score lfqIntensity = em.find(Score.class, ScoreType.LFQ_INTENSITY.getId());
		Map<String, Integer> iLfq = new HashMap<>();
		for( String replica : replicas.keySet() )
			iLfq.put(replica, csv.getIndex(GRP_LFQ + " " + replica));
		
		while( csv.readLine() != null ) {
			if( !csv.hasContent(iGly) )
				continue;
			ProteinGroup pg = new ProteinGroup();
			pg.setAccessions(truncate(csv.getField(iPids)));
			if( pg.getAccessions().contains(EXCLUDE) )
				continue;
			pg.setName(truncate(csv.getField(iName)));
			pg.setDescription(truncate(csv.getField(iDesc)));
			pg.setExperimentBean(exp);
			em.persist(pg);
			saveGroupScore(em, pg, qValue, csv, iQval);
			saveGroupScore(em, pg, mqScore, csv, iScore);
			for( Map.Entry<String, Replica> entry : replicas.entrySet() ) {
				GroupScore score = new GroupScore();
				score.setProteinGroupBean(pg);
				score.setReplicaBean(entry.getValue());
				score.setScore(lfqIntensity);
				score.setValue(csv.getDoubleField(iLfq.get(entry.getKey())));
				em.persist(score);
			}
			mapGroup.put(csv.getField(iGid), pg);
		}
		csv.close();
		return mapGroup;
	}

	private String truncate(String field) {
		if( field.length() > MAX_INDEX_STR ) {
			LOG.warning("Truncated string: " + field);
			return field.substring(0, MAX_INDEX_STR);
		}
		return field; 
	}

	private void saveGroupScore(EntityManager em, ProteinGroup pg, Score qValue, CsvReader csv, int i) {
		GroupScore score = new GroupScore();
		score.setProteinGroupBean(pg);
		score.setScore(qValue);
		score.setValue(csv.getDoubleField(i));
		em.persist(score);
	}

	private List<FileType> types;
	private static final Logger LOG = Logger.getLogger(MaxQuantDao.class.getName());
	private static final Pattern MOD_PATTERN = Pattern.compile("\\([0-9\\.]*\\)");
	
	private static final String FILE_PEP = "peptides.txt";
	private static final String FILE_GRP = "proteinGroups.txt";
	private static final String FILE_GLY = "GlyGly (K)Sites.txt";
	private static final int MAX_INDEX_STR = 255;
	private static final String EXCLUDE = "__";
	
	private static final String PEP_ID = "id";
	private static final String PEP_SEQ = "Sequence";
	private static final String PEP_MISSED = "Missed cleavages";
	private static final String PEP_BEFORE = "Amino acid before";
	private static final String PEP_AFTER = "Amino acid after";
	private static final String PEP_MASS = "Mass";
	private static final String PEP_UGROUP = "Unique (Groups)";
	private static final String PEP_UPROT = "Unique (Proteins)";
	private static final String PEP_PEP = "PEP";
	private static final String PEP_SCORE = "Score";
	private static final String PEP_GIDS = "Protein group IDs";
	private static final String PEP_GLY = "GlyGly (K) site IDs";
	
	private static final String GRP_GID = "id";
	private static final String GRP_PIDS = "Protein IDs";
	private static final String GRP_DESC = "Protein names";
	private static final String GRP_NAME = "Gene names";
	private static final String GRP_QVAL = "Q-value";
	private static final String GRP_SCORE = "Score";
	private static final String GRP_LFQ = "LFQ intensity";
	private static final String GRP_GLY = "GlyGly (K) site IDs";
	
	private static final String GLY_PROB = "Localization prob";
	private static final String GLY_SCORE = "Score for localization";
	private static final String GLY_SEQ = "GlyGly (K) Probabilities";
	private static final String GLY_POS = "Position in peptide";
	private static final String GLY_PIDS = "Peptide IDs";
}
