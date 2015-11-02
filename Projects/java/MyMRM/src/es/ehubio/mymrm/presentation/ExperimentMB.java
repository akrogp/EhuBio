package es.ehubio.mymrm.presentation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import es.ehubio.io.CsvUtils;
import es.ehubio.mymrm.business.Database;
import es.ehubio.mymrm.business.ExperimentFeed;
import es.ehubio.mymrm.data.Chromatography;
import es.ehubio.mymrm.data.Experiment;
import es.ehubio.mymrm.data.FragmentationType;
import es.ehubio.mymrm.data.Instrument;
import es.ehubio.mymrm.data.IonizationType;
import es.ehubio.panalyzer.Configuration;
import es.ehubio.panalyzer.Configuration.Replicate;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.io.ProteomeDiscovererMsf;
import es.ehubio.proteomics.io.ProteomeDiscovererMsf.PeptideConfidenceLevel;

@ManagedBean
@SessionScoped
public class ExperimentMB implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Experiment entity = new Experiment();
	private String instrument;
	private String ionization;
	private String fragmentation;
	private String chromatography;
	private final Set<String> files = new HashSet<>();
	private Peptide.Confidence peptideConfidence = Peptide.Confidence.DISCRIMINATING;
	private ProteomeDiscovererMsf.PeptideConfidenceLevel msfConfidence = PeptideConfidenceLevel.HIGH;
	private Configuration cfg;
	
	public ExperimentMB() {
		resetConfig();
	}

	public Experiment getEntity() {
		return entity;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public String getChromatography() {
		return chromatography;
	}

	public void setChromatography(String chromatography) {
		this.chromatography = chromatography;
	}

	public String getIonization() {
		return ionization;
	}

	public void setIonization(String ionization) {
		this.ionization = ionization;
	}

	public String getFragmentation() {
		return fragmentation;
	}

	public void setFragmentation(String fragmentation) {
		this.fragmentation = fragmentation;
	}
	
	public void uploadFile( FileUploadEvent event ) {
		try {
			UploadedFile file = event.getFile();
			InputStream is = file.getInputstream();
			OutputStream os = new FileOutputStream(new File(getTmpDir(), file.getFileName()));
			IOUtils.copy(is, os);
			is.close();
			os.close();
			files.add(file.getFileName());
			if( isUsingMsf() )
				getCfg().setPsmScore(ScoreType.SEQUEST_XCORR);
		} catch( Exception e ) {			
		}
	}
	
	public void feed() {
		if( !isReady() )
			return;
		
		Experiment experiment = getEntity();
		experiment.setInstrumentBean(Database.findById(Instrument.class, Integer.parseInt(getInstrument())));
		experiment.setIonizationTypeBean(Database.findById(IonizationType.class, Integer.parseInt(getIonization())));
		experiment.setFragmentationTypeBean(Database.findById(FragmentationType.class, Integer.parseInt(getFragmentation())));
		experiment.setChromatographyBean(Database.findById(Chromatography.class, Integer.parseInt(getChromatography())));
		
		updateConfig();

		ExperimentFeed feed = new ExperimentFeed(experiment, cfg, peptideConfidence);
		resetConfig();
		try {
			Database.feed(feed);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("experimentMB");
	}
	
	private void updateConfig() {
		cfg.setDescription(getEntity().getName());
		cfg.setUseFragmentIons(true);
		cfg.setFilterDecoys(true);
		Replicate replicate = new Replicate();
		replicate.setName("single");
		cfg.getReplicates().add(replicate);
		for( String file : files )
			replicate.getFractions().add(new File(getTmpDir(),file).getAbsolutePath());
		if( isUsingMsf() )
			cfg.setPeptideScoreThreshold(new Score(ScoreType.PEPTIDE_MSF_CONFIDENCE, getMsfConfidence().getLevel()));
		else
			cfg.setPeptideScoreThreshold(null);
		if( cfg.getPsmFdr() != null && cfg.getPsmFdr() <= 0.0 )
			cfg.setPsmFdr(null);
		if( cfg.getPeptideFdr() != null && cfg.getPeptideFdr() <= 0.0 )
			cfg.setPeptideFdr(null);
		if( cfg.getProteinFdr() != null && cfg.getProteinFdr() <= 0.0 )
			cfg.setProteinFdr(null);
		if( cfg.getGroupFdr() != null && cfg.getGroupFdr() <= 0.0 )
			cfg.setGroupFdr(null);
	}
	
	public static String getTmpDir() {
		//return FacesContext.getCurrentInstance().getExternalContext().getInitParameter("MyMRM.fastaDir");
		return System.getProperty("java.io.tmpdir");
	}
	
	public String getFiles() {
		return CsvUtils.getCsv(';', files.toArray());
	}
	
	public boolean isReady() {
		return !files.isEmpty() && entity.getName() != null && !entity.getName().isEmpty(); 
	}

	public Peptide.Confidence getPeptideConfidence() {
		return peptideConfidence;
	}

	public void setPeptideConfidence(Peptide.Confidence peptideConfidence) {
		this.peptideConfidence = peptideConfidence;
	}
	
	public ProteomeDiscovererMsf.PeptideConfidenceLevel getMsfConfidence() {
		return msfConfidence;
	}

	public void setMsfConfidence(ProteomeDiscovererMsf.PeptideConfidenceLevel msfConfidence) {
		this.msfConfidence = msfConfidence;
	}
	
	public boolean isUsingMsf() {
		if( files.isEmpty() )
			//return ScoreType.SEQUEST_XCORR.equals(cfg.getPsmScore());
			return false;
		return files.iterator().next().toLowerCase().contains("msf");
	}

	public Configuration getCfg() {
		return cfg;
	}
	
	private void resetConfig() {
		cfg = new Configuration();
		cfg.initializeFilter();
		
		cfg.setPsmFdr(null);
		cfg.setPeptideFdr(null);
		cfg.setProteinFdr(null);
		cfg.setGroupFdr(null);
	}	
}
