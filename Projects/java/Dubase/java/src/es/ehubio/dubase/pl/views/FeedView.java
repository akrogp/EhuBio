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
		/*String[] experiments = {
			"USP1", "USP7", "USP9X", "USP11", "USP42",	// Ramirez et al.
			"USP14"		// Liu et al.
		};*/
		String[] experiments = {"USP1"};
		for( String experiment : experiments )
			try {
				db.saveUgoProteomics(experiment);
				LOG.info("Saved " + experiment);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public void saveUgoCurated() {
		//final String xlsName = "DUB substrates.v5.xlsx";
		//final String xlsName = "DUB substrates Nago.xlsx";
		String[] experiments = {"USP14.xlsx"};
		for( String experiment : experiments )
			try {
				int count = db.saveUgoCurated(experiment);
				LOG.info(String.format("Saved %d entries from %s", count, experiment));
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
