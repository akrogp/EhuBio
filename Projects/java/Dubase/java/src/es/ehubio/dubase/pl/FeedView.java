package es.ehubio.dubase.pl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import es.ehubio.dubase.bl.Database;
import es.ehubio.dubase.bl.ExperimentBean;

@Named
@SessionScoped
public class FeedView implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private Database db;

	public void saveExperiment(ExperimentBean exp) throws IOException {
		db.saveExperiment(exp);
	}
	
	public void saveUsp11() throws IOException {
		ExperimentBean exp = new ExperimentBean();
		exp.setContactName("Gorka Prieto");
		exp.setContactMail("gorka.prieto@ehu.eus");
		exp.setContactAffiliation("UPV/EHU");
		exp.setDate(new Date());
		exp.setMethod("siUSP11 - MaxQuant+Perseus");
		exp.setEnzyme("USP11");
		exp.setEvidencesPath("/home/gorka/Proyectos/EhuBio/Projects/java/Dubase/data/USP11.csv");
		saveExperiment(exp);
	}
}
