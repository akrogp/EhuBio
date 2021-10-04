package es.ehubio.dubase.bl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.TransactionTimeout;

import es.ehubio.db.pubmed.Paper;
import es.ehubio.db.pubmed.PubMed;
import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.dl.entities.Ambiguity;
import es.ehubio.dubase.dl.entities.Author;
import es.ehubio.dubase.dl.entities.Cell;
import es.ehubio.dubase.dl.entities.Condition;
import es.ehubio.dubase.dl.entities.Enzyme;
import es.ehubio.dubase.dl.entities.EvRepScore;
import es.ehubio.dubase.dl.entities.EvScore;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.FileType;
import es.ehubio.dubase.dl.entities.Gene;
import es.ehubio.dubase.dl.entities.MethodSubtype;
import es.ehubio.dubase.dl.entities.MethodType;
import es.ehubio.dubase.dl.entities.ModRepScore;
import es.ehubio.dubase.dl.entities.ModScore;
import es.ehubio.dubase.dl.entities.ModType;
import es.ehubio.dubase.dl.entities.Modification;
import es.ehubio.dubase.dl.entities.Protein;
import es.ehubio.dubase.dl.entities.Publication;
import es.ehubio.dubase.dl.entities.Replicate;
import es.ehubio.dubase.dl.entities.ScoreType;
import es.ehubio.dubase.dl.entities.SupportingFile;
import es.ehubio.dubase.dl.entities.Taxon;
import es.ehubio.dubase.dl.input.LiuUbiquitomicsProvider;
import es.ehubio.dubase.dl.input.Metafile;
import es.ehubio.dubase.dl.input.Provider;
import es.ehubio.dubase.dl.input.UgoManualProvider;
import es.ehubio.dubase.dl.input.UgoProteomicsProvider;

@LocalBean
@Stateless
public class Importer {
	@PersistenceContext
	private EntityManager em;
	@Resource(name="es.ehubio.dubase.inputDir")
	private String inputPath;
	public static final String METADATA = "metadata.xml"; 
	
	@TransactionTimeout(600)	// 10 minutes ...
	public void saveUgoProteomics(String inputId) throws Exception {
		File expDir = new File(inputPath, inputId);
		File metaFile = new File(expDir, METADATA);		
		Experiment exp = Metafile.load(metaFile);		
		saveExperiment(exp, expDir);
	}	
	
	public int saveUgoCurated(String xlsName) throws Exception {
		File inputFile = new File(inputPath, xlsName);
		List<Experiment> exps = UgoManualProvider.loadExperiments(inputFile.getAbsolutePath());
		for( Experiment exp : exps ) {
			saveExperiment(exp, inputFile);
		}
		return exps.size();
	}
	
	private void saveExperiment(Experiment exp, File expDir) throws Exception {
		exp.getMethodBean().setType(em.find(MethodType.class, exp.getMethodBean().getType().getId()));
		if( exp.getMethodBean().getSubtype() != null )
			exp.getMethodBean().setSubtype(em.find(MethodSubtype.class, exp.getMethodBean().getSubtype().getId()));
		em.persist(exp.getMethodBean());		
		updateAuthor(exp);					
		setEnzyme(exp);
		setCell(exp);
		exp.setPubDate(new Date());
		em.persist(exp);		
		Paper paper = savePublications(exp);
		exp.setTitle(paper.getTitle());
		exp.setDescription(paper.getAbs());
		if( paper.getDate() != null )
			exp.setExpDate(paper.getDate());
		em.persist(exp);
		saveFiles(exp);
		saveConditions(exp);		
		Provider provider = findProvider(exp);
		List<Evidence> evs = provider.loadEvidences(expDir.getAbsolutePath(), exp); 
		filter(evs);
		saveEvidences(exp, evs);
	}

	private Paper savePublications(Experiment exp) throws IOException, ParseException {
		Paper first = null;
		for( Publication pub : exp.getPublications() ) {
			Paper paper = PubMed.fillPaper(pub.getPmid());
			if( first == null )
				first = paper;
			pub.setExperiment(exp);
			pub.setTitle(paper.getTitle());
			pub.setJournal(paper.getJournal());
			em.persist(pub);
		}
		return first;
	}

	private void saveConditions(Experiment exp) {
		if( exp.getConditions() == null )
			return;
		for( Condition cond : exp.getConditions() ) {
			cond.setExperimentBean(exp);
			em.persist(cond);
			for( Replicate rep : cond.getReplicates() ) {
				rep.setConditionBean(cond);
				em.persist(rep);
			}
		}
	}

	private void saveFiles(Experiment exp) {
		for( SupportingFile file : exp.getSupportingFiles() ) {
			file.setFileType(em.find(FileType.class, file.getFileType().getId()));
			file.setExperimentBean(exp);
			em.persist(file);
		}
	}

	private Provider findProvider(Experiment exp) {
		if( exp.getMethodBean().isProteomics() )
			return new UgoProteomicsProvider();
		else if( exp.getMethodBean().isManual() )
			return new UgoManualProvider();
		else if( exp.getMethodBean().isUbiquitomics() )
			return new LiuUbiquitomicsProvider();
		return null;
	}

	private void setCell(Experiment exp) {
		try {
			Cell cell = em
				.createNamedQuery("Cell.findByName", Cell.class)
				.setParameter("name", exp.getCellBean().getName())
				.getSingleResult();
			exp.setCellBean(cell);
		} catch (NoResultException e) {
			Taxon taxon = em.find(Taxon.class, exp.getCellBean().getTaxonBean().getId());
			exp.getCellBean().setTaxonBean(taxon);
			em.persist(exp.getCellBean());
		}		
	}

	private void setEnzyme(Experiment exp) {
		Enzyme enzyme = em
			.createNamedQuery("Enzyme.findByGene", Enzyme.class)
			.setParameter("gene", exp.getEnzymeBean().getGene())
			.getSingleResult();
		exp.setEnzymeBean(enzyme);		
	}

	private void updateAuthor(Experiment exp) {
		try {
			Author author = em
				.createNamedQuery("Author.findByMail", Author.class)
				.setParameter("mail", exp.getAuthorBean().getMail())
				.getSingleResult();
			exp.getAuthorBean().setId(author.getId());
			author = em.merge(exp.getAuthorBean());
			exp.setAuthorBean(author);
		} catch (NoResultException e) {
			em.persist(exp.getAuthorBean());
		}		
	}

	private void saveEvidences(Experiment exp, List<Evidence> evs) {		
		for( Evidence ev : evs ) {
			em.persist(ev);
			saveEvScores(ev);
			saveRepScores(ev);			
			saveAmbiguities(ev);
		}
	}

	private void saveAmbiguities(Evidence ev) {
		for( Ambiguity amb : ev.getAmbiguities() ) {
			try {
				Protein prot = em
					.createNamedQuery("Protein.findByAcc", Protein.class)
					.setParameter("acc", amb.getProteinBean().getAccession())
					.getSingleResult();
				amb.setProteinBean(prot);
			} catch (NoResultException e) {
				try {
					Gene gene = em
						.createNamedQuery("Gene.findByName", Gene.class)
						.setParameter("name", amb.getProteinBean().getGeneBean().getName())
						.getSingleResult();
					amb.getProteinBean().setGeneBean(gene);
				} catch (NoResultException e2) {
					Gene gene = amb.getProteinBean().getGeneBean();
					if( gene.getAliases() == null )
						gene.setAliases(gene.getName());
					em.persist(gene);
				}
				em.persist(amb.getProteinBean());
			}
			em.persist(amb);
			if( amb.getModifications() != null )
				for( Modification mod : amb.getModifications() ) {
					mod.setModType(em.find(ModType.class, mod.getModType().getId()));
					mod.setAmbiguityBean(amb);
					em.persist(mod);
					saveModScores(mod);
					saveModRepScores(mod);
				}
		}
	}	

	private void saveRepScores(Evidence ev) {
		if( ev.getRepScores() == null )
			return;
		for( EvRepScore repScore : ev.getRepScores() ) {
			repScore.setScoreType(em.find(ScoreType.class, repScore.getScoreType().getId()));
			em.persist(repScore);
		}		
	}

	private void saveEvScores(Evidence ev) {
		if( ev.getEvScores() == null )
			return;
		for( EvScore evScore : ev.getEvScores() ) {
			evScore.setScoreType(em.find(ScoreType.class, evScore.getScoreType().getId()));
			em.persist(evScore);
		}
	}
	
	private void saveModRepScores(Modification mod) {
		if( mod.getRepScores() == null )
			return;
		for( ModRepScore modRepScore : mod.getRepScores() ) {
			modRepScore.setScoreType(em.find(ScoreType.class, modRepScore.getScoreType().getId()));
			em.persist(modRepScore);
		}
	}

	private void saveModScores(Modification mod) {
		if( mod.getScores() == null )
			return;
		for( ModScore modScore : mod.getScores() ) {
			modScore.setScoreType(em.find(ScoreType.class, modScore.getScoreType().getId()));
			em.persist(modScore);
		}
	}

	private void filter(List<Evidence> evs) {
		for( Evidence ev : evs )
			ev.getAmbiguities().removeIf(amb -> amb.getProteinBean().getAccession().startsWith("CON_") || amb.getProteinBean().getAccession().startsWith("REV_") );
		evs.removeIf(ev -> {
			if( ev.getAmbiguities().isEmpty() )
				return true;
			int samplesImputed = countImputed(ev, false);
			int controlsImputed = countImputed(ev, true);
			if( samplesImputed != 0 && controlsImputed != 0 )
				if( samplesImputed > Thresholds.MAX_IMPUTATIONS || controlsImputed > Thresholds.MAX_IMPUTATIONS )
					return true;
			return false;
		});
	}

	private int countImputed(Evidence ev, boolean control) {	
		if( ev.getRepScores() == null )
			return 0;
		int count = 0;
		for( EvRepScore score : ev.getRepScores() ) {
			if( score.getReplicateBean().getConditionBean().getControl() != control )
				continue;
			if( score.getScoreType().getId() == es.ehubio.dubase.dl.input.ScoreType.LFQ_INTENSITY_LOG2.ordinal() && score.getImputed() )
				count++;
		}
		return count;
	}	
}
