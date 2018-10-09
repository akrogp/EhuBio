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

import javax.persistence.EntityManager;

import es.ehubio.io.CsvReader;
import es.ehubio.ubase.dl.entities.Experiment;
import es.ehubio.ubase.dl.entities.GroupScore;
import es.ehubio.ubase.dl.entities.ProteinGroup;
import es.ehubio.ubase.dl.entities.Replica;
import es.ehubio.ubase.dl.entities.Score;

public class MaxQuantDao implements Dao {

	@Override
	public List<FileType> getInputFiles() {
		if( types == null ) {
			types = new ArrayList<>();
			types.add(new CsvFileType(FILE_PEP, null, PEP_SEQ, PEP_MISSED));
			types.add(new CsvFileType(FILE_GRP, null, GRP_GID, GRP_PIDS, GRP_DESC, GRP_NAME, GRP_QVAL, GRP_SCORE));
			types.add(new CsvFileType(FILE_GLY, null, GLY_PROB, GLY_SIG));
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
	public void persist(EntityManager em, Experiment exp, Map<String, Replica> replicas, File data) throws IOException {
		Map<String, ProteinGroup> mapGroup = saveProteins(em, exp, replicas, data);
		savePeptides(em, exp, replicas, mapGroup, data);
	}
	
	private void savePeptides(EntityManager em, Experiment exp, Map<String, Replica> replicas,
			Map<String, ProteinGroup> mapGroup, File dir) throws IOException {
		CsvReader csv = new CsvReader("\t", true, false);
		csv.open(new File(dir, FILE_PEP).getAbsolutePath());
		
		csv.close();
	}

	private Map<String, ProteinGroup> saveProteins(EntityManager em, Experiment exp, Map<String, Replica> replicas, File dir) throws IOException {
		Map<String, ProteinGroup> mapGroup = new HashMap<>();
		CsvReader csv = new CsvReader("\t", true, false);
		csv.open(new File(dir, FILE_GRP).getAbsolutePath());
		int iGid = csv.getIndex(GRP_GID);
		int iPids = csv.getIndex(GRP_PIDS), iDesc = csv.getIndex(GRP_DESC), iName = csv.getIndex(GRP_NAME);
		int iQval = csv.getIndex(GRP_QVAL), iScore = csv.getIndex(GRP_SCORE);
		Score qValue = em.find(Score.class, ScoreType.Q_VALUE.getId());
		Score mqScore = em.find(Score.class, ScoreType.MQ_SCORE.getId());
		Score lfqIntensity = em.find(Score.class, ScoreType.LFQ_INTENSITY.getId());
		Map<String, Integer> iLfq = new HashMap<>();
		for( String replica : replicas.keySet() )
			iLfq.put(replica, csv.getIndex(GRP_LFQ + " " + replica));
		
		while( csv.readLine() != null ) {
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

	private void saveGroupScore(EntityManager em, ProteinGroup pg, Score qValue, CsvReader csv, int iQval) {
		GroupScore score = new GroupScore();
		score.setProteinGroupBean(pg);
		score.setScore(qValue);
		score.setValue(csv.getDoubleField(iQval));
		em.persist(score);
	}

	private List<FileType> types;
	
	private static final Logger LOG = Logger.getLogger(MaxQuantDao.class.getName());
	private static final String FILE_PEP = "peptides.txt";
	private static final String FILE_GRP = "proteinGroups.txt";
	private static final String FILE_GLY = "GlyGly (K)Sites.txt";
	private static final int MAX_INDEX_STR = 255;
	private static final String EXCLUDE = "__";
	
	private static final String PEP_SEQ = "Sequence";
	private static final String PEP_MISSED = "Missed cleavages";
	
	private static final String GRP_GID = "id";
	private static final String GRP_PIDS = "Protein IDs";
	private static final String GRP_DESC = "Protein names";
	private static final String GRP_NAME = "Gene names";
	private static final String GRP_QVAL = "Q-value";
	private static final String GRP_SCORE = "Score";
	private static final String GRP_LFQ = "LFQ intensity";
	
	private static final String GLY_PROB = "Localization prob";
	private static final String GLY_SIG = "GlyGly (K)";
}
