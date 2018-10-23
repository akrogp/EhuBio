package es.ehubio.ubase.dl.providers;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import es.ehubio.ubase.dl.entities.Experiment;
import es.ehubio.ubase.dl.entities.Protein;
import es.ehubio.ubase.dl.entities.Replica;

public interface Dao {
	List<FileType> getInputFiles();
	List<String> getSamples(File data);
	void persist(EntityManager em, Experiment exp, Map<String,Replica> replicas, Map<String, Protein> proteins, File data) throws Exception;
}
