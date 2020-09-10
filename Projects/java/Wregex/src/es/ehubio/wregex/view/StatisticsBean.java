package es.ehubio.wregex.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.wregex.Pssm;
import es.ehubio.wregex.Wregex;
import es.ehubio.wregex.data.BubbleChartData;
import es.ehubio.wregex.data.DatabaseInformation;
import es.ehubio.wregex.data.MotifDefinition;
import es.ehubio.wregex.data.MotifInformation;
import es.ehubio.wregex.data.ResultEx;
import es.ehubio.wregex.data.ResultGroupEx;
import es.ehubio.wregex.data.Services;
import es.ehubio.wregex.data.Versions;

@ManagedBean
@ApplicationScoped
public class StatisticsBean {
	private final static String cosmicBubbles = "cosmicBubbles.json";
	private final static String dbPtmBubbles = "dbPtmBubbles.json";
	
	private static final int maxBubbles = 1000;
	private static final int topCount = 10;
	private static final int maxMutations = 800;
	private static final int minMutations = 100;
	private static final int maxPTMs = 100;
	private static final int minPTMs = 30;
	
	private final static Logger logger = Logger.getLogger(StatisticsBean.class.getName());
	
	@ManagedProperty(value="#{databasesBean}")
	private DatabasesBean databases;
	private final Services services;
	
	private String jsonCosmic, jsonDbPtm;	
	private boolean initialized = false;	
	private List<String> displayTips = new ArrayList<>();	
	private String chart = "COSMIC";
	private String title;
	private final List<SelectItem> items;
	
	public StatisticsBean() {		
		services = new Services(FacesContext.getCurrentInstance().getExternalContext());
		items = new ArrayList<SelectItem>();
		items.add(new SelectItem("COSMIC", "COSMIC missense mutations"));
		if( Versions.DEV )
			items.add(new SelectItem("dbPTM", "dbPTM experimental PTMs"));
	}	

	@PostConstruct
	public void init() {
		loadChart( chart );
	}
	
	public List<SelectItem> getItems() {
		return items;
	}
	
	private void loadChart( String chart ) {
		boolean ok = false;
		displayTips.clear();		
		switch( chart ) {
			case "COSMIC":
				displayTips.add(String.format("Bubble size has been limited to %d mutations", maxMutations));
				displayTips.add(String.format("Motifs with less than %d mutations have been filtered", minMutations));
				setTitle(String.format(
					"Top %d proteins with COSMIC missense mutations for top Wregex motifs", topCount));
				jsonCosmic = loadBubbles(cosmicBubbles);
				ok = jsonCosmic != null;
				break;
			case "dbPTM":
				displayTips.add(String.format("Bubble size has been limited to %d PTMs", maxPTMs));
				displayTips.add(String.format("Motifs with less than %d PTMs have been filtered", minPTMs));
				setTitle(String.format(
					"Top %d proteins with dbPTM experimental PTMs for top Wregex motifs", topCount));
				jsonDbPtm = loadBubbles(dbPtmBubbles);
				ok = jsonDbPtm != null;
				break;
		}
		if( ok ) {
			logger.info("Using cached bubbles");
			initialized = true;
		} else
			new Initializer().start();
	}
	
	private String loadBubbles(String bubbles) {
		String result = null;
		try {
			Scanner scanner = new Scanner(new File(databases.getDbWregex().getPath(),bubbles)); 
			result = scanner.useDelimiter("\\A").next();
			scanner.close();
		} catch( Exception e ) {}
		return result;
	}
	
	public void onChangeChart( ValueChangeEvent event ) {
		loadChart( event.getNewValue().toString() );
	}

	public String getJsonCosmic() {		
		return jsonCosmic;
	}
	
	public String getJsonDbPtm() {
		return jsonDbPtm;
	}
	
	public String getJsonMotifs() {			
		switch( chart ) {
			case "COSMIC":			
				return getJsonCosmic();
			case "dbPTM":
				return getJsonDbPtm();
		}		
		return "{}";
	}

	public boolean isInitialized() {
		return initialized;
	}

	public int getTopCount() {
		return topCount;
	}

	public int getMaxMutations() {
		return maxMutations;
	}

	public int getMinMutations() {
		return minMutations;
	}
	
	public List<String> getDisplayTips() {
		return displayTips;
	}
	
	private void createJson() throws IOException, InvalidSequenceException, Exception {		
		List<MotifInformation> allMotis = databases.getNrMotifs();
		BubbleChartData dbPtmBubbles = new BubbleChartData();
		BubbleChartData cosmicBubbles = new BubbleChartData();
		List<ResultGroupEx> resultGroups;
		List<ResultEx> results;		
		MotifDefinition def;
		Pssm pssm;
		Wregex wregex;
		int max = maxBubbles;
		int i = 0;		
		long tout = services.getInitNumber("wregex.watchdogtimer")*1000;
		for( MotifInformation motifInformation : allMotis ) {
			logger.info(String.format(
				"Searching human proteome for %s motif (%d/%d) ...",
				motifInformation.getName(), ++i, allMotis.size()));
			def = motifInformation.getDefinitions().get(0);
			pssm = services.getPssm(def.getPssm());
			wregex = new Wregex(def.getRegex(), pssm);
			try {
				resultGroups = Services.search(wregex, motifInformation, databases.getHumanProteome(), false, tout);
			} catch( Exception e ) {
				logger.severe("Discarded by tout");
				continue;
			}
			results = Services.expand(resultGroups, true);
			
			searchDb("dbPTM", results);
			Collections.sort(results);
			addBubbles(dbPtmBubbles, "dbPTM", motifInformation, results);
			
			searchDb("COSMIC", results);
			Collections.sort(results);
			addBubbles(cosmicBubbles, "COSMIC", motifInformation, results);			
			
			if( --max <= 0 )
				break;
		}
		jsonCosmic = truncateJson(cosmicBubbles, minMutations, maxMutations);
		jsonDbPtm = truncateJson(dbPtmBubbles, minPTMs, maxPTMs);
		logger.info("finished!");
	}
	
	private void searchDb( String db, List<ResultEx> results ) throws InterruptedException {
		boolean retry;
		do {
			retry = false;
			try {
				switch( db ) {
					case "COSMIC": Services.searchCosmic(databases.getMapCosmic(), results, false); break;
					case "dbPTM": Services.searchDbPtm(databases.getMapDbPtm(), results); break;
				}				
			} catch( DatabasesBean.ReloadException e ) {
				Thread.sleep(5000);
				retry = true;
			}
		} while( retry );
	}
	
	private static void addBubbles(
			BubbleChartData root, String chart, MotifInformation motifInformation, List<ResultEx> results ) {
		
		String discretion;
		switch( chart ) {
			case "COSMIC": discretion = "COSMIC missense mutations"; break;	
			case "dbPTM": discretion = "dbPTM experimental PTMs"; break;
			default: discretion = ""; break;
		}
		
		BubbleChartData motif = new BubbleChartData();		
		motif.setName(motifInformation.getName());
		motif.setDescription(motifInformation.getSummary());
		motif.setDiscretion(String.format("%s in potencial motif candidates",discretion));
		
		BubbleChartData child;
		int count = topCount;
		int size;
		for( ResultEx result : results ) {
			if( result.getGene() == null )
				continue;
			size = getSize(chart, result);
			if( size <= 0 )
				continue;
			child = motif.getChild(result.getGene());
			if( child != null ) {				
				child.setSize(child.getSize()+size);
				continue;
			}
			child = new BubbleChartData();
			child.setName(result.getGene());
			child.setDescription(result.getFasta().getDescription());
			child.setDiscretion(String.format("%s in potencial motif %s candidates", discretion, motif.getName()));
			child.setSize(size);
			motif.addChild(child);
			if( --count <= 0 )
				break;
		}
		if( !motif.getChildren().isEmpty() ) {
			motif.setResult(""+motif.getChildsSize());
			root.addChild(motif);
		}
	}
	
	private static int getSize( String chart, ResultEx result ) {
		switch( chart ) {
			case "COSMIC": return result.getCosmicMissense();
			case "dbPTM": return result.getDbPtms();
		}
		return 0;
	}
	
	private static String truncateJson( BubbleChartData motifs, int minSize, int maxSize ) {
		BubbleChartData bubbles = new BubbleChartData();
		for( BubbleChartData motif : motifs.getChildren() ) {
			if( motif.getTotalSize() < minSize )
				continue;
			bubbles.addChild(motif);
			for( BubbleChartData gene : motif.getChildren() ) {	
				gene.setResult(""+gene.getSize());
				if( gene.getSize() > maxSize )
					gene.setSize(maxSize);
			}
		}
		return bubbles.toString(null);
	}
	
	private void saveJson() {
		DatabaseInformation db = databases.getDbWregex();
		if( db == null )
			return;
		try {
			PrintWriter pw = new PrintWriter(new File(db.getPath(),cosmicBubbles));
			pw.print(jsonCosmic);
			pw.close();
			
			pw = new PrintWriter(new File(db.getPath(),dbPtmBubbles));
			pw.print(jsonDbPtm);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public DatabasesBean getDatabases() {
		return databases;
	}

	public void setDatabases(DatabasesBean databases) {
		this.databases = databases;
	}
	
	public String getChart() {
		return chart;
	}

	public void setChart(String graph) {
		this.chart = graph;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}	

	private class Initializer extends Thread {		
		@Override
		public void run() {
			initialized = false;
			try {
				createJson();
				saveJson();
				logger.info("Bubbles saved for future uses");
				initialized = true;
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}
}
