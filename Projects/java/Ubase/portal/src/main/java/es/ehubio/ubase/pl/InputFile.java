package es.ehubio.ubase.pl;

import org.primefaces.model.UploadedFile;

public class InputFile {
	private String name;
	private UploadedFile file;
	private boolean fixedName;

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

	public boolean isFixedName() {
		return fixedName;
	}

	public void setFixedName(boolean fixedName) {
		this.fixedName = fixedName;
	}
}
