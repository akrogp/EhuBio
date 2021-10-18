package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import es.ehubio.dubase.bl.Analyzer;
import es.ehubio.dubase.dl.entities.Experiment;

@Named
@ViewScoped
public class AnalyzeView implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Experiment> experiments;
	@EJB
	private Analyzer db;
	@Inject
	private VolcanoView volcanoView;
	private int expIdx = -1;
	private List<String> genes;
	
	public List<Experiment> getExperiments() {
		if( experiments == null )
			experiments = db.getVolcanoExperiments();
		return experiments;
	}
	
	public List<String> getVolcanos() {
		List<Experiment> exps = getExperiments();
		List<String> volcanos = new ArrayList<>(exps.size());
		for( Experiment exp : exps )
			volcanos.add(String.format("%s (%s)", exp.getEnzymeBean().getGene(), exp.getFmtId()));
		return volcanos;
	}

	public int getExpIdx() {
		return expIdx;
	}

	public void setExpIdx(int expIdx) {
		this.expIdx = expIdx;
	}
	
	public String volcano() {
		if( expIdx != -1 ) {
			volcanoView.setExperiment(experiments.get(expIdx));
			return volcanoView.plot();
		}
		return null;
	}
	
	public List<String> getGenes() {
		if( genes == null )
			genes = db.getEnzymesWithEvidences();
		return genes;
	}
}
