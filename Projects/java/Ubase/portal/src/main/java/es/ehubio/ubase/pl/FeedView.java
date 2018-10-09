package es.ehubio.ubase.pl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import es.ehubio.ubase.Constants;
import es.ehubio.ubase.Locator;
import es.ehubio.ubase.bl.Ubase;
import es.ehubio.ubase.dl.input.Condition;
import es.ehubio.ubase.dl.input.Metadata;
import es.ehubio.ubase.dl.providers.FileType;
import es.ehubio.ubase.dl.providers.Provider;

@Named
@SessionScoped
public class FeedView extends BaseView implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<InputFile> files;
	private int step;
	private Metadata metadata;
	private int nConditions;
	private List<InputSample> samples;
	private List<Integer> numbers; 
	private File directory;
	@EJB
	private Ubase ubase;
	
	public Metadata getMetadata() {
		return metadata;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}
	
	public void nextStep() throws IOException {
		switch (step) {
			case 0:
				if( stepUpload() )
					step++;
				break;
			case 1:
				stepMetadata();
				step++;
				break;
			default:
				step++;
				break;
		}
	}
	
	private boolean stepUpload() {
		if( !isUploadReady() ) {
			showError("Required files missing");
			return false;
		}
		if( !uploadFiles() ) {
			showError("Problem uploading files, try again later");
			return false;
		}
		String sig;
		if( (sig=checkSignatures()) != null ) {
			showError("Invalid file signature: " + sig);
			return false;
		}
		buildConditions();
		return true;
	}
	
	private void buildConditions() {
		metadata = new Metadata();
		samples = new ArrayList<>();
		for( String name : getProvider().getSamples(directory) ) {
			InputSample sample = new InputSample();
			sample.setName(name);
			samples.add(sample);
		}
		if( samples.isEmpty() ) {
			setnConditions(0);
			return;
		}
		setnConditions(1);
		numbers = new ArrayList<>(samples.size());
		for( int i = 0; i < samples.size(); i++ )
			numbers.add(i+1);
	}

	private void stepMetadata() {
	}
	
	private String checkSignatures() {
		for( FileType inputFile : getProvider().getInputFiles() ) {
			File file = new File(directory, inputFile.getName());
			if( !inputFile.checkSignature(file) )
				return inputFile.getName();
		}
		return null;
	}

	private boolean uploadFiles() {
		try {
			if( directory == null ) {
				//directory = Files.createTempDirectory(Paths.get("/tmp"), "ubase-").toFile();
				directory = new File(Locator.getConfiguration().getSubmissionPath(), Constants.EXP_PREFIX+System.currentTimeMillis());
				directory.mkdirs();
			}
			for( InputFile inputFile : getProviderFiles() ) 
				FileUtils.copyInputStreamToFile(inputFile.getFile().getInputstream(), new File(directory, inputFile.getName()));
			return true;
		} catch (Exception e) {
			return false;
		}
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
			for( InputSample sample : samples ) {
				Condition condition = metadata.getConditions().get(sample.getCondition()); 
				condition.getReplicas().add(sample.getName());
			}
			ubase.submit(metadata, getProvider(), directory);
			showInfo("Submitted! You will be notified once the submission is approved. Write down this submission number: " + directory.getName());
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.getMessage());			
		}
		finish();
		step++;
	}
	
	public String cancel() throws IOException {
		if( directory != null )
			FileUtils.deleteDirectory(directory);
		return finish();
	}
	
	public String finish() {
		invalidateSession();
		return "/index.xhtml?faces-redirect=true";
	}

	public int getnConditions() {
		return nConditions;
	}

	public void setnConditions(int nConditions) {
		this.nConditions = nConditions;
		List<Condition> conditions = new ArrayList<>(nConditions);
		for( int i = 0; i < nConditions; i++ ) {
			Condition condition = new Condition();
			//condition.setName("Condition "+(i+1));
			condition.setReplicas(new ArrayList<>());
			conditions.add(condition);
		}
		metadata.setConditions(conditions);
		int replicas = samples.size() / nConditions;
		for( int i = 0, r = 0; i < samples.size(); r++)
			for( int j = 0; j < replicas && i < samples.size(); j++ )
				samples.get(i++).setCondition(r);
	}
	
	public List<Integer> getNumbers() {
		return numbers;
	}
	
	public List<InputSample> getSamples() {
		return samples;
	}
}