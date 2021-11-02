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
	
	public void saveUgoProteomics() {
		String[] experiments = {
			"USP1", "USP7", "USP9X", "USP11", "USP42",	// Ramirez et al.
			//"USP14",		// Liu et al.
			"USP30"			// Phu et al.
		};
		//String[] experiments = {"USP14"};
		for( String experiment : experiments )
			try {
				db.saveUgoProteomics(experiment);
				LOG.info("Saved " + experiment);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public void saveUgoCurated() {
		String[] experiments = {
			//"DUB substrates Nago.xlsx",
			//"Gorka.xlsx",			
			//"USP14.xlsx",
			//"USP7.xlsx",
			"20210111-Juanma.fixed.xlsx"
		};
		for( String experiment : experiments )
			try {
				int count = db.saveUgoCurated(experiment);
				LOG.info(String.format("Saved %d entries from %s", count, experiment));
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
