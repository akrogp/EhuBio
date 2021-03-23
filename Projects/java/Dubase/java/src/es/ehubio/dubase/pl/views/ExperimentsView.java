package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import es.ehubio.dubase.bl.Browser;
import es.ehubio.dubase.dl.entities.Experiment;

@Named
@RequestScoped
public class ExperimentsView implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private Browser db;
	
	public List<Experiment> getExperiments() {
		return db.getExperiments();
	}
}
