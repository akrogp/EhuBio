package es.ehubio.ubase.dl.providers;

import java.io.File;

public abstract class FileType {
	private final String name, description;
	private final boolean large;
	
	public FileType(String name, String description, boolean large) {
		this.name = name;
		this.description = description;
		this.large = large;
	}

	public String getName() {
		return name;
	}
	
	public String getDstName() {
		if( isLarge() )
			return getName()+".gz";
		return getName();
	}

	public String getDescription() {
		return description;
	}

	public boolean isLarge() {
		return large;
	}
	
	public abstract boolean checkSignature(File file);
}
