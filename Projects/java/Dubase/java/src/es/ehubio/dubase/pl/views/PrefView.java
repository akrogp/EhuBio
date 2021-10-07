package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.bl.Searcher;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.Method;

@Named
@SessionScoped
public class PrefView implements Serializable {
	private static final long serialVersionUID = 1L;
	@Inject
	private SearchView searchView;
	private Map<Integer, Thresholds> mapThresholds;
	private List<Experiment> exps;
	@EJB
	private Searcher searcher;
	
	public List<Experiment> getExperiments() {
		if( exps == null )
			exps = searcher.findExperimentsWithThreholds();
		return exps;
	}
	
	public Map<Integer, Thresholds> getMapThresholds() {
		if( mapThresholds == null ) {
			mapThresholds = new HashMap<>();
			for( Experiment exp : getExperiments() ) {
				Method method = exp.getMethodBean();
				Thresholds th = new Thresholds();
				th.setFoldChange(method.getFoldThreshold());
				th.setpValue(method.getPvalueThreshold());
				mapThresholds.put(exp.getId(), th);
			}
		}
		return mapThresholds;
	}
	
	public Thresholds getThresholds(Experiment exp) {
		return getMapThresholds().get(exp.getId());
	}
	
	public void reset() {
		exps = null;
		mapThresholds = null;
		save();
	}
	
	public void save() {
		searchView.search();
	}
	
	public boolean isInvalid() {
		for( Thresholds th : getMapThresholds().values() )
			if( !th.isUp() && !th.isDown() )
				return true;
		return false;
	}
}
