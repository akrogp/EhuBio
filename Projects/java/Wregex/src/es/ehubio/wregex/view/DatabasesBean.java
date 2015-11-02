package es.ehubio.wregex.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import es.ehubio.db.cosmic.CosmicStats;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.dbptm.ProteinPtms;
import es.ehubio.io.UnixCfgReader;
import es.ehubio.wregex.InputGroup;
import es.ehubio.wregex.data.DatabaseConfiguration;
import es.ehubio.wregex.data.DatabaseInformation;
import es.ehubio.wregex.data.MotifConfiguration;
import es.ehubio.wregex.data.MotifDefinition;
import es.ehubio.wregex.data.MotifInformation;
import es.ehubio.wregex.data.MotifReference;

@ManagedBean
@ApplicationScoped
public class DatabasesBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(DatabasesBean.class.getName());
	private static final String WregexMotifsPath = "/resources/data/motifs.xml";
	private static final String DatabasesPath = "/resources/data/databases.xml";
	
	private MotifConfiguration motifConfiguration;
	private DatabaseConfiguration databaseConfiguration;
	private List<MotifInformation> elmMotifs;
	private List<MotifInformation> allMotifs;
	private List<String> redundantMotifs;
	private List<MotifInformation> nrMotifs;
	private List<DatabaseInformation> targets;
	private DatabaseInformation elm;
	private DatabaseInformation cosmic;
	private DatabaseInformation dbPtm;
	private DatabaseInformation dbWregex;
	private Map<String,FastaDb> mapFasta;
	private Map<String,CosmicStats> mapCosmic;
	private Map<String, ProteinPtms> mapDbPtm;
	private long lastModifiedCosmic;
	private long lastModifiedElm;
	private long lastModifiedDbPtm;
	private String humanProteome;
	private int initialized = 0;	
	
	private class FastaDb {
		public long lastModified;
		public List<InputGroup> entries;
	}
	
	public DatabasesBean() throws IOException, InvalidSequenceException {
		loadDatabases();
	}

	private void loadDatabases() throws IOException, InvalidSequenceException {
		// Wregex motifs
		Reader rd = new InputStreamReader(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(WregexMotifsPath)); 		
		motifConfiguration = MotifConfiguration.load(rd);
		rd.close();
		redundantMotifs = new ArrayList<>();
		for( MotifInformation motifInformation : motifConfiguration.getMotifs() )
			if( motifInformation.getReplaces() != null )
				redundantMotifs.add(motifInformation.getReplaces());		
		
		// Databases
		rd = new InputStreamReader(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(DatabasesPath)); 		
		databaseConfiguration = DatabaseConfiguration.load(rd);
		rd.close();
		mapFasta = new HashMap<>();
		targets = new ArrayList<>();
		for( DatabaseInformation database : databaseConfiguration.getDatabases() ) {
			if( database.getType().equals("elm") ) {
				elm = database;
				refreshElm();
				continue;
			}
			if( database.getType().equals("cosmic") ) {
				cosmic = database;
				//refreshCosmic();
				continue;
			}
			if( database.getType().equals("dbptm") ) {
				dbPtm = database;
				//refreshDbPtm();
				continue;
			}
			if( database.getType().equals("wregex") ) {
				dbWregex = database;
				continue;
			}
			targets.add(database);
			if( !database.getType().equals("fasta") )
				continue;
			FastaDb fasta = new FastaDb();			
			File f = new File(database.getPath());
			fasta.lastModified = f.lastModified();
			fasta.entries = loadFasta(database.getPath());
			mapFasta.put(database.getPath(), fasta);
			if( database.getName().contains("Human Proteome") )
				humanProteome = database.getPath();
		}
		
		refreshCosmic();
		refreshDbPtm();
	}
	
	private List<InputGroup> loadFasta( String path ) throws IOException, InvalidSequenceException {
		logger.info("Loading DB: " + path);
		Reader rd;
		if( path.endsWith("gz") )
			rd = new InputStreamReader(new GZIPInputStream(new FileInputStream(path)));
		else
			rd = new FileReader(path);
		List<InputGroup> result = InputGroup.readEntries(rd);
		rd.close();
		logger.info("Loaded!");
		return result;
	}
	
	public List<MotifInformation> getWregexMotifs() {
		return motifConfiguration.getMotifs();
	}
	
	public List<DatabaseInformation> getTargets() {
		return targets;
	}
	
	public List<MotifInformation> getElmMotifs() {
		refreshElm();	
		return elmMotifs;
	}
	
	public List<MotifInformation> getAllMotifs() {
		File file = new File(elm.getPath());
		if( file.lastModified() != lastModifiedElm || allMotifs == null ) {
			allMotifs = new ArrayList<>();
			allMotifs.addAll(getWregexMotifs());
			allMotifs.addAll(getElmMotifs());
		}
		return allMotifs;
	}
	
	public List<MotifInformation> getNrMotifs() {
		File file = new File(elm.getPath());
		if( file.lastModified() != lastModifiedElm || nrMotifs == null ) {
			nrMotifs = new ArrayList<>();
			nrMotifs.addAll(getWregexMotifs());
			getElmMotifs();
			for( MotifInformation motifInformation : elmMotifs )
				if( !redundantMotifs.contains(motifInformation.getName()) )
					nrMotifs.add(motifInformation);
		}
		return nrMotifs;
	}
	
	public List<InputGroup> getFasta( String path ) throws IOException, InvalidSequenceException {		
		File file = new File(path);
		FastaDb fasta = mapFasta.get(path);
		if( fasta == null ) {
			fasta = new FastaDb();
			fasta.lastModified = -1;
			mapFasta.put(path, fasta);
		}
		if( fasta.lastModified != file.lastModified() ) {
			fasta.entries = loadFasta(path);
			fasta.lastModified = file.lastModified();
		}
		return fasta.entries;
	}
	
	public List<InputGroup> getHumanProteome() throws IOException, InvalidSequenceException {
		return getFasta(humanProteome);
	}
	
	public DatabaseInformation getElmInformation() {
		return elm;
	}
	
	public DatabaseInformation getCosmicInformation() {
		return cosmic;
	}
	
	public DatabaseInformation getDbPtmInformation() {
		return dbPtm;
	}
	
	public DatabaseInformation getDbWregex() {
		return dbWregex;
	}
	
	public Map<String,CosmicStats> getMapCosmic() throws ReloadException {
		if( !isInitialized() || refreshCosmic() )
			throw new ReloadException(cosmic.getFullName());
		return mapCosmic;
	}
	
	public Map<String, ProteinPtms> getMapDbPtm() throws ReloadException {
		if( !isInitialized() || refreshDbPtm() )
			throw new ReloadException(dbPtm.getFullName());
		return mapDbPtm;
	}	

	public boolean isInitialized() {
		return initialized == 0;
	}	
	
	private boolean refreshElm() {		
		try {
			File file = new File(elm.getPath());
			if( file.lastModified() == lastModifiedElm )
				return false;
			loadElmMotifs();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}	
	
	private boolean refreshCosmic() {
		if( cosmic == null )
			return false;
		File file = new File(cosmic.getPath());
		if( file.lastModified() == lastModifiedCosmic )
			return false;
		Initializer initializer = new Initializer(cosmic.getName());
		initializer.start();
		return true;
	}
	
	private boolean refreshDbPtm() {
		if( dbPtm == null )
			return false;
		File file = new File(dbPtm.getPath());
		if( file.lastModified() == lastModifiedDbPtm )
			return false;
		Initializer initializer = new Initializer(dbPtm.getName());
		initializer.start();
		return true;
	}
	
	private void loadElmMotifs() throws IOException {
		logger.info("Loading DB: " + elm.getPath());
		elmMotifs = new ArrayList<>();
		MotifInformation motif;
		MotifDefinition definition;
		List<MotifDefinition> definitions;
		MotifReference reference;
		List<MotifReference> references;
		File elmFile = new File(elm.getPath());
		UnixCfgReader rd = new UnixCfgReader(new FileReader(elmFile));
		String line;
		String[] fields;
		boolean first = true;
		while( (line=rd.readLine()) != null ) {
			if( first == true ) {
				first = false;
				continue;
			}
			fields = line.replaceAll("\"","").split("\t");
			motif = new MotifInformation();
			motif.setName(fields[1]);
			motif.setSummary(fields[2]);
			definition = new MotifDefinition();
			definition.setName(fields[0]);
			definition.setDescription("ELM regular expression without using Wregex capturing groups and PSSM capabilities");
			definition.setRegex(fields[3].replaceAll("\\(", "(?:"));
			definitions = new ArrayList<>();
			definitions.add(definition);
			motif.setDefinitions(definitions);
			reference = new MotifReference();
			reference.setName("Original ELM entry");
			reference.setLink("http://elm.eu.org/elms/elmPages/"+fields[1]+".html");
			references = new ArrayList<>();
			references.add(reference);
			motif.setReferences(references);
			elmMotifs.add(motif);
		}
		rd.close();
		lastModifiedElm = elmFile.lastModified();
		String version = rd.getComment("ELM_Classes_Download_Date");
		if( version != null )
			elm.setVersion(version.split(" ")[1]);
		logger.info("Loaded " + elm.getFullName() + "!");
	}
	
	private void loadCosmic() throws FileNotFoundException, IOException {
		logger.info("Loading DB: " + cosmic.getFullName());
		mapCosmic = CosmicStats.load(cosmic.getPath());
		lastModifiedCosmic = new File(cosmic.getPath()).lastModified();
		logger.info("Loaded!");
	}
	
	private void loadDbPtm() throws IOException {
		logger.info("Loading DB: " + dbPtm.getFullName());
		mapDbPtm = ProteinPtms.load(dbPtm.getPath());
		lastModifiedDbPtm = new File(dbPtm.getPath()).lastModified();
		logger.info("Loaded!");
	}
	
	private class Initializer extends Thread {
		private final String db;
		
		public Initializer( String db ) {
			this.db = db;
		}
		
		@Override
		public void run() {
			initialized--;
			try { 
				if( elm != null && db.equals(elm.getName()) )
					loadElmMotifs();
				else if( cosmic != null && db.equals(cosmic.getName()) )
					loadCosmic();
				else if( dbPtm != null && db.equals(dbPtm.getName()) )
					loadDbPtm();
				initialized++;
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}				
	}
	
	public static class ReloadException extends Exception {
		private static final long serialVersionUID = 1L;
		private static final String message = "beeing updated, please try again later";
		public ReloadException() {
			super("Databases are "+message);
		}
		public ReloadException( String db ) {
			super(db+" database is "+message);
		}
	}
}