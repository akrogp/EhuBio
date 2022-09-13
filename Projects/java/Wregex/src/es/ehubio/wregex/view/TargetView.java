package es.ehubio.wregex.view;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.db.uniprot.UniProtUtils;
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
	private String baseFileName, fileName;
	private String inputText = "", targetError;
	
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
		inputText = "";
		targetError = null;
		Object value = event.getNewValue();		
		if( value == null || value.toString().equals("Default") ) {
			targetInformation = null;
			return;
		}
		targetInformation = stringToTarget(event.getNewValue());
		if( targetInformation.getType().equals("fasta") ) {
			try {
				inputGroups = databases.getFasta(targetInformation.getPath());
				fileName = null;
				baseFileName = FilenameUtils.removeExtension(new File(targetInformation.getPath()).getName());
			} catch( Exception e ) {
				targetError = e.getMessage();
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
		if( inputGroups != null )
			return null;
		if( targetError != null )
			return targetError;
		if( inputText.isBlank() ) {
			if( isManualFasta() )
				return "Target fasta sequences must be specified, manually or uploading a fasta file";
			if( isManualUniprot() )
				return "UniProt accessions must be specified, manually or uploading a text file";
			if( isManualGenes() )
				return "Gene symbols must be specified, manually or uploading a text file";
		}
		return "Unknown error";
	}
	
	public void uploadFile(FileUploadEvent event) {
		if( targetInformation == null )
			return;
		searchBean.resetResult();
		inputGroups = null;
		inputText = "";
		targetError = null;
		fileName = null;
		UploadedFile file = event.getFile();
		if( file == null )			
			return;
		fileName = file.getFileName();
		//Reader rd = new InputStreamReader(fastaFile.getInputstream());
		try(Reader rd = new InputStreamReader(new ByteArrayInputStream(file.getContents()))) {
			if( isManualFasta() )
				inputGroups = InputGroup.readEntries(rd);
			else if( isManualUniprot() ) {
				String inputText = IOUtils.toString(rd);
				parseInputUniProt(inputText);
			} else if( isManualGenes() )
				loadFromGenes(rd);
			if( targetError != null )
				throw new Exception(targetError);
		} catch (IOException e) {
			inputGroups = null;
			targetError = "File error: " + e.getMessage();
			return;
		} catch (InvalidSequenceException e) {
			inputGroups = null;
			targetError = "Fasta not valid: " + e.getMessage();
			return;
		} catch (Exception e) {
			inputGroups = null;
			targetError = e.getMessage();
			return;
		} 		
		baseFileName = FilenameUtils.removeExtension(file.getFileName());
	}	
	
	private void loadFromGenes(Reader rd) {
		// TODO Auto-generated method stub
		
	}

	public String getBaseFileName() {
		return baseFileName;
	}
	
	public String getFastaSummary() {
		if( inputGroups == null )
			return null;
		if( fileName == null )
			return inputGroups.size() + " entries";
		return fileName + ": " + inputGroups.size() + " entries";
	}
	
	public boolean isManualTarget() {
		return targetInformation == null ? false : targetInformation.getType().equals("manual");
	}
	
	public boolean isManualFasta() {
		return isManualTarget() && targetInformation.getName().contains("fasta");
	}
	
	public boolean isManualUniprot() {
		return isManualTarget() && targetInformation.getName().contains("UniProt");
	}
	
	public boolean isManualGenes() {
		return isManualTarget() && targetInformation.getName().contains("gene symbol");
	}

	public void setInputText(String inputText) {
		this.inputText = inputText;
	}
	
	public String getInputText() {
		return inputText;
	}
	
	public void onChangeInput() {
		searchBean.resetResult();
		targetError = null;
		inputGroups = null;		
		inputText = inputText.trim();
		fileName = null;
		if( inputText.isBlank() )
			return;
		try {
			if( isManualFasta() )
				parseIputFasta(inputText);
			else if( isManualUniprot() )
				parseInputUniProt(inputText);
			else if( isManualGenes() )
				parseInputGenes();
		} catch (Exception e) {
			//searchBean.resetResult(e.getMessage());
			targetError = e.getMessage();
			inputGroups = null;
		}
	}	

	private void parseIputFasta(String inputText) throws InvalidSequenceException {
		if( inputText.charAt(0) != '>' )
			inputText = ">unnamed\n" + inputText;
		int i1 = 0, i2;
		do {
			i2 = inputText.indexOf('>', i1+1);
			String protein = inputText.substring(i1, i2 > 0 ? i2 : inputText.length());
			Fasta fasta = new Fasta(protein, SequenceType.PROTEIN);
			if( inputGroups == null )
				inputGroups = new ArrayList<>();
			inputGroups.add(new InputGroup(fasta));
			i1 = i2;
		} while( i2 > 0);
	}
	
	private void parseInputUniProt(String inputText) throws Exception {
		String accessions[] = inputText.strip().split("[ \\t\\n\\r,;:]+");
		for( String acc : accessions )
			if( !UniProtUtils.validAccession(acc) )
				throw new Exception(acc + " is not a valid UniProt accession");
		Set<String> set = new TreeSet<>(Arrays.asList(accessions));
		inputGroups = databases.getHumanProteome().stream()
			.filter(inputGroup -> set.contains(inputGroup.getFasta().getAccession()))
			.collect(Collectors.toList());
		if( inputGroups.size() != accessions.length ) {
			Set<String> found = inputGroups.stream()
				.map(inputGroup -> inputGroup.getFasta().getAccession())
				.collect(Collectors.toSet());
			List<String> missing = set.stream()
				.filter(acc -> !found.contains(acc))
				.collect(Collectors.toList());
			throw new Exception(String.format("%s %s cannot be mapped to %s",
				missing.size() == 1 ? "Accession" : "Accessions",
				missing.size() == 1 ? missing.iterator().next() : missing.toString(),
				databases.getHumanProteomeInformation().getFullName()));
		}
	}
	
	private void parseInputGenes() {
		// TODO Auto-generated method stub
		
	}
}
