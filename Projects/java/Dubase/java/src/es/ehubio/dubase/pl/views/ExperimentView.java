package es.ehubio.dubase.pl.views;

import java.io.File;
import java.io.Serializable;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.SupportingFile;

@Named
@SessionScoped
public class ExperimentView implements Serializable {
	private static final long serialVersionUID = 1L;
	private Experiment entity;
	@Resource(name="es.ehubio.dubase.inputDir")
	private String inputPath;

	public Experiment getEntity() {
		return entity;
	}

	public void setEntity(Experiment entity) {
		this.entity = entity;
	}
	
	public String showExperiment(Experiment exp) {
		setEntity(exp);
		return "experiment";
	}
	
	public void download(SupportingFile file) {
		File dir = new File(inputPath, file.getExperimentBean().getFmtId());
		File data = new File(dir, file.getName());
		System.out.println("path: " + data.getAbsolutePath());
		if( !data.isFile() ) {
			PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "File not found."));
			return;
		}
		try(JsfFileResponse resp = new JsfFileResponse()) {			
			resp.sendFile("application/octet-stream", data);
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
}
