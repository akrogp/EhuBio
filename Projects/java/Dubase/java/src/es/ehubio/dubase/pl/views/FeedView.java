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
			//"USP14",		// Liu et al. (moved to manual)
			"USP30"			// Phu et al.
		};
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
			//"Gorka.xlsx",	// Pruebas
			"Nerea/USP7.xlsx",
			"Nerea/USP14-1.xlsx",
			"Nerea/USP14-2.xlsx",
			"Nerea/UCHL5.xlsx",
			//"Nerea/USP32.xlsx",	// (taken from Nago Western-based list)
			"Juanma/20211101-Juanma.fixed.xlsx",
			//"Nago/20211102-Nago.fixed.xlsx",	// (splitted for debugging and performance issues)			
			"Nago/20211102-Nago.fixed.BAP1.xlsx",
			"Nago/20211102-Nago.fixed.BRCC3.xlsx",
			"Nago/20211102-Nago.fixed.COPS5.xlsx",
			"Nago/20211102-Nago.fixed.COPS6.xlsx",
			"Nago/20211102-Nago.fixed.CYLD.xlsx",
			"Nago/20211102-Nago.fixed.EIF3F.xlsx",
			"Nago/20211102-Nago.fixed.EIF3H.xlsx",
			"Nago/20211102-Nago.fixed.MINDY4.xlsx",
			"Nago/20211102-Nago.fixed.MYSM1.xlsx",
			"Nago/20211102-Nago.fixed.OTULIN.xlsx",
			"Nago/20211102-Nago.fixed.PRPF8.xlsx",
			"Nago/20211102-Nago.fixed.PSMD14.xlsx",
			"Nago/20211102-Nago.fixed.STAMBPL1.xlsx",
			"Nago/20211102-Nago.fixed.STAMBP.xlsx",
			"Nago/20211102-Nago.fixed.UCHL1.xlsx",
			"Nago/20211102-Nago.fixed.UCHL3.xlsx",
			"Nago/20211102-Nago.fixed.UCHL5.xlsx",
			"Nago/20211102-Nago.fixed.USP10.xlsx",
			"Nago/20211102-Nago.fixed.USP11.xlsx",
			"Nago/20211102-Nago.fixed.USP12.xlsx",
			"Nago/20211102-Nago.fixed.USP13.xlsx",
			"Nago/20211102-Nago.fixed.USP14.xlsx",
			"Nago/20211102-Nago.fixed.USP15.xlsx",
			"Nago/20211102-Nago.fixed.USP16.xlsx",
			"Nago/20211102-Nago.fixed.USP17L2.xlsx",
			"Nago/20211102-Nago.fixed.USP18.xlsx",
			"Nago/20211102-Nago.fixed.USP19.xlsx",
			"Nago/20211102-Nago.fixed.USP1.xlsx",
			"Nago/20211102-Nago.fixed.USP20.xlsx",
			"Nago/20211102-Nago.fixed.USP21.xlsx",
			"Nago/20211102-Nago.fixed.USP22.xlsx",
			"Nago/20211102-Nago.fixed.USP24.xlsx",
			"Nago/20211102-Nago.fixed.USP25.xlsx",
			"Nago/20211102-Nago.fixed.USP26.xlsx",
			"Nago/20211102-Nago.fixed.USP27X.xlsx",
			"Nago/20211102-Nago.fixed.USP28.xlsx",
			"Nago/20211102-Nago.fixed.USP29.xlsx",
			"Nago/20211102-Nago.fixed.USP2.xlsx",
			"Nago/20211102-Nago.fixed.USP30.xlsx",
			"Nago/20211102-Nago.fixed.USP32.xlsx",
			"Nago/20211102-Nago.fixed.USP33.xlsx",
			"Nago/20211102-Nago.fixed.USP34.xlsx",
			"Nago/20211102-Nago.fixed.USP35.xlsx",
			"Nago/20211102-Nago.fixed.USP36.xlsx",
			"Nago/20211102-Nago.fixed.USP37.xlsx",
			"Nago/20211102-Nago.fixed.USP38.xlsx",
			"Nago/20211102-Nago.fixed.USP39.xlsx",
			"Nago/20211102-Nago.fixed.USP3.xlsx",
			"Nago/20211102-Nago.fixed.USP40.xlsx",
			"Nago/20211102-Nago.fixed.USP42.xlsx",
			"Nago/20211102-Nago.fixed.USP43.xlsx",
			"Nago/20211102-Nago.fixed.USP44.xlsx",
			"Nago/20211102-Nago.fixed.USP46.xlsx",
			"Nago/20211102-Nago.fixed.USP47.xlsx",
			"Nago/20211102-Nago.fixed.USP48.xlsx",
			"Nago/20211102-Nago.fixed.USP49.xlsx",
			"Nago/20211102-Nago.fixed.USP4.xlsx",
			"Nago/20211102-Nago.fixed.USP50.xlsx",
			"Nago/20211102-Nago.fixed.USP51.xlsx",
			"Nago/20211102-Nago.fixed.USP52.xlsx",
			"Nago/20211102-Nago.fixed.USP53.xlsx",
			"Nago/20211102-Nago.fixed.USP5.xlsx",
			"Nago/20211102-Nago.fixed.USP6.xlsx",
			"Nago/20211102-Nago.fixed.USP7.xlsx",
			"Nago/20211102-Nago.fixed.USP8.xlsx",
			"Nago/20211102-Nago.fixed.USP9X.xlsx"
		};
		for( String experiment : experiments )
			try {
				LOG.info(String.format("Saving %s ...", experiment));
				int count = db.saveUgoCurated(experiment);
				LOG.info(String.format("Saved %d entries from %s", count, experiment));
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
