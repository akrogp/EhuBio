package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.Method;

@Named
@SessionScoped
public class PrefView implements Serializable {
	private static final long serialVersionUID = 1L;
	private Thresholds thresholds = new Thresholds();
	@Inject
	private SearchView searchView;
	private Map<Integer, Thresholds> mapThresholds = new HashMap<>();
	
	public Thresholds getThresholds(Experiment exp) {
		Method method = exp.getMethodBean();
		if( method.isManual() )
			return thresholds;
		Thresholds th = mapThresholds.get(exp.getId());
		if( th == null ) {
			th = new Thresholds();
			th.setFoldChange(method.getFoldThreshold());
			th.setpValue(method.getPvalueThreshold());
			mapThresholds.put(exp.getId(), th);
		}
		return th;
	}
	
	public Thresholds getThresholds() {
		return thresholds;
	}
	
	public void reset() {
		thresholds = new Thresholds();
		save();
	}
	
	public void save() {
		searchView.search();
	}
	
	public boolean isInvalid() {
		return !thresholds.isUp() && !thresholds.isDown();
	}
}
