package es.ehubio.bl;

import java.io.File;
import java.io.Serializable;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.ehubio.dl.input.Metadata;
import es.ehubio.dl.input.Metafile;
import es.ehubio.ubase.dl.providers.Provider;

@LocalBean
@Stateless
public class Ubase implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String META_FILE = "metadata.xml";
	@PersistenceContext
	private EntityManager em;

	public void submit(Metadata metadata, Provider provider, File data) throws Exception {
		Metafile.save(metadata, new File(data, META_FILE));
	}
}
