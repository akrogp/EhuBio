package es.ehubio.dubase.pl.views;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import es.ehubio.dubase.pl.beans.OperationBean;

@Named
@ApplicationScoped
public class HomeView {
	private final List<OperationBean> ops = new ArrayList<>();
	
	public HomeView() {
		ops.add(new OperationBean("Search", "search", "Query DUBase with your DUB or substrate of interest", "search-preview.png"));
		ops.add(new OperationBean("Browse", "browse", "Browse DUBs and their potential substrates in DUBase graphically", "browse-preview.png"));
		ops.add(new OperationBean("Analyze", "analyze", "Analyze global results from all the experiments in DUBase", "volcano-preview.png"));
	}
	
	public List<OperationBean> getOps() {
		return ops;
	}
}
