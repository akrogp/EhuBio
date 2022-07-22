package es.ehubio.wregex.view;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.wregex.InputGroup;
import es.ehubio.wregex.data.DatabaseInformation;

@Named
@SessionScoped
public class TargetView implements Serializable {
	private static final long serialVersionUID = 1L;
	private String target;
	private DatabaseInformation targetInformation;
	private List<InputGroup> inputGroups = null;
	@Inject
	private DatabasesBean databases;
	@Inject
	private SearchView searchBean;
	private String baseFileName, fastaFileName;
	
	public DatabaseInformation getTargetInformation() {
		return targetInformation;
	}
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public List<InputGroup> getInputGroups() {
		return inputGroups;
	}
	
	public void onChangeTarget( ValueChangeEvent event ) {
		searchBean.resetResult();
		inputGroups = null;
		Object value = event.getNewValue();		
		if( value == null || value.toString().equals("Default") ) {
			targetInformation = null;
			return;
		}
		targetInformation = stringToTarget(event.getNewValue());
		if( targetInformation.getType().equals("fasta") ) {
			try {
				inputGroups = databases.getFasta(targetInformation.getPath());
				fastaFileName = null;
				baseFileName = FilenameUtils.removeExtension(new File(targetInformation.getPath()).getName());
			} catch( Exception e ) {
				searchBean.resetResult(e.getMessage());
			}
		}
	}
	
	private DatabaseInformation stringToTarget( Object object ) {
		if( object == null )
			return null;
		String name = object.toString();
		for( DatabaseInformation target : databases.getTargets() )
			if( name.startsWith(target.getName()) )
				return target;
		return null;
	}

	public String checkConfigError() {
		if( targetInformation == null )
			return "A target must be selected";
		if( inputGroups == null )
			return "A fasta file with input sequences must be uploaded";
		return null;
	}
	
	public void uploadFasta(FileUploadEvent event) {
		searchBean.resetResult();
		UploadedFile fastaFile = event.getFile();
		if( fastaFile == null ) {
			inputGroups = null;
			return;
		}
		fastaFileName = fastaFile.getFileName();
		//Reader rd = new InputStreamReader(fastaFile.getInputstream());
		try(Reader rd = new InputStreamReader(new ByteArrayInputStream(fastaFile.getContents()))) {
			inputGroups = InputGroup.readEntries(rd);
		} catch (IOException e) {
			searchBean.resetResult("File error: " + e.getMessage());
			return;
		} catch (InvalidSequenceException e) {
			searchBean.resetResult("Fasta not valid: " + e.getMessage());
			return;
		} 		
		baseFileName = FilenameUtils.removeExtension(fastaFile.getFileName());
	}
	
	public String getBaseFileName() {
		return baseFileName;
	}
	
	public String getFastaSummary() {
		if( inputGroups == null )
			return null;
		if( fastaFileName == null )
			return inputGroups.size() + " entries";
		return fastaFileName + ": " + inputGroups.size() + " entries";
	}
	
	public boolean isUploadTarget() {
		return targetInformation == null ? false : targetInformation.getType().equals("upload");
	}
}
