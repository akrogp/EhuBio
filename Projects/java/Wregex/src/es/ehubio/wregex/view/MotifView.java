package es.ehubio.wregex.view;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import es.ehubio.wregex.Pssm;
import es.ehubio.wregex.PssmBuilder.PssmBuilderException;
import es.ehubio.wregex.data.MotifDefinition;
import es.ehubio.wregex.data.MotifInformation;

@Named
@SessionScoped
public class MotifView implements Serializable {
	private static final long serialVersionUID = 1L;
	private final MotifBean mainMotif = new MotifBean();
	private final MotifBean auxMotif = new MotifBean();	
	private boolean allMotifs = false;
	private boolean allElmMotifs = false;	
	@Inject
	private DatabasesBean databases;
	@Inject
	private SearchView searchBean;
	private String motifError;

	
	public MotifBean getMainMotif() {
		return mainMotif;
	}
	
	public boolean isUseAuxMotif() {
		return auxMotif.getMotifInformation() != null;
	}
	
	public MotifBean getAuxMotif() {
		return auxMotif;
	}
	
	public boolean isAllMotifs() {
		return allMotifs;
	}
	
	public void setAllMotifs(boolean allMotifs) {
		this.allMotifs = allMotifs;
	}
	
	public boolean isAllElmMotifs() {
		return allElmMotifs;
	}
	
	public void setAllElmMotifs(boolean allElmMotifs) {
		this.allElmMotifs = allElmMotifs;
	}
	
	public void onChangeMainMotif( ValueChangeEvent event ) {
		motifError = null;
		onChangeMotif(event, mainMotif);
	}
	
	public void onChangeAuxMotif( ValueChangeEvent event ) {
		onChangeMotif(event, auxMotif);
	}
	
	public void onChangeMainDefinition( ValueChangeEvent event ) {
		onChangeDefinition(event, mainMotif);
	}
	
	public void onChangeAuxDefinition( ValueChangeEvent event ) {
		onChangeDefinition(event, auxMotif);
	}	
	
	public String checkConfigError() {
		if( mainMotif.isCustom() ) {
			if( mainMotif.getCustomRegex() == null || mainMotif.getCustomRegex().isEmpty() )
				return "A regular expression must be defined";
			/*if( Wregex.countCapturingGroups(customRegex) > 0 && customPssm == null )
				return "A PSSM must be provided when using regex groups";*/
		} else if( !allMotifs && !allElmMotifs) {
			if( mainMotif.getMotifInformation() == null )
				return "A motif must be selected";
			if( mainMotif.getMotifDefinition() == null )
				return "A configuration must be selected for motif " + mainMotif.getMotif();
		}
		if( isUseAuxMotif() ) {
			if( auxMotif.getMotifInformation() == null )
				return "An aux motif must be selected";
			if( auxMotif.getMotifDefinition() == null )
				return "A configuration must be selected for aux motif " + auxMotif.getMotif();
		}
		return motifError;
	}	
	
	private void onChangeMotif( ValueChangeEvent event, MotifBean motif ) {
		Object value = event.getNewValue();
		motif.setCustom(false);
		allMotifs = false;
		allElmMotifs = false;
		motif.setMotifInformation(null);
		if( value != null ) {			
			if( value.toString().equals("Custom") )
				motif.setCustom(true);
			else if( value.toString().equals("All") )
				allMotifs = true;
			else if( value.toString().equals("AllELM") )
				allElmMotifs = true;
			else
				motif.setMotifInformation(stringToMotif(event.getNewValue()));
		}
		searchBean.resetResult();
	}
	
	private MotifInformation stringToMotif( Object object ) {
		if( object == null )
			return null;
		String name = object.toString();
		for( MotifInformation motif : databases.getWregexMotifs() )
			if( motif.getName().equals(name) )
				return motif;
		for( MotifInformation motif : databases.getElmMotifs() )
			if( motif.getName().equals(name) )
				return motif;
		return null;
	}
	
	private void onChangeDefinition( ValueChangeEvent event, MotifBean motif ) {
		motif.setMotifDefinition(stringToDefinition(event.getNewValue(), motif.getDefinitions()));
		searchBean.resetResult();
	}
	
	private MotifDefinition stringToDefinition( Object object, List<MotifDefinition> defs ) {
		if( object == null )
			return null;
		String name = object.toString();
		for( MotifDefinition def : defs )
			if( def.getName().equals(name) )
				return def;
		return null;
	}
	
	public boolean isUsingPssm() {
		return !isAllElmMotifs() || isAllMotifs() || mainMotif.getPssm() != null;
	}
	
	public boolean isShowMotifDetails() {
		return mainMotif.getMotifInformation() != null || (isUseAuxMotif() && auxMotif.getMotifInformation() != null);
	}
	
	public void uploadPssm( FileUploadEvent event ){
		searchBean.resetResult();
		motifError = null;
		UploadedFile pssmFile = event.getFile();
		if( !mainMotif.isCustom() || pssmFile == null ) {
			mainMotif.setCustomPssmFile(null);
			mainMotif.setPssm(null);
		} else {
			mainMotif.setCustomPssmFile(pssmFile.getFileName());
			//Reader rd = new InputStreamReader(pssmFile.getInputstream());
			try(Reader rd = new InputStreamReader(new ByteArrayInputStream(pssmFile.getContents()))) {
				mainMotif.setPssm(Pssm.load(rd, true));
			} catch (IOException e) {
				motifError = "File error: " + e.getMessage();
			} catch (PssmBuilderException e) {
				motifError = "PSSM not valid: " + e.getMessage();
			}	
		}
	}
	
	public String getPssmSummary() {
		if( mainMotif.isCustom() && mainMotif.getCustomPssmFile() != null )
			return mainMotif.getCustomPssmFile();
		return null;
	}
}
