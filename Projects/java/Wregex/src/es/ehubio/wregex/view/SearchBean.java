package es.ehubio.wregex.view;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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
import es.ehubio.io.Streams;
import es.ehubio.wregex.InputGroup;
import es.ehubio.wregex.PssmBuilder.PssmBuilderException;
import es.ehubio.wregex.Wregex;
import es.ehubio.wregex.Wregex.WregexException;
import es.ehubio.wregex.data.CachedResult;
import es.ehubio.wregex.data.DatabaseInformation;
import es.ehubio.wregex.data.ResultEx;
import es.ehubio.wregex.data.ResultGroupEx;
import es.ehubio.wregex.data.Services;
import es.ehubio.wregex.view.DatabasesBean.ReloadException;

@Named
@SessionScoped
public class SearchBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(SearchBean.class.getName());
	private String target;
	private DatabaseInformation targetInformation;	
	private String searchError;
	private List<ResultEx> results = null;
	private String cachedAlnPath;
	private boolean grouping = true;
	private boolean filterEqual = false;
	private double scoreThreshold = 0.0;
	private boolean cosmic = false;
	private boolean dbPtm = false;	
	private String baseFileName, fastaFileName;
	private boolean assayScores = false;
	private List<InputGroup> inputGroups = null;
	@Inject
	private DatabasesBean databases;
	@Inject
	private MotifView motifs;
	private final Services services;
	private int flanking = 0;
	
	public SearchBean() {
		services = new Services(FacesContext.getCurrentInstance().getExternalContext());
	}
	
	public void resetResult() {
		searchError = null;
		results = null;
	}
	
	public List<DatabaseInformation> getTargets() {
		return databases.getTargets();
	}
	
	public DatabaseInformation getTargetInformation() {
		return targetInformation;
	}
	
	public DatabaseInformation getElmInformation() {
		return databases.getElmInformation();
	}
	
	public DatabaseInformation getCosmicInformation() {
		return databases.getCosmicInformation();
	}
	
	public DatabaseInformation getDbPtmInformation() {
		return databases.getDbPtmInformation();
	}
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
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
	
	public void onChangeTarget( ValueChangeEvent event ) {
		inputGroups = null;
		results = null;
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
				searchError = e.getMessage();
			}
		}
	}

	public String getConfigError() {
		String error = checkConfigError();
		if( error != null )
			results = null;
		return error;
	}
	
	private String checkConfigError() {
		String error = motifs.checkConfigError();
		if( error != null )
			return error;
		if( targetInformation == null )
			return "A target must be selected";
		if( inputGroups == null )
			return "A fasta file with input sequences must be uploaded";
		if( motifs.isAllMotifs() && inputGroups.size() > services.getInitNumber("wregex.allMotifs") )
			return String.format("Sorry, when searching for all motifs the number of target sequences is limited to %d", services.getInitNumber("wregex.allMotifs"));
		return null;
	}
	
	public void search() {
		searchError = null;		
		try {
			updateAssayScores();
			results = loadSearchCache();
			if( results == null ) {
				List<ResultGroupEx> resultGroups = motifs.isAllMotifs() == false ? singleSearch() : allSearch();
				results = Services.expand(resultGroups, grouping);				
				results = Services.filter(results, filterEqual, scoreThreshold);
				Services.flanking(results, flanking);
				if( motifs.isUseAuxMotif() )
					searchAux();
				if( cosmic )
					searchCosmic();
				if( dbPtm )
					searchDbPtm();
				Collections.sort(results);
				saveSearchCache(results);
			}
		} catch( IOException e ) {
			searchError = "File error: " + e.getMessage();
		} catch( PssmBuilderException e ) {
			searchError = "PSSM not valid: " + e.getMessage();
		} catch( WregexException e ) {
			searchError = "Invalid configuration: " + e.getMessage();
		} catch( Exception e ) {
			searchError = e.getMessage();
		}
	}
	
	private String getSingleRegex() {
		MotifBean motif = this.motifs.getMainMotif();
		return motif.isCustom() ? motif.getCustomRegex() : motif.getRegex();
	}
	
	private List<ResultGroupEx> singleSearch() throws NumberFormatException, Exception {
		initPssm();		
		Wregex wregex = new Wregex(getSingleRegex(), motifs.getMainMotif().getPssm());
		return Services.search(wregex, motifs.getMainMotif().getMotifInformation(), inputGroups, assayScores, services.getInitNumber("wregex.watchdogtimer")*1000);
	}
	
	private void initPssm() throws IOException, PssmBuilderException {
		MotifBean motif = this.motifs.getMainMotif();
		if( !motif.isCustom() && motif.getPssmFile() != null )
			motif.setPssm(services.getPssm(motif.getPssmFile()));		
	}
	
	private File getSearchCache() {
		if( services.getInitNumber("wregex.cacheSearch") == 0 )
			return null;
		DatabaseInformation cacheDb = databases.getDbWregex(); 
		if( cacheDb == null || motifs.isUseAuxMotif() || dbPtm || assayScores || motifs.getMainMotif().isCustom() )
			return null;
		return new File(cacheDb.getPath());
	}

	private List<ResultEx> loadSearchCache() {
		cachedAlnPath = null;
		if( targetInformation == null || !targetInformation.getType().equals("fasta") )
			return null;
		File dir = getSearchCache();
		if( dir == null )
			return null;		
		String[] files = dir.list(new FilenameFilter() {			
			@Override
			public boolean accept(File dir, String name) {
				return name.contains("-search.dat");
			}
		});
		for( String file : files ) {
			File cache = new File(dir,file);
			try( DataInputStream dis = new DataInputStream(Streams.getBinReader(cache)); ) {
				if( !dis.readUTF().equals("WCV3") )
					continue;
				if( !dis.readUTF().equals(targetInformation.getPath()) )
					continue;
				if( !dis.readUTF().equals(motifs.getMainMotif().getRegex()) )
					continue;
				if( !dis.readUTF().equals(motifs.getMainMotif().getPssmFile()) )
					continue;
				if( dis.readBoolean() != grouping )
					continue;
				if( dis.readBoolean() != cosmic )
					continue;
				if( dis.readBoolean() != filterEqual )
					continue;
				if( dis.readDouble() != scoreThreshold )
					continue;
				if( dis.readInt() != flanking )
					continue;
				logger.info("Using cached search");
				cachedAlnPath = cache.getAbsolutePath().replaceAll("\\.dat", ".aln");
				int len = dis.readInt();
				List<ResultEx> results = new ArrayList<>(len);
				for( int i = 0; i < len; i++ )
					results.add(loadSearchItem(dis));
				initPssm();
				return results;
			} catch( Exception e ) {
				logger.severe(e.getMessage());
			}
		}
		return null;
	}

	private void saveSearchCache(List<ResultEx> results) {
		if( targetInformation == null || !targetInformation.getType().equals("fasta") )
			return;
		File dir = getSearchCache();
		if( dir == null )
			return;
		boolean delete = false;
		long id = System.currentTimeMillis();
		File file = new File(dir,String.format("%s-search.dat.gz", id));
		try( DataOutputStream dos = new DataOutputStream(Streams.getBinWriter(file)); ) {
			dos.writeUTF("WCV3");
			dos.writeUTF(targetInformation.getPath());
			dos.writeUTF(motifs.getMainMotif().getRegex());
			dos.writeUTF(motifs.getMainMotif().getPssmFile());
			dos.writeBoolean(grouping);
			dos.writeBoolean(cosmic);
			dos.writeBoolean(filterEqual);
			dos.writeDouble(scoreThreshold);
			dos.writeInt(flanking);
			dos.writeInt(results.size());
			for( ResultEx result : results )
				saveSearchItem(dos, result);
			cachedAlnPath = new File(dir, String.format("%s-search.aln.gz", id)).getAbsolutePath();
			try( Writer wr = Streams.getTextWriter(cachedAlnPath); ) {
				ResultEx.saveAln(wr, results);
			} catch( Exception e ) {
				logger.severe(String.format("Could not save ALN cache: %s", e.getMessage()));
				new File(cachedAlnPath).delete();
			}
		} catch( Exception e ) {
			logger.severe(e.getMessage());
			delete = true;
		}
		if( delete )
			file.delete();
	}
	
	private void saveSearchItem( DataOutputStream dos, ResultEx result ) throws IOException {
		dos.writeUTF(result.getEntry());
		dos.writeInt(result.getStart());
		dos.writeInt(result.getEnd());
		dos.writeUTF(result.getAlignment());
		dos.writeInt(result.getCombinations());
		dos.writeDouble(result.getScore());
		dos.writeInt(result.getGroups().size());
		for( String group : result.getGroups() )
			if( group == null )
				dos.writeUTF("@null");
			else
				dos.writeUTF(group);
		dos.writeUTF(result.getMatch());
		dos.writeUTF(result.getSequence());
		dos.writeUTF(result.getName());
		dos.writeUTF(result.toString());
		dos.writeUTF(result.getAccession());
		dos.writeUTF(result.getMotif());
		dos.writeUTF(result.getFasta().getHeader());
		dos.writeUTF(result.getFasta().getSequence());
		if( cosmic ) {
			dos.writeUTF(result.getGene());
			dos.writeInt(result.getCosmicMissense());
			if( result.getCosmicMissense() >= 0 ) {
				dos.writeUTF(result.getMutSequence());
				dos.writeDouble(result.getMutScore());
				dos.writeUTF(result.getCosmicUrl());
			}
		}
	}
	
	private CachedResult loadSearchItem( DataInputStream dis ) throws IOException, InvalidSequenceException {
		CachedResult result = new CachedResult();
		result.setEntry(dis.readUTF());
		result.setStart(dis.readInt());
		result.setEnd(dis.readInt());
		result.setAlignement(dis.readUTF());
		result.setCombinations(dis.readInt());
		result.setScore(dis.readDouble());
		int ngroups = dis.readInt();
		List<String> groups = new ArrayList<String>(ngroups);
		for( int j = 0; j < ngroups; j++ ) {
			String group = dis.readUTF();
			if( group.equals("@null") )
				group = null;
			groups.add(group);
		}
		result.setGroups(groups);
		result.setMatch(dis.readUTF());
		result.setSequence(dis.readUTF());
		result.setName(dis.readUTF());
		result.setString(dis.readUTF());
		result.setAccession(dis.readUTF());
		result.setMotif(dis.readUTF());
		result.setFasta(new Fasta(dis.readUTF(), dis.readUTF(), SequenceType.PROTEIN));
		if( cosmic ) {
			result.setGene(dis.readUTF());
			result.setCosmicMissense(dis.readInt());
			if( result.getCosmicMissense() >= 0 ) {
				result.setMutSequence(dis.readUTF());							
				result.setMutScore(dis.readDouble());
				result.setCosmicUrl(dis.readUTF());
			}
		}
		return result;
	}

	private List<ResultGroupEx> allSearch() throws Exception {
		assayScores = false;
		//long div = getWregexMotifs().size() + getElmMotifs().size();
		//long tout = getInitNumber("wregex.watchdogtimer")*1000/div;
		long tout = services.getInitNumber("wregex.watchdogtimer")*1000;
		List<ResultGroupEx> results = services.searchAll(motifs.getAllMotifs(), inputGroups, tout);
		return results;
	}	

	private void searchCosmic() throws ReloadException {
		Services.searchCosmic(databases.getMapCosmic(), results, motifs.isUsingPssm());
	}
	
	private void searchDbPtm() throws ReloadException {
		Services.searchDbPtm(databases.getMapDbPtm(), results);
	}
	
	private void searchAux() throws Exception {
		MotifBean motif = this.motifs.getAuxMotif();
		if( motif.getPssmFile() != null )
			motif.setPssm(services.getPssm(motif.getPssmFile()));
		Wregex wregex = new Wregex(motif.getRegex(), motif.getPssm());		
		Services.searchAux(wregex, results);
	}

	public void uploadPssm( FileUploadEvent event ) {
		resetResult();
		try {
			motifs.uploadPssm(event);
		} catch (IOException e) {
			searchError = "File error: " + e.getMessage();
		} catch (PssmBuilderException e) {
			searchError = "PSSM not valid: " + e.getMessage();
		}		
	}
	
	public void uploadFasta(FileUploadEvent event) {
		searchError = null;
		results = null;
		UploadedFile fastaFile = event.getFile();
		if( fastaFile == null ) {
			inputGroups = null;
			return;
		}
		fastaFileName = fastaFile.getFileName();
		//Reader rd = new InputStreamReader(fastaFile.getInputstream());
		Reader rd = new InputStreamReader(new ByteArrayInputStream(fastaFile.getContents()));
		try {
			inputGroups = InputGroup.readEntries(rd);
			rd.close();
		} catch (IOException e) {
			searchError = "File error: " + e.getMessage();
			return;
		} catch (InvalidSequenceException e) {
			searchError = "Fasta not valid: " + e.getMessage();
			return;
		} 		
		baseFileName = FilenameUtils.removeExtension(fastaFile.getFileName());
	}
	
	private void updateAssayScores() {
		assayScores = true;
		for( InputGroup inputGroup : inputGroups )
			if( !inputGroup.hasScores() ) {
				assayScores = false;
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

	public boolean isGrouping() {
		return grouping;
	}

	public void setGrouping(boolean grouping) {
		this.grouping = grouping;
	}
	
	public boolean isFilterEqual() {
		return filterEqual;
	}

	public void setFilterEqual(boolean filterEqual) {
		this.filterEqual = filterEqual;
	}
	
	public void downloadCsv() {
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();
	    ec.responseReset();
	    ec.setResponseContentType("text/csv"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(length);
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\""+baseFileName+".csv\"");
		try {
			OutputStream output = ec.getResponseOutputStream();
			ResultEx.saveCsv(new OutputStreamWriter(output), results, assayScores, motifs.isUseAuxMotif(), cosmic, dbPtm );
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
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\""+baseFileName+".aln\"");
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
		downloadFile(motifs.getMainMotif().getPssmFile());
	}
	
	public void downloadAuxPssm() {
		downloadFile(motifs.getAuxMotif().getPssmFile());
	}

	public boolean getAssayScores() {
		return assayScores;
	}
	
	public String getFastaSummary() {
		if( inputGroups == null )
			return null;
		if( fastaFileName == null )
			return inputGroups.size() + " entries";
		return fastaFileName + ": " + inputGroups.size() + " entries";
	}	
	
	public void onChangeOption() {
		searchError = null;
		results = null;
	}
		
	public boolean isUploadTarget() {
		return targetInformation == null ? false : targetInformation.getType().equals("upload");
	}

	public void setDatabases(DatabasesBean databases) {
		this.databases = databases;
	}

	public boolean isCosmic() {
		return cosmic;
	}

	public void setCosmic(boolean cosmic) {
		this.cosmic = cosmic;
	}

	public boolean isDbPtm() {
		return dbPtm;
	}

	public void setDbPtm(boolean dbPtm) {
		this.dbPtm = dbPtm;
	}
	
	public boolean isInitialized() {
		return databases.isInitialized();
	}	

	public int getFlanking() {
		return flanking;
	}

	public void setFlanking(int flanking) {
		this.flanking = flanking;
	}

	public double getScoreThreshold() {
		return scoreThreshold;
	}

	public void setScoreThreshold(double scoreThreshold) {
		this.scoreThreshold = scoreThreshold;
	}
}