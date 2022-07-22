package es.ehubio.wregex.view;

import java.io.BufferedReader;
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
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;

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
	private String searchError;
	private List<ResultEx> results = null;
	private String cachedAlnPath;
	private boolean grouping = true;
	private boolean filterEqual = false;
	private double scoreThreshold = 0.0;
	private boolean cosmic = false;
	private boolean dbPtm = false;	
	private boolean assayScores = false;	
	@Inject
	private DatabasesBean databases;
	@Inject
	private MotifView motifView;
	@Inject
	private TargetView targetView;
	private final Services services;
	private int flanking = 0;
	
	public SearchBean() {
		services = new Services(FacesContext.getCurrentInstance().getExternalContext());
	}
	
	public void resetResult() {
		resetResult(null);
	}
	
	public void resetResult(String error) {
		searchError = error;
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
		if( motifView.isAllMotifs() && targetView.getInputGroups().size() > services.getInitNumber("wregex.allMotifs") )
			return String.format("Sorry, when searching for all motifs the number of target sequences is limited to %d", services.getInitNumber("wregex.allMotifs"));
		return null;
	}
	
	public void search() {
		searchError = null;		
		try {
			updateAssayScores();
			results = loadSearchCache();
			if( results == null ) {
				List<ResultGroupEx> resultGroups = motifView.isAllMotifs() == false ? singleSearch() : allSearch();
				results = Services.expand(resultGroups, grouping);				
				results = Services.filter(results, filterEqual, scoreThreshold);
				Services.flanking(results, flanking);
				if( motifView.isUseAuxMotif() )
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
	
	private List<ResultGroupEx> singleSearch() throws NumberFormatException, Exception {
		initPssm();		
		Wregex wregex = new Wregex(motifView.getMainMotif().getSingleRegex(), motifView.getMainMotif().getPssm());
		return Services.search(wregex, motifView.getMainMotif().getMotifInformation(), targetView.getInputGroups(), assayScores, services.getInitNumber("wregex.watchdogtimer")*1000);
	}
	
	private void initPssm() throws IOException, PssmBuilderException {
		MotifBean motif = this.motifView.getMainMotif();
		if( !motif.isCustom() && motif.getPssmFile() != null )
			motif.setPssm(services.getPssm(motif.getPssmFile()));		
	}
	
	private File getSearchCache() {
		if( services.getInitNumber("wregex.cacheSearch") == 0 )
			return null;
		DatabaseInformation cacheDb = databases.getDbWregex(); 
		if( cacheDb == null || motifView.isUseAuxMotif() || dbPtm || assayScores || motifView.getMainMotif().isCustom() )
			return null;
		return new File(cacheDb.getPath());
	}

	private List<ResultEx> loadSearchCache() {
		cachedAlnPath = null;
		DatabaseInformation targetInformation = targetView.getTargetInformation();
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
				if( !dis.readUTF().equals(motifView.getMainMotif().getRegex()) )
					continue;
				if( !dis.readUTF().equals(motifView.getMainMotif().getPssmFile()) )
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
		DatabaseInformation targetInformation = targetView.getTargetInformation();
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
			dos.writeUTF(motifView.getMainMotif().getRegex());
			dos.writeUTF(motifView.getMainMotif().getPssmFile());
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
		List<ResultGroupEx> results = services.searchAll(databases.getAllMotifs(), targetView.getInputGroups(), tout);
		return results;
	}	

	private void searchCosmic() throws ReloadException {
		Services.searchCosmic(databases.getMapCosmic(), results, motifView.isUsingPssm());
	}
	
	private void searchDbPtm() throws ReloadException {
		Services.searchDbPtm(databases.getMapDbPtm(), results);
	}
	
	private void searchAux() throws Exception {
		MotifBean motif = this.motifView.getAuxMotif();
		if( motif.getPssmFile() != null )
			motif.setPssm(services.getPssm(motif.getPssmFile()));
		Wregex wregex = new Wregex(motif.getRegex(), motif.getPssm());		
		Services.searchAux(wregex, results);
	}
	
	private void updateAssayScores() {
		assayScores = true;
		for( InputGroup inputGroup : targetView.getInputGroups() )
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
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\""+targetView.getBaseFileName()+".csv\"");
		try {
			OutputStream output = ec.getResponseOutputStream();
			ResultEx.saveCsv(new OutputStreamWriter(output), results, assayScores, motifView.isUseAuxMotif(), cosmic, dbPtm );
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

	public boolean getAssayScores() {
		return assayScores;
	}	
	
	public void onChangeOption() {
		searchError = null;
		results = null;
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