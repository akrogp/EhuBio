package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import es.ehubio.dubase.bl.Searcher;
import es.ehubio.dubase.dl.entities.Evidence;

@Named
@SessionScoped
public class StringView implements Serializable {
	private static final long serialVersionUID = 1L;
	private String gene;
	@EJB
	private Searcher db;
	@Inject
	private PrefView prefs;
	private String genes;
	private String organism;
	
	public String getGene() {
		return gene;
	}
	
	public void setGene(String gene) {
		this.gene = gene;
	}
	
	/*public String string() {
		return redirect(db.searchEnzyme(getGene(), prefs.getThresholds()));
	}*/
	
	public String redirect(List<Evidence> evidences) {
		genes = evidences.stream()
			.flatMap(e->e.getAmbiguities().stream())
			.map(a->a.getProteinBean().getGeneBean().getName())
			.distinct()
			.collect(Collectors.joining("\n"));
		organism = evidences.stream()
			.map(e->e.getExperimentBean().getCellBean().getTaxonBean().getSciName())
			.distinct()
			.collect(Collectors.joining("\n"));
		return "string";
	}

	public String getGenes() {
		return genes;
	}
	
	public String getOrganism() {
		return organism;
	}
}
