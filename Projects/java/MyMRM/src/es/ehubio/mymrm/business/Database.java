package es.ehubio.mymrm.business;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

import es.ehubio.mymrm.data.Experiment;
import es.ehubio.mymrm.data.ExperimentFile;
import es.ehubio.mymrm.data.Fragment;
import es.ehubio.mymrm.data.IonType;
import es.ehubio.mymrm.data.Peptide;
import es.ehubio.mymrm.data.PeptideEvidence;
import es.ehubio.mymrm.data.Precursor;
import es.ehubio.mymrm.data.ScoreType;
import es.ehubio.mymrm.data.Transition;
import es.ehubio.panalyzer.Configuration;
import es.ehubio.panalyzer.MainModel;
import es.ehubio.proteomics.FragmentIon;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Score;

public class Database {
	private static final Logger logger = Logger.getLogger(Database.class.getName());
	private static final int FRAGMENTS=10;
	private static final int OKFRAGMENTS=3;
	private static EntityManagerFactory emf;
	private static BlockingQueue<ExperimentFeed> queue = new LinkedBlockingDeque<>();
	private static Worker worker;
	private static volatile boolean working = false;
	private static volatile boolean cancel = false;
	private static ExperimentFeed currentFeed = null;
	
	public static void connect() {
		emf = Persistence.createEntityManagerFactory("MyMRM");
		working = true;
		worker = new Worker();
		worker.start();
	}
	
	public static void close() throws InterruptedException {
		working = false;
		worker.join(2000);
		emf.close();
	}
	
	public static <T> List<T> findAll(final Class<T> c) {
		return doTransaction(new Operation<List<T>>() {
			@Override
			public List<T> run(EntityManager em) throws Exception {
				return em.createQuery(String.format("SELECT x FROM %s x",c.getSimpleName()),c).getResultList();
			}
		});
	}
	
	public static <T> void add(final T item) {
		doTransaction(new Operation<Void>() {
			@Override
			public Void run(EntityManager em) throws Exception {
				em.persist(item);
				return null;
			}
		});
	}
	
	public static <T> T findById(final Class<T> c, int id) {
		final Integer fid = id; 
		return doTransaction(new Operation<T>() {
			@Override
			public T run(EntityManager em) throws Exception {
				return (T)em.find(c, fid);
			}			
		});		
	}
	
	public static <T> boolean remove(final Class<T> c, int id) {
		final Integer fid = id;
		return doTransaction(new Operation<Boolean>() {
			@Override
			public Boolean run(EntityManager em) throws Exception {
				T item = em.find(c, fid);
				if( item == null )
					return false;
				em.remove(item);
				return true;
			}
		});
	}
	
	public static <T> void remove( final T c ) {
		doTransaction(new Operation<Void>() {
			@Override
			public Void run(EntityManager em) throws Exception {
				em.remove(c);
				return null;
			}
		});
	}
	
	public static List<Experiment> findExperiments() {
		return doTransaction(new Operation<List<Experiment>>() {
			@Override
			public List<Experiment> run(EntityManager em) throws Exception {
				List<Experiment> experiments = findAll(Experiment.class);
				for( Experiment experiment : experiments )
					experiment.setExperimentFiles(findExperimentFiles(experiment.getId()));			
				return experiments;
			}			
		});		
	}
	
	public static void removeExperiment( int experimentId ) {
		List<ExperimentFile> files = Database.findExperimentFiles(experimentId);
		for( ExperimentFile file : files )
			Database.remove(ExperimentFile.class, file.getId());
		Database.remove(Experiment.class, experimentId);
		Database.clearUnreferenced();
	}
	
	public static int countPeptidesBySequence( final String sequence ) {
		return doTransaction(new Operation<Integer>() {
			@Override
			public Integer run(EntityManager em) throws Exception {
				Number res = em.createQuery("SELECT COUNT (p) FROM Peptide p WHERE p.sequence = :sequence",Number.class)
					.setParameter("sequence", sequence)
					.getSingleResult();
				return res.intValue();
			}
		});		
	}
	
	public static List<Peptide> findBySequence( final String sequence ) {
		return doTransaction(new Operation<List<Peptide>>() {
			@Override
			public List<Peptide> run(EntityManager em) throws Exception {
				List<Peptide> list = null;
				try {
					list = em
						.createQuery("SELECT p FROM Peptide p WHERE p.sequence = :sequence",Peptide.class)
						.setParameter("sequence", sequence)
						.getResultList();
				} catch( NoResultException ex ) {			
				}
				return list == null ? new ArrayList<Peptide>() : list;
			}
		});		
	}	
	
	public static void feed( ExperimentFeed experiment ) throws InterruptedException {
		experiment.setStatus("Waiting ...");
		queue.put(experiment);
	}
	
	public static Collection<ExperimentFeed> getPendingExperiments() {
		if( currentFeed == null )
			return queue;
		List<ExperimentFeed> list = new ArrayList<>();
		list.add(currentFeed);
		list.addAll(queue);		
		return list;
	}
	
	public static void cancelFeed( ExperimentFeed feed ) throws InterruptedException {
		if( feed != currentFeed ) {
			queue.remove(feed);
			return;
		}
		cancel = true;
		while( cancel == true )
			Thread.sleep(100);
		remove(Experiment.class, feed.getExperiment().getId());
	}

	public static List<Peptide> findPeptides(String pepSequence) {
		List<Peptide> peptides = findBySequence(pepSequence);
		for( Peptide peptide : peptides )
			peptide.setPeptideEvidences(findEvidences(peptide.getId()));
		return peptides;
	}
	
	public static void clearUnreferenced() {
		doTransaction(new Operation<Void>() {
			@Override
			public Void run(EntityManager em) throws Exception {
				em.createQuery("DELETE FROM Peptide p WHERE p.id NOT IN (SELECT e.peptideBean.id FROM PeptideEvidence e)").executeUpdate();
				em.createQuery("DELETE FROM Precursor p WHERE p.id NOT IN (SELECT e.precursorBean.id FROM PeptideEvidence e)").executeUpdate();
				em.createQuery("DELETE FROM Fragment f WHERE f.id NOT IN (SELECT t.fragmentBean.id FROM Transition t)").executeUpdate();
				return null;
			}
		});		
	}

	private static List<PeptideEvidence> findEvidences(int idPeptide) {
		final Integer id = idPeptide;
		return doTransaction(new Operation<List<PeptideEvidence>>() {
			@Override
			public List<PeptideEvidence> run(EntityManager em) throws Exception {
				List<PeptideEvidence> evidences = null;
				try {
					evidences = em
						.createQuery("SELECT p FROM PeptideEvidence p WHERE p.peptideBean.id = :peptide", PeptideEvidence.class)
						.setParameter("peptide", id)
						.getResultList();
				} catch( NoResultException ex ) {			
				}
				return evidences == null ? new ArrayList<PeptideEvidence>() : evidences;
			}
		});		
	}

	public static List<Fragment> findFragments(int idPrecursor) {
		final Integer id = idPrecursor;
		return doTransaction(new Operation<List<Fragment>>() {
			@Override
			public List<Fragment> run(EntityManager em) throws Exception {
				List<Fragment> fragments = new ArrayList<>();
				try {
					List<Transition> transitions = em
						.createQuery("SELECT t FROM Transition t WHERE t.precursorBean.id = :precursor", Transition.class)
						.setParameter("precursor", id)
						.getResultList();
					for( Transition transition : transitions )
						fragments.add(transition.getFragmentBean());
				} catch( NoResultException ex ) {			
				}
				return fragments;
			}
		});		
	}

	public static List<es.ehubio.mymrm.data.Score> findScores(int evidenceId) {
		final Integer id = evidenceId;
		return doTransaction(new Operation<List<es.ehubio.mymrm.data.Score>>() {
			@Override
			public List<es.ehubio.mymrm.data.Score> run(EntityManager em) throws Exception {
				List<es.ehubio.mymrm.data.Score> scores = null;
				try {
					scores = em
						.createQuery("SELECT s FROM Score s WHERE s.peptideEvidenceBean.id = :evidence", es.ehubio.mymrm.data.Score.class)
						.setParameter("evidence", id)
						.getResultList();
				} catch( NoResultException ex ) {			
				}
				return scores == null ? new ArrayList<es.ehubio.mymrm.data.Score>() : scores;
			}
		});		
	}
	
	public static List<ExperimentFile> findExperimentFiles( int idExperiment ) {
		final Integer id = idExperiment;
		return doTransaction(new Operation<List<ExperimentFile>>() {
			@Override
			public List<ExperimentFile> run(EntityManager em) throws Exception {
				List<ExperimentFile> files = null;
				try {
					files = em
						.createQuery("SELECT f FROM ExperimentFile f WHERE f.experimentBean.id = :exp", ExperimentFile.class)
						.setParameter("exp", id)
						.getResultList();
				} catch( NoResultException ex ) {			
				}
				return files == null ? new ArrayList<ExperimentFile>() : files;
			}
		});		
	}
	
	public static int countExperimentFiles( int idExperiment ) {
		final Integer id = idExperiment;
		return doTransaction(new Operation<Integer>() {			
			@Override
			public Integer run(EntityManager em) throws Exception {
				Number res = em.createQuery("SELECT COUNT (f) FROM ExperimentFile f WHERE f.experimentBean.id = :exp",Number.class)
					.setParameter("exp", id)
					.getSingleResult();
				return res.intValue();
			}
		});		
	}
	
	private static <T> T doTransaction( Operation<T> op ) {
		T res = null;
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			res = op.run(em);
			em.getTransaction().commit();
			em.close();
		} catch( Exception e ) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
		return res;
	}
	
	private static abstract class Operation<T> {
		public abstract T run( EntityManager em ) throws Exception;
	}
	
	private static class Worker extends Thread {
		private static EntityManager em;
	
		@Override
		public void run() {
			em = emf.createEntityManager();		
			while( working ) {
				try {
					ExperimentFeed experiment = queue.take();
					currentFeed = experiment;
					em.getTransaction().begin();
					if( feed(experiment) )
						em.getTransaction().commit();
					else {
						em.getTransaction().rollback();
						cancel = false;
					}
					currentFeed = null;
				} catch( Exception e ) {
					e.printStackTrace();
					em.getTransaction().rollback();					
				}
			}
		}
		
		private boolean feed( ExperimentFeed e ) throws Exception {									
			em.persist(e.getExperiment());
			
			e.setStatus("Analyzing ...");
			MainModel panalyzer = new MainModel();
			panalyzer.setConfig(e.getConfiguration());
			panalyzer.run();
			MsMsData data = panalyzer.getData();

			double total = data.getPeptides().size();
			double partial = 0.0;
			for( es.ehubio.proteomics.Peptide peptide : data.getPeptides() ) {
				if( cancel == true )
					return false;
				partial += 1.0;
				if( Boolean.TRUE.equals(peptide.getDecoy()) || peptide.getConfidence().getOrder() > e.getConfidence().getOrder() )
					continue;
				Peptide dbPeptide = findByMassSequence(peptide.getMassSequence());
				if( dbPeptide == null ) {
					dbPeptide = new Peptide();
					dbPeptide.setSequence(peptide.getSequence());
					dbPeptide.setMassSequence(peptide.getMassSequence());				
					em.persist(dbPeptide);				
				}			
				for( Psm psm : peptide.getPsms() ) {
					//logger.info(String.format("Feedind with %s (mz=%s)", peptide.getSequence(), psm.getCalcMz()));
					Precursor precursor = new Precursor();
					precursor.setMz(psm.getCalcMz());
					precursor.setCharge(psm.getCharge());
					precursor.setRt(psm.getSpectrum().getRt());
					precursor.setIntensity(psm.getSpectrum().getIntensity());
					em.persist(precursor);
					PeptideEvidence evidence = new PeptideEvidence();
					evidence.setPeptideBean(dbPeptide);
					evidence.setPrecursorBean(precursor);				
					evidence.setExperimentBean(e.getExperiment());
					em.persist(evidence);
					feedIons(precursor,psm.getIons());
					feedScores(evidence,psm.getScores());
				}
				e.setStatus(String.format("Completed %.1f%%", partial/total*100.0));
				if( ((int)(partial+0.5))%20 == 0 )					
					logger.info(e.getStatus());
			}
			feedFiles(e.getExperiment(), e.getConfiguration());
			return true;
		}
		
		private static Peptide findByMassSequence( String massSequence ) {
			Peptide peptide = null;
			try {
				peptide = em
					.createQuery("SELECT p FROM Peptide p WHERE p.massSequence = :massSequence",Peptide.class)
					.setParameter("massSequence", massSequence)
					.getSingleResult();
			} catch( NoResultException ex ) {			
			}
			return peptide;
		}
		
		private static void feedIons( Precursor precursor, List<FragmentIon> ions) {
			Collections.sort(ions, new Comparator<FragmentIon>() {
				@Override
				public int compare(FragmentIon o1, FragmentIon o2) {
					return (int)Math.signum(o2.getIntensity()-o1.getIntensity());
				}
			});
			int count = FRAGMENTS;
			int countok = OKFRAGMENTS;
			Map<String,IonType> mapTypes = new HashMap<>();
			for( FragmentIon ion : filterIons(ions) ) {
				if( count > 0 || (countok > 0 && ion.getMzCalc() > precursor.getMz()) ) {
					IonType ionType = mapTypes.get(ion.getType().getName());
					if( ionType == null ) {
						ionType = findIonTypeByName(ion.getType().getName());
						if( ionType == null ) {
							ionType = new IonType();
							ionType.setName(ion.getType().getName());
							em.persist(ionType);
						}
						mapTypes.put(ion.getType().getName(), ionType);
					}
					Fragment fragment = new Fragment();
					fragment.setMz(ion.getMzCalc());
					fragment.setIntensity(ion.getIntensity());
					fragment.setError(ion.getMzError());
					fragment.setCharge(ion.getCharge());
					fragment.setPosition(ion.getIndex());
					fragment.setIonType(ionType);
					em.persist(fragment);
					Transition transition = new Transition();
					transition.setPrecursorBean(precursor);
					transition.setFragmentBean(fragment);
					em.persist(transition);
					count--;
					if( ion.getMzCalc() > precursor.getMz() )
						countok--;
				}			
			}
		}
		
		private static List<FragmentIon> filterIons(List<FragmentIon> ions) {
			Map<Double,FragmentIon> map = new HashMap<>();
			for( FragmentIon ion : ions ) {
				FragmentIon prev = map.get(ion.getMzCalc());
				if( prev == null || (Math.abs(prev.getMzError()/prev.getMzCalc()) > Math.abs(ion.getMzError()/ion.getMzCalc())) )
					map.put(ion.getMzCalc(), ion);			
			}
			List<FragmentIon> res = new ArrayList<>(map.values());
			Collections.sort(res, new Comparator<FragmentIon>() {
				@Override
				public int compare(FragmentIon o1, FragmentIon o2) {
					return (int)Math.signum(o2.getIntensity()-o1.getIntensity());
				}
			});
			return res;
		}

		private static IonType findIonTypeByName( String name ) {
			IonType ionType = null;
			try {
				ionType = em
					.createQuery("SELECT i FROM IonType i WHERE i.name = :name",IonType.class)
					.setParameter("name", name)
					.getSingleResult();
			} catch( NoResultException ex ) {			
			}
			return ionType;
		}
		
		private static void feedScores(PeptideEvidence evidence, Set<Score> scores) {
			Map<String, ScoreType> mapTypes = new HashMap<>();
			for( Score score : scores ) {
				ScoreType scoreType = mapTypes.get(score.getName());
				if( scoreType == null ) {
					scoreType = findScoreTypeByName(score.getName());
					if( scoreType == null ) {
						scoreType = new ScoreType();
						scoreType.setName(score.getName());
						scoreType.setDescription(score.getType().getDescription());
						scoreType.setLargerBetter(score.getType().isLargerBetter());
						em.persist(scoreType);
					}
					mapTypes.put(score.getName(), scoreType);
				}
				es.ehubio.mymrm.data.Score dbScore = new es.ehubio.mymrm.data.Score();
				dbScore.setScoreType(scoreType);
				dbScore.setPeptideEvidenceBean(evidence);
				dbScore.setValue(score.getValue());		
				em.persist(dbScore);
			}
		}
		
		private static ScoreType findScoreTypeByName( String name ) {
			ScoreType scoreType = null;
			try {
				scoreType = em
					.createQuery("SELECT s FROM ScoreType s WHERE s.name = :name",ScoreType.class)
					.setParameter("name", name)
					.getSingleResult();
			} catch( NoResultException ex ) {			
			}
			return scoreType;
		}
		
		private static void feedFiles( Experiment e, Configuration cfg ) {
			for( String input : cfg.getReplicates().get(0).getFractions() ) {
				ExperimentFile file = new ExperimentFile();
				file.setExperimentBean(e);
				file.setFileName(new File(input).getName());
				em.persist(file);
			}
		}
	}
}