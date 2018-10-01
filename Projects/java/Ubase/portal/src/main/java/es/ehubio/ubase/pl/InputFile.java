package es.ehubio.ubase.pl;

import org.primefaces.model.UploadedFile;

public class InputFile {
	private String name;
	private UploadedFile file;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}
}
