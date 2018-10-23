package es.ehubio.ubase.dl.providers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import es.ehubio.io.CsvReader;
import es.ehubio.io.Streams;
import es.ehubio.ubase.dl.entities.Experiment;
import es.ehubio.ubase.dl.entities.GroupScore;
import es.ehubio.ubase.dl.entities.Modification;
import es.ehubio.ubase.dl.entities.ModificationEvidence;
import es.ehubio.ubase.dl.entities.ModificationScore;
import es.ehubio.ubase.dl.entities.Peptide;
import es.ehubio.ubase.dl.entities.Peptide2Protein;
import es.ehubio.ubase.dl.entities.PeptideEvidence;
import es.ehubio.ubase.dl.entities.PeptideScore;
import es.ehubio.ubase.dl.entities.Protein;
import es.ehubio.ubase.dl.entities.Protein2Group;
import es.ehubio.ubase.dl.entities.ProteinGroup;
import es.ehubio.ubase.dl.entities.Replica;
import es.ehubio.ubase.dl.entities.Score;

public class MaxQuantDao implements Dao {
	private static class Peptide2Group {
		PeptideEvidence pev;
		String[] gids;
	}

	@Override
	public List<FileType> getInputFiles() {
		if( types == null ) {
			types = new ArrayList<>();
			types.add(new CsvFileType(FILE_PEP, null, true, PEP_ID, PEP_SEQ, PEP_BEFORE, PEP_AFTER, PEP_MISSED, PEP_MASS, PEP_UGROUP, PEP_UPROT, PEP_PEP, PEP_SCORE, PEP_GIDS, PEP_GLY));
			types.add(new CsvFileType(FILE_GRP, null, true, GRP_GID, GRP_PIDS, GRP_QVAL, GRP_SCORE, GRP_GLY));
			types.add(new CsvFileType(FILE_GLY, null, true, GLY_PROB, GLY_SCORE, GLY_PIDS, GLY_SEQ, GLY_POS));
			types.add(new NameFileType(FILE_PAR, null, false));
		}
		return types;
	}
	
	@Override
	public List<String> getSamples(File data) {
		List<String> samples = new ArrayList<>();
		File file = new File(data, FILE_PEP+".gz");
		try( BufferedReader br = new BufferedReader(Streams.getTextReader(file)) ) {
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
	public void persist(EntityManager em, Experiment exp, Map<String, Replica> replicas, Map<String, Protein> proteins, File data) throws Exception {
		Map<String, PeptideEvidence> mapPev = new HashMap<>();
		List<Peptide2Group> p2gs = new ArrayList<>();
		savePeptides(em, exp, replicas, data, mapPev, p2gs);
		saveModifications(em, replicas, mapPev, data);
		Set<String> groupSet = new HashSet<>();
		for( Peptide2Group p2g : p2gs )
			groupSet.addAll(Arrays.asList(p2g.gids));
		saveGroups(em, exp, replicas, proteins, groupSet, data);
		savePep2Prot(em, exp, p2gs);
	}
	
	@SuppressWarnings("unchecked")
	private void savePep2Prot(EntityManager em, Experiment exp, List<Peptide2Group> p2gs) {
		for( Peptide2Group p2g : p2gs ) {
			String seq = p2g.pev.getPeptideBean().getSequence();
			for( String gid :  p2g.gids ) {
				List<Protein> proteins = em
					.createQuery("SELECT p2g.proteinBean FROM Protein2Group p2g WHERE p2g.proteinGroup.name = :gid AND p2g.proteinBean.experimentBean = :exp")
					.setParameter("gid", gid)
					.setParameter("exp", exp)
					.getResultList();
				for( Protein protein : proteins ) {
					int pos = protein.getSequence().indexOf(seq);
					if( pos < 0 )
						continue;
					Peptide2Protein p2p = new Peptide2Protein();
					p2p.setPeptideEvidence(p2g.pev);
					p2p.setProteinBean(protein);
					p2p.setPosition(pos+1);
					em.persist(p2p);
				}
			}
		}
	}

	private void saveModifications(EntityManager em, Map<String, Replica> replicas, Map<String, PeptideEvidence> mapPev, File dir) throws Exception {
		CsvReader csv = new CsvReader("\t", true, false);
		csv.open(new File(dir, FILE_GLY+".gz").getAbsolutePath());
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

	private void savePeptides(EntityManager em, Experiment exp, Map<String, Replica> replicas, File dir,
			Map<String, PeptideEvidence> mapPev, List<Peptide2Group> p2gs) throws IOException {
		CsvReader csv = new CsvReader("\t", true, false);
		csv.open(new File(dir, FILE_PEP+".gz").getAbsolutePath());
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
			if( !csv.hasContent(iGids) ) {
				LOG.warning("Ignoring peptide not mapped to a protein group");
				continue;
			}
			pev.setPeptideBean(findPeptide(em, csv.getField(iSeq).toUpperCase()));
			pev.setMissedCleavages(csv.getIntField(iMissed));
			pev.setMass(csv.getDoubleField(iMass));
			pev.setPrev(getChar(csv, iBefore));
			pev.setAfter(getChar(csv, iAfter));
			pev.setExperimentBean(exp);
			em.persist(pev);
			savePeptideScore(em, pev, pepScore, csv, iPep);
			savePeptideScore(em, pev, mqScore, csv, iScore);
			mapPev.put(csv.getField(iId), pev);
			Peptide2Group p2g = new Peptide2Group();
			p2g.pev = pev;
			p2g.gids = csv.getField(iGids).split(";");
			p2gs.add(p2g);
		}
		
		csv.close();
	}

	private Character getChar(CsvReader csv, int i) {
		String str = csv.getField(i);
		if( str.length() != 1 )
			return null;
		char ch = str.toUpperCase().charAt(0);
		return ch == '-' ? null : ch;
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

	private void saveGroups(EntityManager em, Experiment exp, Map<String, Replica> replicas, Map<String, Protein> proteins, Set<String> groupSet, File dir) throws IOException {
		CsvReader csv = new CsvReader("\t", true, false);
		csv.open(new File(dir, FILE_GRP+".gz").getAbsolutePath());
		int iGid = csv.getIndex(GRP_GID), iGly = csv.getIndex(GRP_GLY);
		int iPids = csv.getIndex(GRP_PIDS);
		int iQval = csv.getIndex(GRP_QVAL), iScore = csv.getIndex(GRP_SCORE);
		Score qValue = em.find(Score.class, ScoreType.Q_VALUE.getId());
		Score mqScore = em.find(Score.class, ScoreType.MQ_SCORE.getId());
		Score lfqIntensity = em.find(Score.class, ScoreType.LFQ_INTENSITY.getId());
		Map<String, Integer> iLfq = new HashMap<>();
		for( String replica : replicas.keySet() )
			iLfq.put(replica, csv.getIndex(GRP_LFQ + " " + replica));
		
		while( csv.readLine() != null ) {
			if( !groupSet.contains(csv.getField(iGid)) || !csv.hasContent(iGly) || csv.getField(iPids).contains(EXCLUDE) )
				continue;
			ProteinGroup pg = new ProteinGroup();
			pg.setName(csv.getField(iGid));
			//pg.setName(truncate(csv.getField(iName)));
			em.persist(pg);
			String[] pids = csv.getField(iPids).split(";");
			for( String pid : pids ) {
				Protein protein = proteins.get(pid);
				if( protein == null ) {
					LOG.severe(pid + " not found");
				} else {
					em.persist(protein);
					Protein2Group p2g = new Protein2Group();
					p2g.setProteinBean(protein);
					p2g.setProteinGroup(pg);
					em.persist(p2g);
				}
			}
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
		}
		csv.close();
	}

	/*private String truncate(String field) {
		if( field.length() > MAX_INDEX_STR ) {
			LOG.warning("Truncated string: " + field);
			return field.substring(0, MAX_INDEX_STR);
		}
		return field; 
	}*/

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
	private static final String FILE_PAR = "mqpar.xml";
	//private static final int MAX_INDEX_STR = 255;
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
	//private static final String GRP_DESC = "Protein names";
	//private static final String GRP_NAME = "Gene names";
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
