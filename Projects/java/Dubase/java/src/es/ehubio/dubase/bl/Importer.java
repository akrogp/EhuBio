package es.ehubio.dubase.bl;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.TransactionTimeout;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.db.pubmed.Paper;
import es.ehubio.db.pubmed.PubMed;
import es.ehubio.db.uniprot.Fetcher;
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
import es.ehubio.dubase.dl.input.Metafile;
import es.ehubio.dubase.dl.input.providers.PhuProteomicsProvider;
import es.ehubio.dubase.dl.input.providers.Provider;
import es.ehubio.dubase.dl.input.providers.UgoManualProvider;
import es.ehubio.dubase.dl.input.providers.UgoProteomicsProvider;

@LocalBean
@Stateless
public class Importer {
	@PersistenceContext
	private EntityManager em;
	@Resource(name="es.ehubio.dubase.inputDir")
	private String inputPath;
	public static final String METADATA = "metadata.xml"; 
	private static final Logger LOG = Logger.getLogger(Importer.class.getName());
	
	@TransactionTimeout(3600)	// 60 minutes ...
	public void saveUgoProteomics(String inputId) throws Exception {
		File expDir = new File(inputPath, inputId);
		File metaFile = new File(expDir, METADATA);		
		Experiment exp = Metafile.load(metaFile);		
		saveExperiment(exp, expDir);
	}	
	
	public int saveUgoCurated(String xlsName) throws Exception {
		File inputFile = new File(inputPath, xlsName);
		Collection<Experiment> exps = UgoManualProvider.loadExperiments(inputFile.getAbsolutePath());
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
		if( exp.getDescription() == null )
			exp.setDescription(paper.getTitle());
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

	private Paper savePublications(Experiment exp) throws Exception {
		Paper first = null;
		for( Publication pub : exp.getPublications() ) {
			Paper paper = PubMed.fillPaper(pub.getPmid());
			PubMed.waitLimit();
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
		String doi = exp.getPublications().get(0).getDoi();
		/*if( doi.equals("10.1038/s41467-018-07185-y") )
			return new LiuUbiquitomicsProvider();*/
		if( doi.equals("10.1016/j.molcel.2020.02.012") )
			return new PhuProteomicsProvider();
		if( exp.getMethodBean().isProteomics() )
			return new UgoProteomicsProvider();
		if( exp.getMethodBean().isManual() )
			return new UgoManualProvider();
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
		try {
			Enzyme enzyme = em
				.createNamedQuery("Enzyme.findByGene", Enzyme.class)
				.setParameter("gene", exp.getEnzymeBean().getGene())
				.getSingleResult();
			exp.setEnzymeBean(enzyme);		
		} catch(NoResultException e) {
			LOG.severe(String.format("DUB '%s' not in DB", exp.getEnzymeBean().getGene()));
			throw e;
		}
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
			saveSubstrate(amb);
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
	
	private void saveSubstrate(Ambiguity amb) {
		Protein prot = amb.getProteinBean();
		if( prot.getAccession() == null )
			try {
				prot.setAccession(em
					.createQuery("SELECT MIN(p.accession) FROM Protein p WHERE p.geneBean.aliases LIKE :gene", String.class)
					.setParameter("gene", "%" + prot.getGeneBean().getName() + "%")
					.getSingleResult());
			} catch (NoResultException eAccession) {
				LOG.warning(String.format("Protein accession not found for gene '%s'", prot.getGeneBean().getName()));
			}
		try {
			prot = em
				.createNamedQuery("Protein.findByAcc", Protein.class)
				.setParameter("acc", prot.getAccession())
				.getSingleResult();
			amb.setProteinBean(prot);
		} catch (NoResultException eProtein) {				
			LOG.warning(String.format("Protein accession '%s' not present in DB", prot.getAccession()));
			if( prot.getName() == null || prot.getDescription() == null ) {
				LOG.info("Downloading information from UniProt ...");
				try {
					Fasta fasta = Fetcher.downloadFasta(prot.getAccession(), SequenceType.PROTEIN);
					prot.setName(fasta.getProteinName());
					prot.setDescription(fasta.getDescription());
				} catch (Exception eFasta) {
					LOG.warning(String.format("Could not download UniProt accession '%s'", prot.getAccession()));
					eFasta.printStackTrace();
				}
			}
			LOG.warning(String.format("Using %s: %s", prot.getName(), prot.getDescription()));
			
			Gene gene = prot.getGeneBean();
			if( gene == null ) {
				gene = new Gene();
				gene.setName(prot.getName());
				prot.setGeneBean(gene);
			}			
			try {
				gene.setName(gene.getName().toUpperCase());
				gene = em
					.createNamedQuery("Gene.findByName", Gene.class)
					.setParameter("name", gene.getName())
					.getSingleResult();
				prot.setGeneBean(gene);
			} catch (Exception eGene) {
				LOG.warning(String.format("Gene name '%s' not present in DB", prot.getGeneBean().getName()));				
				if( gene.getName() == null )
					gene.setName(prot.getAccession());
				if( gene.getAliases() == null )
					gene.setAliases(gene.getName());
				em.persist(gene);
			}
			em.persist(prot);
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
			ev.getAmbiguities().removeIf(amb -> amb.getProteinBean().getAccession() != null && (amb.getProteinBean().getAccession().startsWith("CON_") || amb.getProteinBean().getAccession().startsWith("REV_")) );
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
