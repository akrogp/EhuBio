package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import es.ehubio.dubase.bl.Browser;
import es.ehubio.dubase.dl.entities.Author;
import es.ehubio.dubase.dl.entities.Cell;
import es.ehubio.dubase.dl.entities.Enzyme;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.Method;
import es.ehubio.dubase.dl.entities.MethodSubtype;
import es.ehubio.dubase.dl.entities.MethodType;
import es.ehubio.dubase.dl.entities.Publication;
import es.ehubio.dubase.dl.entities.SupportingFile;
import es.ehubio.dubase.dl.entities.Taxon;

@Named
@ViewScoped
public class SubmissionView implements Serializable {
	private static final long serialVersionUID = 1L;
	private Boolean purified;
	private Boolean western;	
	private final Experiment entity;
	private List<String> dubs;
	@EJB
	private Browser db;
	private UIComponent uiPurified;
	private String accessions;
	private String genes;
	private String notes;
	
	public SubmissionView() {
		entity = new Experiment();
		entity.setAuthorBean(new Author());
		entity.setEnzymeBean(new Enzyme());
		entity.setCellBean(new Cell());
		entity.getCellBean().setTaxonBean(new Taxon());
		entity.setMethodBean(new Method());
		entity.getMethodBean().setType(new MethodType());
		entity.getMethodBean().setSubtype(new MethodSubtype());
		entity.setSupportingFiles(new ArrayList<>(1));
		entity.getSupportingFiles().add(new SupportingFile());
		entity.setPublications(new ArrayList<>(1));
		entity.getPublications().add(new Publication());
	}
	
	public Experiment getEntity() {
		return entity;
	}
	
	public void setDoi(String doi) {
		entity.getPublications().get(0).setDoi(doi);
	}
	
	public String getDoi() {
		return entity.getPublications().get(0).getDoi();
	}
	
	public List<String> getDubs() {
		if( dubs == null )
			dubs = db.getEnzymes();
		return dubs;
	}
	
	public List<String> completeOrganism(String query) {
		return db.queryTaxon(query).stream().map(t->t.getSciName()).collect(Collectors.toList());
	}

	public Boolean getPurified() {
		return purified;
	}

	public void setPurified(Boolean purified) {
		this.purified = purified;
		if( Boolean.FALSE.equals(purified) )
			FacesContext.getCurrentInstance().addMessage(uiPurified.getClientId(),
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "DUBase requires the use of purified material", null));
	}

	public Boolean getWestern() {
		return western;
	}

	public void setWestern(Boolean western) {
		this.western = western;
	}
	
	public boolean isManual() {
		return entity.getMethodBean().getType().getId() == es.ehubio.dubase.dl.input.MethodType.MANUAL.ordinal();
	}
	
	public boolean isProteomics() {
		return entity.getMethodBean().getType().getId() == es.ehubio.dubase.dl.input.MethodType.PROTEOMICS.ordinal();
	}
	
	public void submit() {
		
	}

	public UIComponent getUiPurified() {
		return uiPurified;
	}

	public void setUiPurified(UIComponent uiPurified) {
		this.uiPurified = uiPurified;
	}

	public String getGenes() {
		return genes;
	}

	public void setGenes(String genes) {
		this.genes = genes;
	}

	public String getAccessions() {
		return accessions;
	}

	public void setAccessions(String accessions) {
		this.accessions = accessions;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
