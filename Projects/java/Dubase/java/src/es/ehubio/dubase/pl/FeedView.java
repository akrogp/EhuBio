package es.ehubio.dubase.pl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import es.ehubio.dubase.bl.Importer;
import es.ehubio.dubase.bl.beans.ExperimentBean;

@Named
@SessionScoped
public class FeedView implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private Importer db;
	private static final Logger LOG = Logger.getLogger(FeedView.class.getName());

	public void saveExperiment(ExperimentBean exp) throws IOException {
		db.saveExperiment(exp);
	}
	
	public void saveExamples() {
		//String[] genes = {"USP1", "USP7", "USP9X", "USP11"};
		String[] genes = {"USP11"};
		for( String gene : genes )
			try {
				saveTest(gene);
				LOG.info("Saved " + gene);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	private void saveTest(String geneName) throws IOException {
		ExperimentBean exp = new ExperimentBean();
		exp.setContactName("Gorka Prieto");
		exp.setContactMail("gorka.prieto@ehu.eus");
		exp.setContactAffiliation("UPV/EHU");
		exp.setDate(new Date());
		exp.setMethod(String.format("si%s - MaxQuant+Perseus", geneName));
		exp.setEnzyme(geneName);
		//exp.setEvidencesPath(String.format("/home/gorka/Proyectos/EhuBio/Projects/java/Dubase/data/%s.csv", geneName));
		exp.setEvidencesPath(String.format("/home/gorka/MyProjects/EhuBio/Projects/java/Dubase/data/%s.csv", geneName));
		saveExperiment(exp);
	}
}
