package es.ehubio.dubase.pl.views;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import es.ehubio.dubase.bl.Searcher;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.pl.beans.OperationBean;

@Named
@ApplicationScoped
public class HomeView {
	private final List<OperationBean> ops = new ArrayList<>();
	@EJB
	private Searcher db;
	
	public HomeView() {
		ops.add(new OperationBean("Browse", "browse", "Browse DUBs and their potential substrates in DUBase graphically", "browse-preview.png"));
		ops.add(new OperationBean("Search", "search", "Query DUBase with your DUB or substrate of interest", "search-preview.png"));		
		ops.add(new OperationBean("Analyze", "analyze", "Analyze global results from all the experiments in DUBase", "string-preview.png"));
		ops.add(new OperationBean("Thresholds", "settings", "Configure your quality thresholds for each experiment", "volcano-preview.png"));
	}
	
	public List<OperationBean> getOps() {
		return ops;
	}
	
	public List<Experiment> getExperiments() {
		List<Experiment> exps = db.findExperiments(); 
		exps.sort(new Comparator<Experiment>() {
			@Override
			public int compare(Experiment o1, Experiment o2) {
				return o2.getExpDate().compareTo(o1.getExpDate());
			}
		});
		return exps;
	}
}
