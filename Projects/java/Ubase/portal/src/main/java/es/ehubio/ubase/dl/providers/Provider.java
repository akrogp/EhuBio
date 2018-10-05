package es.ehubio.ubase.dl.providers;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;

public interface Provider {
	String getName();
	String getDescription();
	List<FileType> getInputFiles();
	List<String> getSamples(File data);
	void persist(EntityManager em, File data);
}
