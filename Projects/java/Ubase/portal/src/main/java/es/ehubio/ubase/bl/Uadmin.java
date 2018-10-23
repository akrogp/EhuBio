package es.ehubio.ubase.bl;

import static es.ehubio.ubase.Constants.EXP_PREFIX;
import static es.ehubio.ubase.Constants.META_FILE;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.ubase.Constants;
import es.ehubio.ubase.Locator;
import es.ehubio.ubase.dl.entities.ExpCondition;
import es.ehubio.ubase.dl.entities.Experiment;
import es.ehubio.ubase.dl.entities.Protein;
import es.ehubio.ubase.dl.entities.Replica;
import es.ehubio.ubase.dl.entities.Taxon;
import es.ehubio.ubase.dl.input.Metadata;
import es.ehubio.ubase.dl.input.Metafile;
import es.ehubio.ubase.dl.providers.Dao;
import es.ehubio.ubase.dl.providers.Provider;

@LocalBean
@Stateless
public class Uadmin implements Serializable {
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
		em.persist(exp);
		Map<String, Replica> replicas = persistReplicas(exp, metadata);
		Map<String, Protein> proteins = loadProteins(exp, metadata);
		
		Dao dao = metadata.getProvider().getDao().newInstance();
		dao.persist(em, exp, replicas, proteins, metadata.getData());
		
		File dst = new File(Locator.getConfiguration().getArchivePath(), metadata.getData().getName());
		FileUtils.moveDirectory(metadata.getData(), dst);		
		Metafile.save(metadata, new File(dst, META_FILE));
	}
	
	private Map<String, Protein> loadProteins(Experiment exp, Metadata metadata) throws IOException, InvalidSequenceException {
		List<Fasta> fastas = Fasta.readEntries(new File(metadata.getData(), Constants.FASTA_FILE+".gz").getAbsolutePath(), SequenceType.PROTEIN);
		Map<String, Protein> result = new HashMap<>();
		for( Fasta fasta : fastas ) {
			Protein protein = new Protein();
			protein.setAccession(fasta.getAccession());
			protein.setEntry(fasta.getProteinName());
			if( fasta.getDescription() == null || fasta.getDescription().isEmpty() )
				protein.setName(protein.getEntry());
			else
				protein.setName(fasta.getDescription());
			protein.setGene(fasta.getGeneName());
			protein.setSequence(fasta.getSequence().toUpperCase());
			protein.setExperimentBean(exp);
			result.put(protein.getAccession(), protein);
		}
		return result;
	}

	private Map<String, Replica> persistReplicas(Experiment exp, Metadata metadata) {
		Map<String, Replica> replicas = new HashMap<>();
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
		return replicas;
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
		exp.setTaxon(em.find(Taxon.class, metadata.getOrganism().getId()));
		exp.setInstrument(metadata.getInstrument());
		exp.setSubDate(metadata.getSubDate());
		exp.setExpDate(metadata.getExpDate());
		exp.setPubDate(metadata.getPubDate());
		return exp;
	}
}
