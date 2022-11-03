package es.ehubio.wregex.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import es.ehubio.db.PtmItem;
import es.ehubio.db.cosmic.CosmicStats;
import es.ehubio.db.dbptm.Entry;
import es.ehubio.db.dbptm.TxtReader;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.go.Ontology;
import es.ehubio.db.go.Term;
import es.ehubio.db.psp.PspFile;
import es.ehubio.db.psp.Site;
import es.ehubio.dbptm.ProteinPtms;
import es.ehubio.io.UnixCfgReader;
import es.ehubio.wregex.InputGroup;
import es.ehubio.wregex.data.DatabaseConfiguration;
import es.ehubio.wregex.data.DatabaseInformation;
import es.ehubio.wregex.data.MotifConfiguration;
import es.ehubio.wregex.data.MotifDefinition;
import es.ehubio.wregex.data.MotifInformation;
import es.ehubio.wregex.data.MotifReference;
import es.ehubio.wregex.data.PresetBean;
import es.ehubio.wregex.data.PresetConfiguration;
import es.ehubio.wregex.data.Versions;

@Named
@ApplicationScoped
public class DatabasesBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(DatabasesBean.class.getName());
	private static final String WregexMotifsPath = "/resources/data/motifs.xml";
	private static final String DatabasesPath = "/resources/data/databases.xml";
	private static final String PresetsPath = "/resources/data/presets.xml";
	
	private MotifConfiguration motifConfiguration;
	private DatabaseConfiguration databaseConfiguration;
	private PresetConfiguration presetConfiguration;
	private List<MotifInformation> elmMotifs;
	private List<MotifInformation> allMotifs;
	private List<String> redundantMotifs;
	private List<MotifInformation> nrMotifs;
	private List<DatabaseInformation> targets;
	private DatabaseInformation elm;
	private DatabaseInformation cosmic;
	private DatabaseInformation dbPtm;
	private DatabaseInformation psp;
	private DatabaseInformation dbWregex;
	private DatabaseInformation humanProteome;
	private DatabaseInformation go;
	private Map<String,FastaDb> mapFasta;
	private Map<String,CosmicStats> mapCosmic;
	private Map<String, ProteinPtms> mapDbPtm;
	private Map<String, ProteinPtms> mapPsp;
	private List<String> ptmsDbPtm;
	private List<String> ptmsPsp;
	private List<Term> goTerms;
	
	private class FastaDb {
		public long lastModified;
		public List<InputGroup> entries;
	}
	
	public DatabasesBean() throws IOException, InvalidSequenceException {
		loadDatabases();
	}

	private void loadDatabases() throws IOException, InvalidSequenceException {		
		// Wregex motifs
		try(Reader rd = new InputStreamReader(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(WregexMotifsPath))) { 		
			motifConfiguration = MotifConfiguration.load(rd);
			filterPrivateMotifs();
			redundantMotifs = new ArrayList<>();
			for( MotifInformation motifInformation : motifConfiguration.getMotifs() )
				if( motifInformation.getReplaces() != null )
					redundantMotifs.add(motifInformation.getReplaces());		
		}
		
		// Databases
		try(Reader rd = new InputStreamReader(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(DatabasesPath))) { 		
			databaseConfiguration = DatabaseConfiguration.load(rd);
			mapFasta = new HashMap<>();
			targets = new ArrayList<>();
			for( DatabaseInformation database : databaseConfiguration.getDatabases() ) {
				if( Versions.PROD && database.getWregexVersion() != null && (database.getWregexVersion() == 0 || Versions.MAJOR < database.getWregexVersion()) )
					continue;
				if( database.getType().equals("elm") ) {
					elm = database;
					continue;
				}
				if( database.getType().equals("cosmic") ) {
					cosmic = database;
					continue;
				}
				if( database.getType().equals("dbptm") ) {
					dbPtm = database;
					continue;
				}
				if( database.getType().equals("psp") ) {
					psp = database;
					continue;
				}
				if( database.getType().equals("go") ) {
					go = database;
					continue;
				}
				if( database.getType().equals("wregex") ) {
					dbWregex = database;
					continue;
				}
				targets.add(database);
				if( !database.getType().equals("fasta") )
					continue;
				if( database.getName().contains("Human Proteome") )
					humanProteome = database;
			}
		}
		
		// Presets
		try( Reader rd = new InputStreamReader(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(PresetsPath)) ) {
			presetConfiguration = PresetConfiguration.load(rd);
		}
	}
	
	public List<PresetBean> getPresets() {
		return presetConfiguration.getPresets();
	}
	
	public List<PresetBean> getCaseStudies() {
		return getPresets().stream().filter(preset -> preset.getValue().startsWith("case")).collect(Collectors.toList());
	}
	
	private void filterPrivateMotifs() {
		List<MotifInformation> filter = new ArrayList<>();
		for( MotifInformation motif : motifConfiguration.getMotifs() )
			if( Versions.PROD && motif.getWregexVersion() != null && (motif.getWregexVersion() == 0 || Versions.MAJOR < motif.getWregexVersion()) )
				filter.add(motif);
		motifConfiguration.getMotifs().removeAll(filter);
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
		if( elmMotifs == null )
			loadElmMotifs();
		return elmMotifs;
	}
	
	public List<MotifInformation> getAllMotifs() {
		if( allMotifs == null ) {
			allMotifs = new ArrayList<>();
			allMotifs.addAll(getWregexMotifs());
			allMotifs.addAll(getElmMotifs());
		}
		return allMotifs;
	}
	
	public List<MotifInformation> getNrMotifs() {
		if( nrMotifs == null ) {
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
		return getFasta(humanProteome.getPath());
	}
	
	public DatabaseInformation getHumanProteomeInformation() {
		return humanProteome;
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
	
	public Map<String,CosmicStats> getMapCosmic() {
		if( mapCosmic == null )
			loadCosmic();
		return mapCosmic;
	}
	
	public Map<String, ProteinPtms> getMapDbPtm() {
		if( mapDbPtm == null )
			loadDbPtm();
		return mapDbPtm;
	}
	
	public Map<String, ProteinPtms> getMapPsp() {
		if( mapPsp == null )
			loadPsp();
		return mapPsp;
	}
	
	public List<String> getDbPtms() {
		if( ptmsDbPtm == null )
			loadDbPtm();
		return ptmsDbPtm;
	}
	
	public List<String> getPspPtms() {
		if( ptmsPsp == null )
			loadPsp();
		return ptmsPsp;
	}
	
	public List<Term> getGoTerms() {
		if( goTerms == null )
			loadGo();
		return goTerms;
	}
	
	private void loadElmMotifs() {
		logger.info("Loading DB: " + elm.getPath());
		elmMotifs = new ArrayList<>();
		MotifInformation motif;
		MotifDefinition definition;
		List<MotifDefinition> definitions;
		MotifReference reference;
		List<MotifReference> references;
		File elmFile = new File(elm.getPath());
		try(UnixCfgReader rd = new UnixCfgReader(new FileReader(elmFile))) {
			String line;
			String[] fields;
			boolean first = true;
			while( (line=rd.readLine()) != null ) {
				if( first == true ) {
					first = false;
					continue;
				}
				if( !rd.getComment("ELM_Classes_Download_Version").contains("1.4") )
					throw new IOException("ELM file version not supported");
				fields = line.replaceAll("\"","").split("\t");
				motif = new MotifInformation();
				motif.setName(fields[1]);
				motif.setSummary(fields[3]);
				definition = new MotifDefinition();
				definition.setName(fields[0]);
				definition.setDescription("ELM regular expression without using Wregex capturing groups and PSSM capabilities");
				definition.setRegex(fields[4].replaceAll("\\(", "(?:"));
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
			String version = rd.getComment("ELM_Classes_Download_Date");
			if( version != null )
				elm.setVersion(version.split(" ")[1]);
			logger.info("Loaded " + elm.getFullName() + "!");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void loadCosmic() {
		logger.info("Loading DB: " + cosmic.getFullName());
		try {
			mapCosmic = CosmicStats.load(cosmic.getPath());
			logger.info("Loaded " + cosmic.getFullName() + "!");
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void loadDbPtm() {
		logger.info("Loading DB: " + dbPtm.getFullName());
		try {
			List<Entry> list = TxtReader.readFile(dbPtm.getPath());
			mapDbPtm = ProteinPtms.load(list);
			ptmsDbPtm = buildPtmList(list);
			logger.info("Loaded " + dbPtm.getFullName() + "!");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadPsp() {
		logger.info("Loading DB: " + psp.getFullName());
		try {
			List<Site> list = PspFile.readDir(psp.getPath()).stream()
				.filter(site -> site.getOrganism().equals("human"))
				.collect(Collectors.toList());
			mapPsp = ProteinPtms.load(list);
			ptmsPsp = buildPtmList(list);
			logger.info("Loaded " + psp.getFullName() + "!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadGo() {
		logger.info("Loading DB: " + go.getFullName());
		try {
			goTerms = Ontology.loadTerms(go.getPath());
			logger.info("Loaded " + go.getFullName() + "!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<String> buildPtmList(List<? extends PtmItem> list) {
		return list.stream().map(PtmItem::getType)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())) 
		    .entrySet()
		    .stream()
		    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
		    .map(Map.Entry::getKey)
		    .collect(Collectors.toList());
	}
}