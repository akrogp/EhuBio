package es.ehubio.dubase.pl;

import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import es.ehubio.dubase.bl.Score;
import es.ehubio.dubase.bl.Searcher;
import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.dl.CsvExporter;
import es.ehubio.io.CsvUtils;

@Named
@SessionScoped
public class SearchView implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private Searcher db;
	private String query, gene;
	private List<EvidenceBean> rawResults;
	private List<SearchBean> results;
	@Inject
	private DetailsView detailsView;
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public void search() {
		gene = query.trim().toUpperCase();
		if( gene.isEmpty() )
			return;
		rawResults = new ArrayList<>(db.search(gene));
		rawResults.sort(new Comparator<EvidenceBean>() {
			@Override
			public int compare(EvidenceBean o1, EvidenceBean o2) {
				double s1 = Math.abs(o1.getMapScores().get(Score.FOLD_CHANGE.ordinal()));
				double s2 = Math.abs(o2.getMapScores().get(Score.FOLD_CHANGE.ordinal()));
				return (int)Math.signum(s2-s1);
			}
		});
		parseResults();
		return;
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

	private void parseResults() {
		results = new ArrayList<>(rawResults.size());
		for( EvidenceBean ev : rawResults ) {
			SearchBean result = parseResult(ev);
			results.add(result);
		}
	}

	private SearchBean parseResult(EvidenceBean ev) {
		SearchBean result = new SearchBean();
		
		result.setExperiment(String.format("EXP%05d", ev.getExperiment().getId()));
		result.setEnzyme(ev.getExperiment().getEnzymeBean().getGene());
		result.setGenes(CsvUtils.getCsv("<br/>", ev.getGenes().toArray()));
		result.setDescriptions(CsvUtils.getCsv("<br/>", ev.getDescriptions().toArray()));
		
		double foldChange = ev.getMapScores().get(Score.FOLD_CHANGE.ordinal());
		result.setFoldChange(String.format(
			"<font color='%s'>%.2f</font>",
			foldChange >= 0 ? Colors.UP_REGULATED : Colors.DOWN_REGULATED,
			foldChange));
		
		double pValue = ev.getMapScores().get(Score.P_VALUE.ordinal());
		pValue = Math.pow(10, -pValue);
		result.setpValue(String.format("%4.1e", pValue));
		
		result.setTotalPepts(ev.getMapScores().get(Score.TOTAL_PEPTS.ordinal()).intValue()+"");
		result.setUniqPepts(ev.getMapScores().get(Score.UNIQ_PEPTS.ordinal()).intValue()+"");
		
		double weight = ev.getMapScores().get(Score.MOL_WEIGHT.ordinal());
		result.setWeight(String.format("%.3f", weight));
		
		result.setGlygly(ev.getModPositions().isEmpty() ? "" : CsvUtils.getCsv(';', ev.getModPositions().toArray()));
		
		return result;
	}

	public String getQuery() {
		String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gene"); 
		if( param != null ) {
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
	
	public String onDetails(int i) {		
		detailsView.setResult(rawResults.get(i), results.get(i));
		return "details";
	}
}
