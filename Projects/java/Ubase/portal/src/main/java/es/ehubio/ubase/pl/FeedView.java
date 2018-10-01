package es.ehubio.ubase.pl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import es.ehubio.bl.Ubase;
import es.ehubio.ubase.Locator;
import es.ehubio.ubase.dl.entities.Experiment;
import es.ehubio.ubase.dl.providers.FileType;
import es.ehubio.ubase.dl.providers.Provider;

@Named
@SessionScoped
public class FeedView extends BaseView implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<InputFile> files;
	private final Experiment experiment = new Experiment();
	private int step;
	private File directory;
	@EJB
	private Ubase ubase;

	public Experiment getExperiment() {
		return experiment;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}
	
	public void nextStep() throws IOException {
		switch (step) {
			case 0: step++; break;
			case 1:
				if( isUploadReady() && uploadFiles() )
					step++;
				else
					showError("Required files missing");
				break;
		}
	}
	
	private boolean uploadFiles() throws IOException {
		if( directory == null )
			directory = Files.createTempDirectory(Paths.get("/tmp"), "ubase-").toFile();
		for( InputFile inputFile : getProviderFiles() ) 
			FileUtils.copyInputStreamToFile(inputFile.getFile().getInputstream(), new File(directory, inputFile.getName()));
		return true;
	}
	
	public Provider getProvider() {
		return Locator.getProviders().get(0);
	}

	public String getProviderName() {
		return getProvider().getName();
	}

	public List<InputFile> getProviderFiles() {
		if( files == null ) {
			files = new ArrayList<>();
			for( FileType type : getProvider().getInputFiles() ) {
				InputFile file = new InputFile();
				file.setName(type.getName());
				files.add(file);
			}
		}
		return files;
	}
	
	public boolean isUploadReady() {
		if( files == null )
			return false;
		for( InputFile file : getProviderFiles() )
			if( !checkName(file) )
				return false;
		return true;
	}

	private boolean checkName(InputFile file) {
		if( file.getFile() == null )
			return false;
		String name1 = FilenameUtils.removeExtension(file.getFile().getFileName());
		String name2 = FilenameUtils.removeExtension(file.getName());
		return name1.equalsIgnoreCase(name2);
	}
	
	public void submit() {
		try {
			ubase.submit(getProvider(), directory);
			showInfo("Submitted!");
		} catch (Exception e) {
			showError(e.getMessage());			
		}
		finish();
		step++;
	}
	
	public String finish() {
		invalidateSession();
		return "/index.xhtml?faces-redirect=true";
	}
	
	
}
