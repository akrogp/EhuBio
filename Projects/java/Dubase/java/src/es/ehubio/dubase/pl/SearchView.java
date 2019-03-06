package es.ehubio.dubase.pl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import es.ehubio.dubase.bl.Score;
import es.ehubio.dubase.bl.Searcher;
import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.io.CsvUtils;

@Named
@SessionScoped
public class SearchView implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private Searcher db;
	private String query;
	private List<EvidenceBean> rawResults;
	private List<SearchBean> results;
	@Inject
	private DetailsView detailsView;
	
	public void search() {
		String gene = query.trim().toUpperCase();
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
		
		return result;
	}

	public String getQuery() {
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
