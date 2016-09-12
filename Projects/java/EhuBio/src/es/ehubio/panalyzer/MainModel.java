package es.ehubio.panalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import es.ehubio.panalyzer.html.HtmlReport;
import es.ehubio.proteomics.DecoyBase;
import es.ehubio.proteomics.MsExperiment;
import es.ehubio.proteomics.MsExperiment.Replicate;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.io.EhubioCsv;
import es.ehubio.proteomics.io.MsMsFile;
import es.ehubio.proteomics.io.Mzid;
import es.ehubio.proteomics.pipeline.DecoyMatcher;
import es.ehubio.proteomics.pipeline.FdrCalculator;
import es.ehubio.proteomics.pipeline.FdrCalculator.FdrResult;
import es.ehubio.proteomics.pipeline.Filter;
import es.ehubio.proteomics.pipeline.PAnalyzer;
import es.ehubio.proteomics.pipeline.RandomMatcher;
import es.ehubio.proteomics.pipeline.ScoreIntegrator;

public class MainModel {
	public enum State { WORKING, INIT, CONFIGURED, LOADED, RESULTS, SAVED}
	public static final String NAME = "PAnalyzer";
	public static final String VERSION = "v2.0-alpha8";
	public static final String SIGNATURE = String.format("%s (%s)", NAME, VERSION);
	public static final String URL = "https://code.google.com/p/ehu-bio/wiki/PAnalyzer";

	private static final Logger logger = Logger.getLogger(MainModel.class.getName());
	private static final String STATE_ERR_MSG="This method should not be called in the current state";
	private static final int MAXITER=15;
	private String status;
	private String progressMessage="";
	private int progressPercent = 0;
	private MsExperiment experiment;
	private MsMsFile file;
	private Configuration config;
	private State state;
	private Set<ScoreType> psmScoreTypes, peptideScoreTypes, proteinScoreTypes;
	private File reportFile = null;
	private final FdrCalculator fdrCalc = new FdrCalculator();
	
	public MainModel() {
		resetTotal();	
	}
	
	public void run() throws Exception {
		resetData();
		loadData();
		filterData();
		saveData();
	}
	
	public void run( String pax ) throws Exception {
		resetTotal();
		loadConfig(pax);
		run();
	}
	
	private void resetData() {
		experiment = null;
		psmScoreTypes = null;
		setState(State.CONFIGURED, "Experiment configured, you can now load the data");
	}

	private void resetTotal() {
		resetData();
		config = null;		
		setState(State.INIT, "Load experiment data");
	}
	
	public void reset() {
		resetTotal();
		logger.info("--- Started a new analysis ---");
	}
	
	public State getState() {
		return state;
	}

	public Configuration getConfig() {
		return config;
	}
	
	public void setConfig( Configuration config ) {
		if( config == null ) {
			resetTotal();
			return;
		}
		resetData();
		this.config = config;
	}
	
	public void loadConfig( String path ) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Configuration.class);
		Unmarshaller um = context.createUnmarshaller();
		Configuration config = (Configuration)um.unmarshal(new File(path));		
		logger.info(String.format("Using config from '%s': %s", path, config.getDescription()));
		setConfig(config);
	}
	
	public void loadData() throws Exception {
		assertState(state==State.CONFIGURED);
		try {
			experiment = new MsExperiment();
			int step = 0, steps = 0;
			for( es.ehubio.panalyzer.Configuration.Replicate replicate : config.getReplicates() )
				steps += replicate.getFractions().size();
			for( es.ehubio.panalyzer.Configuration.Replicate replicate : config.getReplicates() ) {
				logger.info(String.format("Loading replicate (%s) ...", replicate.getName()));
				Replicate rep = new Replicate(replicate.getName());
				for( String fraction : replicate.getFractions() ) {
					setProgress(step++, steps, String.format("Loading %s (%s) ...", new File(fraction).getName(), rep.getName()));
					file = MsMsFile.autoDetect(fraction);
					if( file == null )
						throw new Exception(String.format("File format not supported: %s", fraction));
					MsMsData data = file.load(fraction,Boolean.TRUE.equals(config.getUseFragmentIons())).markDecoys(config.getDecoyRegex());
					rep.mergeFraction(data);
					logCounts("Merged",rep.getData());
				}
				experiment.getReplicates().add(rep);
				rebuildGroups(rep.getName(), rep.getData());
			}
			finishProgress(State.LOADED, "Data loaded, you can now apply a filter");
		} catch( Exception e ) {
			resetData();
			handleException(e, "Error loading data, correct your configuration");
		}
	}
	
	private Set<ScoreType> getScoreTypes(Collection<? extends DecoyBase> items) {
		assertState(state.ordinal()>=State.LOADED.ordinal());
		Set<ScoreType> scoreTypes = new HashSet<>();
		for( DecoyBase item : items ) {
			if( item.getScores().isEmpty() )
				continue;
			for( Score score : item.getScores() )
				scoreTypes.add(score.getType());
			break;
		}
		return scoreTypes;
	}
	
	public Set<ScoreType> getPsmScoreTypes() {
		if( psmScoreTypes == null )
			psmScoreTypes = getScoreTypes(experiment.getReplicates().get(0).getData().getPsms());
		return psmScoreTypes;
	}
	
	public Set<ScoreType> getPeptideScoreTypes() {
		if( peptideScoreTypes == null )
			peptideScoreTypes = getScoreTypes(experiment.getReplicates().get(0).getData().getPeptides());
		return peptideScoreTypes;
	}
	
	public Set<ScoreType> getProteinScoreTypes() {
		if( proteinScoreTypes == null )
			proteinScoreTypes = getScoreTypes(experiment.getReplicates().get(0).getData().getProteins());
		return proteinScoreTypes;
	}
	
	public void filterData() throws Exception {
		assertState(state == State.LOADED || state == State.RESULTS);
		try {
			int step = 0, steps = 5*experiment.getReplicates().size()+2;
			for( Replicate replicate : experiment.getReplicates() ) {
				logger.info(String.format("Filtering replicate (%s) ...", replicate.getName()));
				/*setProgress(step++, steps, String.format("Updating PSM ranks (%s) ...",replicate.getName()));
				replicate.getData().updateRanks(config.getPsmScore());*/
				setProgress(step++, steps, String.format("Applying input filter (%s) ...",replicate.getName()));
				inputFilter(replicate.getData());
				setProgress(step++, steps, String.format("Applying PSM FDR filter (%s) ...",replicate.getName()));
				processPsmFdr(replicate.getData());
				setProgress(step++, steps, String.format("Applying peptide FDR filter (%s) ...",replicate.getName()));
				processPeptideFdr(replicate.getData());
				setProgress(step++, steps, String.format("Applying protein FDR filter (%s) ...",replicate.getName()));
				processProteinFdr(replicate.getData());
				setProgress(step++, steps, String.format("Applying protein group FDR filter (%s) ...",replicate.getName()));
				processGroupFdr(replicate.getData());
				logFdrs(replicate.getData());
				logCounts(String.format("Counts (%s)",replicate.getName()), replicate.getData());
				logger.info(getCounts(replicate.getData()).toString());
			}
			setProgress(step++, steps, "Merging replicates ...");
			experiment.merge();
			setProgress(step++, steps, "Applying minimum replicate number filter ...");
			replicateFilter();
			logFdrs(experiment.getData());
			logCounts("Final counts",experiment.getData());
			logger.info(getCounts(experiment.getData()).toString());
			finishProgress(State.RESULTS, "Data filtered, you can now save the results");
		} catch( Exception e ) {
			resetData();
			handleException(e, "Error filtering data, correct your configuration");
		}
	}
	
	public File saveData() throws Exception {
		reportFile = null;
		assertState(state == State.RESULTS);
		try {
			if( Boolean.TRUE.equals(config.getFilterDecoys()) ) {
				Filter filter = new Filter(experiment.getData());
				filter.setFilterDecoyPeptides(true);
				filterAndGroup(filter,"Decoy removal");
			}
			if( config.getOutput() == null || config.getOutput().isEmpty() )
				return null;
			int step = 0;
			int steps = 3;
			File dir = new File(config.getOutput());
			dir.mkdir();
			if( config.getReplicates().size() == 1 && config.getReplicates().get(0).getFractions().size() == 1 && file instanceof Mzid ) {
				steps++;
				setProgress(step++, steps, "Saving in mzid format ...");
				file.save(config.getOutput());
			}
			setProgress(step++, steps, "Saving csv files ...");
			EhubioCsv csv = new EhubioCsv(experiment.getData());
			csv.setPsmScoreType(config.getPsmScore());
			csv.save(config.getOutput());
			setProgress(step++, steps, "Saving configuration ...");
			saveConfiguration();
			setProgress(step++, steps, "Saving html report ...");
			reportFile = generateHtml();
			finishProgress(State.SAVED, "Data saved, you can now browse the results");
		} catch( Exception e ) {
			state = State.RESULTS;
			handleException(e, "Error saving data, correct your configuration");			
		}
		return reportFile;
	}	

	public MsMsData getData() {
		return experiment.getData();
	}
	
	public String getStatus() {
		return status;
	}
	
	public PAnalyzer.Counts getCounts() {
		return getCounts(experiment.getData());
	}
	
	private PAnalyzer.Counts getCounts(MsMsData data) {
		return new PAnalyzer(data).getCounts();
	}
	
	public PAnalyzer.Counts getTargetCounts() {
		return new PAnalyzer(experiment.getData()).getTargetCounts();
	}
	
	public PAnalyzer.Counts getDecoyCounts() {
		return new PAnalyzer(experiment.getData()).getDecoyCounts();
	}
	
	public List<CountReport> getCountReport() {
		List<CountReport> list = new ArrayList<>();
		PAnalyzer.Counts target = getTargetCounts();
		PAnalyzer.Counts decoy = getDecoyCounts();
		list.add(new CountReport("Minimum proteins (grouped)",target.getMinimum(),decoy.getMinimum()));
		list.add(new CountReport("Maximum proteins (un-grouped)",target.getMaximum(),decoy.getMaximum()));
		list.add(new CountReport("Conclusive proteins",target.getConclusive(),decoy.getConclusive()));
		list.add(new CountReport("Indistinguishable proteins (grouped)",target.getIndistinguishableGroups(),decoy.getIndistinguishableGroups()));
		list.add(new CountReport("Indistinguishable proteins (un-grouped)",target.getIndistinguishable(),decoy.getIndistinguishable()));
		list.add(new CountReport("Ambigous proteins (grouped)",target.getAmbiguousGroups(),decoy.getAmbiguousGroups()));
		list.add(new CountReport("Ambigous proteins (un-grouped)",target.getAmbiguous(),decoy.getAmbiguous()));
		list.add(new CountReport("Non-conclusive proteins",target.getNonConclusive(),decoy.getNonConclusive()));
		list.add(new CountReport("Total peptides",target.getAmbiguityParts(),decoy.getAmbiguityParts()));
		list.add(new CountReport("Unique peptides",target.getUnique(),decoy.getUnique()));
		list.add(new CountReport("Discriminating peptides",target.getDiscriminating(),decoy.getDiscriminating()));
		list.add(new CountReport("Non-discriminating peptides",target.getNonDiscriminating(),decoy.getNonDiscriminating()));
		list.add(new CountReport("Total PSMs",target.getPsms(),decoy.getPsms()));
		return list;
	}
	
	public FdrResult getPsmFdr() {
		return fdrCalc.getGlobalFdr(getData().getPsms());
	}
	
	public FdrResult getPeptideFdr() {
		return fdrCalc.getGlobalFdr(getData().getPeptides());
	}
	
	public FdrResult getProteinFdr() {
		return fdrCalc.getGlobalFdr(getData().getProteins());
	}
	
	public FdrResult getGroupFdr() {
		return fdrCalc.getGlobalFdr(getData().getGroups());
	}
	
	public List<FdrReport> getFdrReport() {
		List<FdrReport> list = new ArrayList<>();
		list.add(new FdrReport("Protein group", getGroupFdr().getRatio(), config.getGroupFdr()));
		list.add(new FdrReport("Protein", getProteinFdr().getRatio(), config.getProteinFdr()));
		list.add(new FdrReport("Peptide", getPeptideFdr().getRatio(), config.getPeptideFdr()));
		list.add(new FdrReport("PSM", getPsmFdr().getRatio(), config.getPsmFdr()));
		return list;
	}
	
	private void saveConfiguration() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Configuration.class);
		Marshaller marshaller = context.createMarshaller();
		File pax = new File(getConfig().getOutput(),"config.pax");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(getConfig(), pax);
		logger.info(String.format("Config saved in '%s'", pax.getName()));
	}
	
	private File generateHtml() throws IOException {
		HtmlReport html = new HtmlReport(this);
		html.create();
		logger.info(String.format("HTML report available in '%s'", html.getHtmlFile().getName()));
		return html.getHtmlFile();
	}
	
	private void logFdrs( MsMsData data ) {
		fdrCalc.logFdrs(data);
	}
	
	private void logCounts( String title, MsMsData data ) {
		logger.info(String.format("%s: %s", title, data.toString()));
	}
	
	private void assertState( boolean ok ) {
		if( !ok )
			throw new AssertionError(String.format("%s (%s)",STATE_ERR_MSG,state.toString()));
	}
	
	private void handleException( Exception e, String msg ) throws Exception {
		e.printStackTrace();
		status = msg;
		logger.severe(String.format("%s: %s", msg, e.getMessage()));
		throw e;
	}
	
	private void rebuildGroups(String title, MsMsData data) {
		//logger.info("Updating protein groups ...");
		PAnalyzer pAnalyzer = new PAnalyzer(data);
		pAnalyzer.run();
		logger.info(String.format("%s: %s", title, pAnalyzer.getCounts().toString()));
	}
	
	private void filterAndGroup( Filter filter, String title ) {
		filter.run();
		logCounts(title,filter.getData());
		//filter.getData().checkIntegrity();
		rebuildGroups("Re-grouped", filter.getData());
	}
	
	private void inputFilter(MsMsData data) {
		logFdrs(data);
		Filter filter = new Filter(data);
		filter.setRankTreshold(config.getPsmRankThreshold()==null?0:config.getPsmRankThreshold());
		filter.setOnlyBestPsmPerPrecursor(Boolean.TRUE.equals(config.getBestPsmPerPrecursor())?config.getPsmScore():null);
		filter.setOnlyBestPsmPerPeptide(Boolean.TRUE.equals(config.getBestPsmPerPeptide())?config.getPsmScore():null);
		filter.setPsmScoreThreshold(config.getPsmScoreThreshold());
		filter.setMinPeptideLength(config.getMinPeptideLength()==null?0:config.getMinPeptideLength());
		filter.setUniquePeptides(Boolean.TRUE.equals(config.getUniquePeptides()));
		filter.setFilterDecoyPeptides(false);
		filter.setPeptideScoreThreshold(config.getPeptideScoreThreshold());
		filter.setProteinScoreThreshold(config.getProteinScoreThreshold());
		filter.setGroupScoreThreshold(config.getGroupScoreThreshold());
		filterAndGroup(filter,"Input filter");
		//validator.logFdrs();
	}
	
	private void replicateFilter() {
		if( experiment.getReplicates().size() <= 1 )
			return;
		
		Filter filter = new Filter(experiment.getData());
		boolean run = false;
		if( config.getMinPeptideReplicates() != null ) {
			run = true;
			filter.setMinPeptideReplicates(config.getMinPeptideReplicates());
		}
		if( config.getMinProteinReplicates() != null ) {
			run = true;
			filter.setMinProteinReplicates(config.getMinPeptideReplicates());
		}
		if( run )
			filterAndGroup(filter, "Replicate number filter");
		else
			rebuildGroups("Re-grouped", experiment.getData());
	}
	
	private void processPsmFdr(MsMsData data) {		
		if( config.getPsmFdr() != null || config.getPeptideFdr() != null || config.getProteinFdr() != null || config.getGroupFdr() != null ) {
			fdrCalc.updatePsmScores(data.getPsms(), config.getPsmScore(), true);
			ScoreIntegrator.updatePsmScores(data.getPsms());
		}
		if( config.getPsmFdr() == null )
			return;
		Filter filter = new Filter(data);
		filter.setPsmScoreThreshold(new Score(ScoreType.PSM_Q_VALUE, config.getPsmFdr()));
		filterAndGroup(filter,String.format("PSM FDR=%s filter",config.getPsmFdr()));
	}
	
	private void processPeptideFdr(MsMsData data) {
		if( config.getPeptideFdr() != null || config.getProteinFdr() != null || config.getGroupFdr() != null ) {			
			ScoreIntegrator.psmToPeptide(data.getPeptides());
			fdrCalc.updatePeptideScores(data.getPeptides(), ScoreType.LPP_SCORE, false);
		}
		if( config.getPeptideFdr() == null )
			return;
		Filter filter = new Filter(data);
		filter.setPeptideScoreThreshold(new Score(ScoreType.PEPTIDE_Q_VALUE, config.getPeptideFdr()));
		filterAndGroup(filter,String.format("Peptide FDR=%s filter",config.getPeptideFdr()));
	}
	
	private void processProteinFdr(MsMsData data) {
		if( config.getProteinFdr() == null )
			return;
		//ScoreIntegrator.updateProteinScores(data.getProteins());
		//ScoreIntegrator.updateProteinScoresPrefix(data.getProteins(),config.getDecoyRegex());
		ScoreIntegrator.peptideToProteinEquitative(data.getProteins());
		RandomMatcher random = new DecoyMatcher(data.getProteins(), config.getDecoyRegex());
		ScoreIntegrator.setExpectedValues(data.getProteins(), random);
		ScoreIntegrator.divideRandom(data.getProteins(), true);
		fdrCalc.updateProteinScores(data.getProteins(), ScoreType.LPQCORR_SCORE, false);
		Filter filter = new Filter(data);
		filter.setProteinScoreThreshold(new Score(ScoreType.PROTEIN_Q_VALUE, config.getProteinFdr()));
		filterAndGroup(filter,String.format("Protein FDR=%s filter",config.getProteinFdr()));
	}
	
	private void processGroupFdr(MsMsData data) {
		if( config.getGroupFdr() == null )
			return;
		
		ScoreIntegrator.peptideToProteinEquitative(data.getProteins());
		RandomMatcher random = new DecoyMatcher(data.getProteins(), config.getDecoyRegex());
		ScoreIntegrator.setExpectedValues(data.getProteins(), random);
		ScoreIntegrator.divideRandom(data.getProteins(), true);
		
		PAnalyzer pAnalyzer = new PAnalyzer(data);		
		PAnalyzer.Counts curCount = pAnalyzer.getCounts(), prevCount;
		int i = 0;
		Filter filter = new Filter(data);
		filter.setGroupScoreThreshold(new Score(ScoreType.GROUP_Q_VALUE, config.getGroupFdr()));
		do {
			i++;
			ScoreIntegrator.proteinToGroup(data.getGroups());
			fdrCalc.updateGroupScores(data.getGroups(), ScoreType.LPG_SCORE, false);
			filterAndGroup(filter,String.format("Group FDR=%s filter, iteration %s",config.getGroupFdr(),i));
			prevCount = curCount;
			curCount = pAnalyzer.getCounts();
		} while( !curCount.equals(prevCount) && i < MAXITER );
		if( i >= MAXITER )
			logger.warning("Maximum number of iterations reached!");
		
		ScoreIntegrator.proteinToGroup(data.getGroups());
		fdrCalc.updateGroupScores(data.getGroups(), ScoreType.LPG_SCORE, false);
	}

	public String getProgressMessage() {
		return progressMessage;
	}

	public int getProgressPercent() {
		return progressPercent;
	}
	
	private void setState( State state, String msg ) {
		this.state = state;
		status = msg;
	}
	
	private void setProgress( int step, int steps, String msg ) {
		setState(State.WORKING, "Working ...");
		progressPercent = (int)Math.round(step*100.0/steps);
		progressMessage = msg;
		logger.info(String.format("%s (%d%%)", progressMessage, progressPercent));
	}
	
	private void finishProgress( State state, String msg ) {
		setState(state, msg);
		progressPercent = 100;
		progressMessage = status;
		logger.info(String.format("Finished! (state=%s)",this.state.toString()));
	}

	public File getReportFile() {
		return reportFile;
	}
	
	public static class CountReport {		
		private final String title;
		private final int target;
		private final int decoy;
		private final int total;
		
		public CountReport(String title, int target, int decoy) {
			this.title = title;
			this.target = target;
			this.decoy = decoy;
			total = target+decoy;
		}		
		public String getTitle() {
			return title;
		}
		public int getTarget() {
			return target;
		}
		public int getDecoy() {
			return decoy;
		}
		public int getTotal() {
			return total;
		}
	}
	
	public static class FdrReport {
		private final String title;
		private final String value;
		private final String threshold;
		
		public FdrReport( String title, double value, Double threshold ) {
			this.title = title;
			this.value = String.format("%.5f",value);
			this.threshold = threshold == null ? "" : String.format("%.5f",threshold);
		}		
		public String getTitle() {
			return title;
		}
		public String getValue() {
			return value;
		}
		public String getThreshold() {
			return threshold;
		}
	}
}