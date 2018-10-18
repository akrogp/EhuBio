package es.ehubio.ubase.dl.providers;

import java.io.File;

public class NameFileType extends FileType {
	private final String[] extensions;
	
	public NameFileType(String name, String desc, boolean large, String... extensions) {
		super(name, desc, large);
		this.extensions = extensions;
	}

	@Override
	public boolean checkSignature(File file) {
		String name = file.getName();
		if( extensions == null || extensions.length == 0 )
			return name.equalsIgnoreCase(getName());
		name = name.toLowerCase();
		for( String ext : extensions ) {
			ext = ext.toLowerCase();
			if( name.endsWith(ext) || name.endsWith(ext+".gz"))
				return true;
		}
		return false;
	}
}
