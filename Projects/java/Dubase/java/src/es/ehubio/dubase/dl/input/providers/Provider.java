package es.ehubio.dubase.dl.input.providers;

import java.util.List;

import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.Experiment;

public interface Provider {
	List<Evidence> loadEvidences(String dir, Experiment exp) throws Exception;
}
