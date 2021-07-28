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
		String[] genes = {"USP1", "USP7", "USP9X", "USP11", "USP42"};
		//String[] genes = {"USP7"};
		for( String gene : genes )
			try {
				db.saveUgoProteomics(gene);
				LOG.info("Saved " + gene);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public void saveUgoCurated() {
		final String xlsName = "DUB substrates.v5.xlsx";
		try {
			int count = db.saveUgoCurated(xlsName);
			LOG.info(String.format("Saved %d entries from %s", count, xlsName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
