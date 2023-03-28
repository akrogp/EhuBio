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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.db.uniprot.Fetcher;
import es.ehubio.db.uniprot.UniProtUtils;
import es.ehubio.proteomics.pipeline.DecoyDb;
import es.ehubio.wregex.InputGroup;
import es.ehubio.wregex.data.DatabaseInformation;

@Named
@SessionScoped
public class TargetView implements Serializable {
	private static final long serialVersionUID = 1L;
	private String target;
	private DatabaseInformation targetInformation;
	private List<InputGroup> inputGroups = null;
	private List<InputGroup> decoyGroups = null;
	@Inject
	private DatabasesBean databases;
	@Inject
	private SearchView searchBean;
	private String baseFileName, fileName;
	private String inputText = "", targetError;
	private boolean downloading;
	private boolean decoy;
	
	public DatabaseInformation getTargetInformation() {
		return targetInformation;
	}
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public boolean isDecoy() {
		return decoy;
	}
	
	public void setDecoy(boolean decoy) {
		this.decoy = decoy;
	}
	
	public List<InputGroup> getInputGroups() {
		if( !decoy )
			return inputGroups;
		if( decoyGroups == null )
			buildDecoy();
		return decoyGroups;
	}
	
	private void buildDecoy() {
		if( inputGroups == null )
			return;
		decoyGroups = new ArrayList<>(inputGroups.size());
		for( InputGroup inputGroup : inputGroups ) {
			Fasta inputFasta = inputGroup.getFasta();
			Fasta decoyFasta = DecoyDb.getDecoy(inputFasta, DecoyDb.Strategy.SHUFFLE, null, "shuffle-");
			decoyGroups.add(new InputGroup(decoyFasta));
		}
	}

	private void reset(boolean resetInput) {
		searchBean.resetResult();
		inputGroups = null;
		decoyGroups = null;
		if( resetInput )
			inputText = "";
		targetError = null;
		fileName = null;
		baseFileName = null;
	}
	
	public void onChangeTarget( ValueChangeEvent event ) {
		reset(true);
		Object value = event.getNewValue();		
		if( value == null || value.toString().equals("none") ) {
			targetInformation = null;
			return;
		}
		targetInformation = stringToTarget(event.getNewValue());
		if( targetInformation.getType().equals("fasta") ) {
			try {
				inputGroups = databases.getFasta(targetInformation.getPath());
				baseFileName = FilenameUtils.removeExtension(new File(targetInformation.getPath()).getName());
			} catch( Exception e ) {
				targetError = e.getMessage();
			}
		}
	}
	
	public void onChangeDecoy( ValueChangeEvent event ) {
		searchBean.resetResult();
		decoyGroups = null;
	}
	
	public void onBuildDecoy( ValueChangeEvent event ) {
		searchBean.resetResult();
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
			if( isManualSubproteome() )
				return "Gene Ontology term must be specified to filter the proteome";
			if( isManualUniprot() )
				return "UniProt accessions must be specified, manually or uploading a text file";
			if( isManualGenes() )
				return "Gene symbols must be specified, manually or uploading a text file";			
		} else if( downloading )
			return inputText + " query was still processing";
		return "Unknown error";
	}
	
	public void uploadFile(FileUploadEvent event) {
		if( targetInformation == null )
			return;		
		reset(true);
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
			} else if( isManualGenes() ) {
				String inputText = IOUtils.toString(rd);
				parseInputGenes(inputText);
			} if( targetError != null )
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

	public String getBaseFileName() {
		String name = baseFileName != null ? baseFileName : "results";
		return decoy ? "decoy-" + name : name;
	}
	
	public String getFastaSummary() {
		if( inputGroups == null )
			return null;
		if( fileName == null )
			return inputGroups.size() + " entries";
		return fileName + ": " + inputGroups.size() + " entries";
	}
	
	public boolean isRemote() {
		return targetInformation != null || targetInformation.getType().equals("remote");
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
	
	public boolean isManualSubproteome() {
		return isManualTarget() && targetInformation.getName().contains("Subproteome");
	}
	
	public boolean isDownloading() {
		return downloading;
	}

	public void setInputText(String inputText) {
		this.inputText = inputText;
	}
	
	public String getInputText() {
		return inputText;
	}
	
	public void onChangeInput() {		
		reset(false);		
		inputText = inputText.trim();
		if( inputText.isBlank() )
			return;
		try {
			if( isManualFasta() )
				parseIputFasta(inputText);
			else if( isManualSubproteome() )
				downloadGo(inputText);
			else if( isManualUniprot() )
				parseInputUniProt(inputText);
			else if( isManualGenes() )
				parseInputGenes(inputText);			
		} catch (Exception e) {
			//searchBean.resetResult(e.getMessage());
			e.printStackTrace();
			targetError = e.getMessage();
			inputGroups = null;
		}
	}	

	public List<String> completeGo(String query) {
		String queryLower = query.toLowerCase();
		List<String> result = databases.getGoTerms().stream()
			.filter(term -> !term.isObsolete())
			.map(term -> String.format("%s (%s)", term.getId(), term.getName()))
			.filter(term -> term.toLowerCase().contains(queryLower))
			.collect(Collectors.toList());
		return result;
	}

	private void parseIputFasta(String inputText) throws Exception {
		if( inputText == null || inputText.isBlank() )
			throw new Exception("Empty input");
		if( inputText.charAt(0) != '>' )
			inputText = ">unnamed\n" + inputText;
		int i1 = 1, i2;
		do {
			i2 = inputText.indexOf('\n', i1); // header
			if( i2 > 0 && inputText.length() > i2 )
				i2 = inputText.indexOf('>', i2+1); // sequence
			String protein = inputText.substring(i1, i2 > 0 ? i2 : inputText.length());
			Fasta fasta = new Fasta(protein, SequenceType.PROTEIN);
			if( inputGroups == null )
				inputGroups = new ArrayList<>();
			inputGroups.add(new InputGroup(fasta));
			i1 = i2+1;
		} while( i2 > 0);
	}
	
	private void parseInputUniProt(String inputText) throws Exception {
		parseIdList(inputText, UniProtUtils::validAccession, "UniProt accession", Fasta::getAccession);
	}
	
	private void parseInputGenes(String inputText) throws Exception {
		parseIdList(inputText, null, "gene symbol", Fasta::getGeneName);		
	}
	
	private void parseIdList(String inputText, Predicate<String> checkId, String idType, Function<Fasta, String> getId) throws Exception {
		String ids[] = inputText.strip().toUpperCase().split("[ \\t\\n\\r,;:]+");
		if( checkId != null )
			for( String id : ids )
				if( !checkId.test(id) )
					throw new Exception(id + " is not a valid " + idType);
		Set<String> set = new TreeSet<>(Arrays.asList(ids));
		inputGroups = databases.getHumanProteome().stream()
			.filter(inputGroup -> {
				String id = getId.apply(inputGroup.getFasta());
				return id != null && set.contains(id);
			})
			.collect(Collectors.toList());
		if( inputGroups.size() != set.size() ) {
			Set<String> found = inputGroups.stream()
				.map(inputGroup -> getId.apply(inputGroup.getFasta()))
				.collect(Collectors.toSet());
			List<String> missing = set.stream()
				.filter(id -> !found.contains(id))
				.collect(Collectors.toList());
			throw new Exception(String.format("%s%s %s cannot be mapped to %s",
				StringUtils.capitalize(idType),
				missing.size() == 1 ? "" : "s",
				missing.size() == 1 ? missing.iterator().next() : es.ehubio.io.StringUtils.truncate(missing.toString(), 100, "...]"),
				databases.getHumanProteomeInformation().getFullName()));
		}
		if( set.size() == 1 )
			baseFileName = set.iterator().next();
	}
	
	private void downloadGo(String inputText) throws Exception {
		String go = inputText.split(" ")[0];
		int iName = inputText.indexOf('(');
		if( iName > 0 )
			baseFileName = inputText.substring(iName+1, inputText.length()-1);
		String query = String.format("(%s) AND (organism_id:9606) AND (reviewed:true)", go);
		downloading = true;
		String fasta = Fetcher.queryFasta(query);
		downloading = false;
		parseIputFasta(fasta);
	}
}
