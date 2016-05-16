package es.ehubio.io;

import java.io.File;

public class FileUtils {
	public static String concat( String parent, String child) {
		return new File(parent, child).getAbsolutePath();
	}
}
