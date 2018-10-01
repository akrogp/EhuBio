package es.ehubio.ubase.dl.providers;

import java.util.List;

public interface Provider {
	String getName();
	String getDescription();
	List<FileType> getInputFiles();
}
