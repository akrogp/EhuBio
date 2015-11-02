package es.ehubio.mymrm.data;

import java.io.Serializable;

public class FastaFile implements Serializable {
	private static final long serialVersionUID = 1L;

	private String description;
	private String name;

	public FastaFile() {
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}