package es.ehubio.wregex.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.io.Streams;
import es.ehubio.wregex.InputGroup;
import es.ehubio.wregex.PssmBuilder.PssmBuilderException;
import es.ehubio.wregex.Wregex;
import es.ehubio.wregex.Wregex.WregexException;
import es.ehubio.wregex.data.MotifInformation;
import es.ehubio.wregex.data.PresetBean;
import es.ehubio.wregex.data.PtmProvider;
import es.ehubio.wregex.data.ResultComparator;
import es.ehubio.wregex.data.ResultEx;
import es.ehubio.wregex.data.ResultGroupEx;
import es.ehubio.wregex.data.Services;

@Named
@SessionScoped
public class SearchView implements Serializable {
	private static final long serialVersionUID = 1L;		
	private String searchError;
	private List<ResultEx> results = null;
	private String cachedAlnPath;
	private boolean assayScores = false;
	private boolean motifProbs = false;
	@Inject
	private DatabasesBean databases;
	@Inject
	private MotifView motifView;
	@Inject
	private TargetView targetView;
	@Inject
	private SearchOptionsView options;
	private final Services services;
	private String preset;
	
	public SearchView() {
		services = new Services(FacesContext.getCurrentInstance().getExternalContext());
	}
	
	public void resetResult() {
		preset = "none";
		searchError = null;
		results = null;
	}

	public String getConfigError() {
		String error = checkConfigError();
		if( error != null )
			results = null;
		return error;
	}
	
	private String checkConfigError() {
		String error = motifView.checkConfigError();
		if( error != null )
			return error;
		error = targetView.checkConfigError();
		if( error != null )
			return error;		
		if( motifView.isMultiMotif() && targetView.getInputGroups().size() > services.getInitNumber("wregex.allMotifs") )
			return String.format("Sorry, when searching for all motifs the number of target sequences is limited to %d", services.getInitNumber("wregex.allMotifs"));
		return null;
	}
	
	public void search() {
		resetResult();		
		try {
			updateAssayScores();
			List<ResultGroupEx> resultGroups = motifView.isMultiMotif() ?  allSearch() : singleSearch();
			results = Services.expand(resultGroups, options.isGrouping());			
			if( motifView.isUseAuxMotif() )
				searchAux();
			if( options.isCosmic() )
				searchCosmic();
			if( options.isPtm() )
				searchPtm();
			results = Services.filter(results, options.isFilterEqual(), options.isFilterNoConn(), options.getSelectedPtms(), options.getScoreThreshold());
			Services.flanking(results, options.getFlanking());
			Services.addFeatures(databases.getMapUniprot(), results);
			Collections.sort(results, new ResultComparator(options.getSelectedPtms()));
			updateMotifProbs();
		} catch( IOException e ) {
			searchError = "File error: " + e.getMessage();
		} catch( PssmBuilderException e ) {
			searchError = "PSSM not valid: " + e.getMessage();
		} catch( WregexException e ) {
			searchError = "Invalid configuration: " + e.getMessage();
		} catch( Exception e ) {
			e.printStackTrace();
			searchError = e.getMessage();
		}
	}	
	
	private List<ResultGroupEx> singleSearch() throws NumberFormatException, Exception {
		initPssm();		
		Wregex wregex = new Wregex(motifView.getMainMotif().getSingleRegex(), motifView.getMainMotif().getPssm());
		return Services.search(wregex, motifView.getMainMotif().getMotifInformation(), motifView.getMainMotif().getMotifDefinition(), targetView.getInputGroups(), assayScores, services.getInitNumber("wregex.watchdogtimer")*1000);
	}
	
	private void initPssm() throws IOException, PssmBuilderException {
		MotifBean motif = this.motifView.getMainMotif();
		if( !motif.isCustom() && motif.getPssmFile() != null )
			motif.setPssm(services.getPssm(motif.getPssmFile()));		
	}	

	private List<ResultGroupEx> allSearch() throws Exception {
		assayScores = false;
		//long div = getWregexMotifs().size() + getElmMotifs().size();
		//long tout = getInitNumber("wregex.watchdogtimer")*1000/div;
		long tout = services.getInitNumber("wregex.watchdogtimer")*1000;
		List<MotifInformation> motifs = null;
		switch( motifView.getMultiType() ) {
			case ALL:
				motifs = databases.getAllMotifs();
				break;
			case ELM:
				motifs = databases.getElmMotifs();
				break;
			case ELM_HUMAN:
				motifs = databases.getElmHumanMotifs();
				break;
			default:
				throw new Exception("MultiMotif type not supported");
		}
		List<ResultGroupEx> results = services.searchAll(motifs, targetView.getInputGroups(), tout);
		return results;
	}	

	private void searchCosmic() {
		Services.searchCosmic(databases.getMapCosmic(), results, motifView.isUsingPssm());
	}
	
	private void searchPtm() {
		if( options.isDbPtm() )
			Services.searchPtm(PtmProvider.DBPTM, databases.getMapDbPtm(), results);
		else if( options.isPsp() )
			Services.searchPtm(PtmProvider.PSP, databases.getMapPsp(), results);
	}
	
	private void searchAux() throws Exception {
		MotifBean motif = this.motifView.getAuxMotif();
		if( motif.getPssmFile() != null )
			motif.setPssm(services.getPssm(motif.getPssmFile()));
		Wregex wregex = new Wregex(motif.getRegex(), motif.getPssm());		
		Services.searchAux(wregex, motif.getMotifInformation(), motif.getMotifDefinition(), results);
	}
	
	private void updateAssayScores() {
		assayScores = true;
		for( InputGroup inputGroup : targetView.getInputGroups() )
			if( !inputGroup.hasScores() ) {
				assayScores = false;
				break;
			}
	}
	
	private void updateMotifProbs() {
		motifProbs = false;
		for( ResultEx result : getResults() )
			if( result.getMotifProb() != null ) {
				motifProbs = true;
				break;
			}
	}

	public String getSearchError() {		
		return searchError;
	}

	public List<? extends ResultEx> getResults() {
		if( searchError == null )			
			return results;
		return null;
	}
	
	public String getNumberOfResults() {
		int count = 0;
		if( results != null )
			count = results.size();
		if( count == 0 )
			return "No matches (if a match was expected, try relaxing the regular expression)";
		if( count == 1 )
			return "1 result!";
		return count + " results!";
	}

	public void downloadCsv() {
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();
	    ec.responseReset();
	    ec.setResponseContentType("text/csv"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(length);
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\""+targetView.getBaseFileName()+".csv\"");
		try {
			OutputStream output = ec.getResponseOutputStream();
			ResultEx.saveCsv(new OutputStreamWriter(output), results, getAssayScores(), isMotifProbs() ,motifView.isUseAuxMotif(), options.isCosmic(), options.isPtm(), options.getSelectedPtms() );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		fc.responseComplete();
	}
	
	public void downloadAln() {
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();
	    ec.responseReset();
	    ec.setResponseContentType("text"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(length);
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\""+targetView.getBaseFileName()+".aln\"");
		try {
			OutputStream output = ec.getResponseOutputStream();
			if( cachedAlnPath != null )
				try(InputStream input = Streams.getBinReader(cachedAlnPath) ) {
					IOUtils.copy(input,output);
				}
			else
				ResultEx.saveAln(new OutputStreamWriter(output), results);			
		} catch( Exception e ) {
			e.printStackTrace();
		}
		fc.responseComplete();
	}
	
	public void downloadResultsFasta() {
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();
	    ec.responseReset();
	    ec.setResponseContentType("text"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(length);
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\""+targetView.getBaseFileName()+"-results.fasta\"");
		try {
			OutputStream output = ec.getResponseOutputStream();
			ResultEx.saveFasta(new OutputStreamWriter(output), results);
		} catch( Exception e ) {
			e.printStackTrace();
		}
		fc.responseComplete();
	}
	
	private void downloadFile( String name ) {
		if( name == null )
			return;
		
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();
	    ec.responseReset();
	    ec.setResponseContentType("text"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(length);
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\""+name+"\"");
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(ec.getResourceAsStream("/resources/data/"+name)));
			PrintWriter wr = new PrintWriter(ec.getResponseOutputStream());
			String str;
			while( (str = rd.readLine()) != null )
				wr.println(str);
			rd.close();
			wr.flush();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		fc.responseComplete();
	}
	
	public void downloadPssm() {
		downloadFile(motifView.getMainMotif().getPssmFile());
	}
	
	public void downloadAuxPssm() {
		downloadFile(motifView.getAuxMotif().getPssmFile());
	}
	
	public void downloadFasta() {
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();
	    ec.responseReset();
	    ec.setResponseContentType("text");
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\""+targetView.getBaseFileName()+".fasta\"");
		try {
			OutputStream output = ec.getResponseOutputStream();
			Fasta.writeEntries(new OutputStreamWriter(output), targetView.getInputGroups().stream().map(inputGroup -> inputGroup.getFasta()).collect(Collectors.toList()));
		} catch( Exception e ) {
			e.printStackTrace();
		}
		fc.responseComplete();
	}

	public boolean getAssayScores() {
		return assayScores;
	}
	
	public boolean isMotifProbs() {
		return motifProbs;
	}
	
	public void onChangeOption() {
		resetResult();
	}

	public String getPreset() {
		return preset;
	}

	public void setPreset(String preset) {
		this.preset = preset;
	}
	
	public void onSelectPreset(ValueChangeEvent event) {		
		String value = event.getNewValue().toString();
		if( value != null && value.equals(preset) )
			return;
		resetResult();
		PresetBean preset = string2preset(value);
		if( preset == null )
			return;
		
		value = presetValue(preset.getMainMotif()); 
		motifView.getMainMotif().setMotif(value);
		event = new ValueChangeEvent(event.getComponent(), null, value);
		options.setScoreThreshold(preset.getThreshold());
		options.setFlanking(preset.getFlanking());
		motifView.onChangeMainMotif(event);
		
		value = presetValue(preset.getAuxMotif());
		motifView.getAuxMotif().setMotif(preset.getAuxMotif());
		event = new ValueChangeEvent(event.getComponent(), null, value);
		motifView.onChangeAuxMotif(event);
		
		value = presetValue(preset.getTarget());
		targetView.setTarget(value);
		event = new ValueChangeEvent(event.getComponent(), null, value);
		targetView.onChangeTarget(event);
		if( preset.getTargetInput() != null ) {
			targetView.setInputText(preset.getTargetInput());
			targetView.onChangeInput();
		}
		
		options.setGrouping(preset.isGrouping());
		options.setFilterEqual(preset.isFilterSimilar());
		options.setCosmic(preset.isCosmic());
		options.setDbPtm(preset.isDbPtm());
		options.setPsp(preset.isPsp());
		if( preset.getPtms() != null )			
			options.setSelectedPtms(preset.getPtms().toArray(new String[0]));
		else
			options.setSelectedPtms(new String[0]);
		onChangeOption();
		
		search();
	}

	private String presetValue(String value) {
		return value == null ? "none" : value;
	}

	private PresetBean string2preset(String value) {
		for( PresetBean preset : databases.getPresets() )
			if( preset.getValue().equals(value) )
				return preset;
		return null;
	}
}