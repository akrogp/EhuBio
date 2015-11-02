package es.ehubio.mymrm.presentation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import es.ehubio.mymrm.business.Database;
import es.ehubio.mymrm.business.ExperimentFeed;
import es.ehubio.mymrm.data.Chromatography;
import es.ehubio.mymrm.data.Experiment;
import es.ehubio.mymrm.data.ExperimentFile;
import es.ehubio.mymrm.data.FastaFile;
import es.ehubio.mymrm.data.Fragment;
import es.ehubio.mymrm.data.FragmentationType;
import es.ehubio.mymrm.data.Instrument;
import es.ehubio.mymrm.data.InstrumentType;
import es.ehubio.mymrm.data.IonizationType;
import es.ehubio.mymrm.data.Peptide;
import es.ehubio.mymrm.data.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.io.ProteomeDiscovererMsf;

@ManagedBean
@ApplicationScoped
public class DatabaseMB {	
	public DatabaseMB() {
		Database.connect();
	}
	
	public List<Instrument> getInstruments() {		
		return Database.findAll(Instrument.class);
	}
	
	public List<Instrument> getInstrumentsNull() {
		List<Instrument> list = new ArrayList<>(getInstruments());
		list.add(null);
		return list;
	}
	
	public void removeInstrument( final Instrument instrument ) {
		Database.remove(Instrument.class, instrument.getId());
	}
	
	public void addInstrument( final InstrumentMB bean ) {				
		Instrument instrument = bean.getEntity();
		instrument.setInstrumentTypeBean(Database.findById(InstrumentType.class, Integer.parseInt(bean.getTypeId())));
		Database.add(instrument);
	}
	
	public List<InstrumentType> getInstrumentTypes() {
		return Database.findAll(InstrumentType.class);
	}
	
	public List<InstrumentType> getInstrumentTypesNull() {
		List<InstrumentType> list = new ArrayList<>(getInstrumentTypes());
		list.add(null);
		return list;
	}
	
	public void removeInstrumentType( InstrumentType type ) {
		Database.remove(InstrumentType.class, type.getId());
	}
	
	public void addInstrumentType( InstrumentTypeMB bean ) {
		Database.add(bean.getEntity());
	}
	
	public List<Chromatography> getChromatographies() {
		return Database.findAll(Chromatography.class);
	}
	
	public List<Chromatography> getChromatograhiesNull() {
		List<Chromatography> list = new ArrayList<>(getChromatographies());
		list.add(null);
		return list;
	}
	
	public void removeChromatography( Chromatography chr ) {
		Database.remove(Chromatography.class, chr.getId());
	}
	
	public void addChromatography( ChromatographyMB bean ) {
		Database.add(bean.getEntity());
	}
	
	public List<ExperimentBean> getExperiments() {
		List<ExperimentBean> list = new ArrayList<>();
		for( Experiment experiment : Database.findExperiments() ) {
			ExperimentBean bean = new ExperimentBean();
			bean.setEntity(experiment);
			list.add(bean);
		}
		for( ExperimentFeed feed : Database.getPendingExperiments() ) {
			ExperimentBean bean = new ExperimentBean();
			bean.setFeed(feed);
			list.add(bean);
		}
		return list;
	}
	
	public void removeExperiment( ExperimentBean exp ) {
		if( exp.getFeed() == null ) {
			Database.removeExperiment(exp.getEntity().getId());
		} else
			try {
				Database.cancelFeed(exp.getFeed());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	public List<FragmentationType> getFragmentationTypes() {
		return Database.findAll(FragmentationType.class);
	}
	
	public List<FragmentationType> getFragmentationTypesNull() {
		List<FragmentationType> list = new ArrayList<>(getFragmentationTypes());
		list.add(null);
		return list;
	}
	
	public void removeFragmentationType( FragmentationType type ) {
		Database.remove(FragmentationType.class, type.getId());
	}
	
	public void addFragmentationType( FragmentationTypeMB bean ) {
		Database.add(bean.getEntity());
	}
	
	public List<IonizationType> getIonizationTypes() {
		return Database.findAll(IonizationType.class);
	}
	
	public List<IonizationType> getIonizationTypesNull() {
		List<IonizationType> list = new ArrayList<>(getIonizationTypes());
		list.add(null);
		return list;
	}
	
	public void removeIonizationType( IonizationType type ) {
		Database.remove(IonizationType.class, type.getId());
	}
	
	public void addIonizationType( IonizationTypeMB bean ) {
		Database.add(bean.getEntity());
	}
	
	public List<FastaFile> getFastas() {
		List<FastaFile> list = new ArrayList<>();
		File dir = new File(getFastaDir());
		for( File file : dir.listFiles() )
			if( file.isFile() && file.getName().contains("fasta") ) {
				FastaFile fasta = new FastaFile();
				fasta.setName(file.getName());
				list.add(fasta);
			}
		return list;
	}
	
	public void removeFasta( FastaFile fasta ) {
		File file = new File(getFastaDir(),fasta.getName());
		file.delete();
	}
	
	public void uploadFasta( FileUploadEvent event ) {
		try {
			UploadedFile file = event.getFile();
			InputStream is = file.getInputstream();
			OutputStream os = new FileOutputStream(new File(getFastaDir(), file.getFileName()));
			IOUtils.copy(is, os);
			is.close();
			os.close();
		} catch( Exception e ) {			
		}
	}
	
	public List<Fragment> getFragments( int idPrecursor ) {
		return Database.findFragments( idPrecursor );
	}
	
	public List<Peptide> search( String pepSequence ) {
		return Database.findPeptides( pepSequence );
	}
	
	public int checkPeptideAvailable( String pepSequence ) {
		return Database.countPeptidesBySequence(pepSequence);
	}
	
	@Override
	protected void finalize() throws Throwable {
		Database.close();
		super.finalize();
	}

	public List<Score> getScores(int evidenceId) {
		return Database.findScores(evidenceId);
	}
	
	public List<ScoreType> getScoreTypes() {
		if( scoreTypes != null )
			return scoreTypes;
		scoreTypes = new ArrayList<>();
		for( ScoreType type : ScoreType.class.getEnumConstants() ) {
			if( type == ScoreType.OTHER_LARGER )
				break;
			scoreTypes.add(type);
		}
		return scoreTypes;
	}
	
	public static String getFastaDir() {
		String dir = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("MyMRM.fastaDir");
		if( dir == null )
			dir = System.getProperty("java.io.tmpdir");
		return dir;
	}
	
	public List<ExperimentFile> findExperimentFiles( int idExperiment ) {
		return Database.findExperimentFiles(idExperiment);
	}
	
	public int countExperimentFiles( int idExperiment ) {
		return Database.countExperimentFiles(idExperiment);
	}
	
	public es.ehubio.proteomics.Peptide.Confidence[] getPeptideConfidences() {
		return es.ehubio.proteomics.Peptide.Confidence.values();
	}
	
	public ProteomeDiscovererMsf.PeptideConfidenceLevel[] getMsfConfidences() {
		return ProteomeDiscovererMsf.PeptideConfidenceLevel.values();
	}
	
	private List<ScoreType> scoreTypes;
}