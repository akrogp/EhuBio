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
		ops.add(new OperationBean("Search", "search", "Search for DUBs or their substrates by gene name", "search-preview.png"));
		ops.add(new OperationBean("Browse", "browse", "Browse DUBs and their substrates graphically", "browse-preview.png"));
		ops.add(new OperationBean("Analyze", "analyze", "Analyze global results from all the experiments in DUBase", "gprofiler-preview.png"));
	}
	
	public List<OperationBean> getOps() {
		return ops;
	}
}
