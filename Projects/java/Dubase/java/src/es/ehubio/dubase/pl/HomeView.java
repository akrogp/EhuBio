package es.ehubio.dubase.pl;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class HomeView {
	private final List<OperationBean> ops = new ArrayList<>();
	
	public HomeView() {
		ops.add(new OperationBean("Search", "search", "Search for DUBs or their substrates by gene name"));
		ops.add(new OperationBean("Browse", "browse", "Browse DUBs and their substrates graphically"));
		ops.add(new OperationBean("Analyze", "index", "Analyze global results from all the experiments in DUBase"));
	}
	
	public List<OperationBean> getOps() {
		return ops;
	}
}
