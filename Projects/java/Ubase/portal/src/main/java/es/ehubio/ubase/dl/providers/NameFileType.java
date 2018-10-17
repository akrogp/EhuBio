package es.ehubio.ubase.dl.providers;

import java.io.File;

public class NameFileType extends FileType {
	private final String[] extensions;
	
	public NameFileType(String name, String desc, String... extensions) {
		super(name, desc);
		this.extensions = extensions;
	}

	@Override
	public boolean checkSignature(File file) {
		String name = file.getName();
		if( extensions == null || extensions.length == 0 )
			return name.equalsIgnoreCase(getName());
		name = name.toLowerCase();
		for( String ext : extensions )
			if( name.endsWith(ext.toLowerCase()) )
				return true;
		return false;
	}
}
