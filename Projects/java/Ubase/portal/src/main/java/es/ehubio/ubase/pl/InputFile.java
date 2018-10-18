package es.ehubio.ubase.pl;

import org.primefaces.model.UploadedFile;

import es.ehubio.ubase.dl.providers.FileType;

public class InputFile {
	private final FileType type;
	private UploadedFile file;
	private boolean fixedName;
	
	public InputFile(FileType type) {
		this.type = type;
	}

	public String getName() {
		return type.getName();
	}
	
	public String getDstName() {
		return type.getDstName();
	}
	
	public String getDescription() {
		return type.getDescription();
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

	public boolean isLarge() {
		return type.isLarge();
	}
}
