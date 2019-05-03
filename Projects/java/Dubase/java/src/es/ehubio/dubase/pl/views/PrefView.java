package es.ehubio.dubase.pl.views;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import es.ehubio.dubase.Thresholds;

@Named
@SessionScoped
public class PrefView implements Serializable {
	private static final long serialVersionUID = 1L;
	private Thresholds thresholds = new Thresholds();
	@Inject
	private SearchView searchView;
	
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
