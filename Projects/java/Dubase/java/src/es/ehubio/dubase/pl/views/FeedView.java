package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import es.ehubio.dubase.bl.Importer;

@Named
@SessionScoped
public class FeedView implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private Importer db;
	private static final Logger LOG = Logger.getLogger(FeedView.class.getName());

	public void saveExperiment(String inputId) throws Exception {
		db.saveExperiment(inputId);
	}
	
	public void saveExamples() {
		//String[] genes = {"USP1", "USP7", "USP9X", "USP11", "USP42"};
		//String[] genes = {"USP1"};
		String[] genes = {"USP7", "USP9X", "USP42"};
		for( String gene : genes )
			try {
				saveExperiment(gene);
				LOG.info("Saved " + gene);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
