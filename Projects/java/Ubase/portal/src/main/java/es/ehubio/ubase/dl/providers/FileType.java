package es.ehubio.ubase.dl.providers;

import java.io.File;

public abstract class FileType {
	private String name, description;
	
	public FileType() {
		this(null);
	}
	
	public FileType(String name) {
		this(name, null);
	}
	
	public FileType(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public abstract boolean checkSignature(File file);
}
