package es.ehubio.dubase.pl.views;

import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.bl.Searcher;
import es.ehubio.dubase.dl.CsvExporter;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.pl.beans.SearchBean;

@Named
@SessionScoped
public class SearchView implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private Searcher db;
	private String query, gene;
	private boolean dub, substrate, extended;
	private List<String> genes;
	private List<Evidence> rawResults;
	private List<SearchBean> results;
	@Inject
	private PrefView prefs;
	@Inject
	private VolcanoView volcanoView;
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public SearchView() {
		reset();
	}
	
	public void search() {
		clear();
		if( query == null )
			return;
		gene = query.trim().toUpperCase();
		if( gene.isEmpty() )
			return;
		Thresholds th = prefs.getThresholds();
		Collection<Evidence> tmpResults = !substrate ? db.searchEnzyme(gene, th) : (!dub ? db.searchSubstrate(gene, th) : db.search(gene, th));
		rawResults = new ArrayList<>(tmpResults);
		parseResults();
		return;
	}
	
	public void clear() {
		rawResults = null;
		results = null;
	}
	
	public void reset() {
		clear();
		dub = true;
		substrate = true;
		query = null;
		gene = null;
	}
	
	public void download() {
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();
	    ec.responseReset();
	    ec.setResponseContentType("text/csv");
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\"DUBase-"+gene+"-"+SDF.format(new Date())+".csv\"");
		try (
			PrintWriter pw = new PrintWriter(ec.getResponseOutputStream());
		){
			CsvExporter.export(rawResults, pw);
		} catch( Exception e ) {
			e.printStackTrace();
		} finally {
			fc.responseComplete();
		}
	}
	
	public void gprofiler() {
		ProfilerView.redirect(rawResults);
	}
	
	public String volcano() {
		volcanoView.setGene(gene);
		return volcanoView.plot();
	}

	private void parseResults() {
		results = new ArrayList<>(rawResults.size());
		for( Evidence ev : rawResults )
			results.add(new SearchBean(ev));
	}

	public String getQuery() {
		String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("dub"); 
		if( param != null ) {
			setDub(true);
			setSubstrate(false);
			setQuery(param);
			search();
		}
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	public List<SearchBean> getResults() {
		return results;
	}
	
	public List<Evidence> getRawResults() {
		return rawResults;
	}
	
	public List<String> getGenes() {
		if( genes == null )
			genes = db.searchEnzymesWithData().stream().map(e -> e.getGene()).collect(Collectors.toList()); 
		return genes;
	}

	public boolean isDub() {
		return dub;
	}

	public void setDub(boolean dub) {
		this.dub = dub;
	}

	public boolean isSubstrate() {
		return substrate;
	}

	public void setSubstrate(boolean substrate) {
		this.substrate = substrate;
	}
	
	public boolean isInvalid() {
		return !dub && !substrate;
	}

	public boolean isExtended() {
		return extended;
	}

	public void setExtended(boolean extended) {
		this.extended = extended;
	}
	
	public void toggle() {
		extended = !extended;
	}
}
