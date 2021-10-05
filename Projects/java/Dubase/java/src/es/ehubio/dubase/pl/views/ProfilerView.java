package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import es.ehubio.dubase.bl.Searcher;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.io.CsvUtils;
import es.ehubio.io.UrlBuilder;

@Named
@SessionScoped
public class ProfilerView implements Serializable {
	private static final long serialVersionUID = 1L;
	private String gene;
	@EJB
	private Searcher db;
	@Inject
	private PrefView prefs;
	
	public String getGene() {
		return gene;
	}
	
	public void setGene(String gene) {
		this.gene = gene;
	}
	
	/*public void gprofiler() {
		redirect(db.searchEnzyme(getGene(), prefs.getThresholds()));
	}*/
	
	public static void redirect(List<Evidence> evidences) {
		List<String> genes = new ArrayList<>();
		for( Evidence ev : evidences )
			genes.addAll(ev.getGenes());
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			ec.redirect( new UrlBuilder("https", "biit.cs.ut.ee", "gprofiler/gost")
				.param("organism", "hsapiens")
				.param("query", CsvUtils.getCsv('\n', genes.toArray()))
				.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
