package es.ehubio.ubase.dl.providers;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;

public interface Dao {
	List<FileType> getInputFiles();
	List<String> getSamples(File data);
	void persist(EntityManager em, File data);
}
