package es.ehubio.bl;

import java.io.File;
import java.io.Serializable;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.ehubio.ubase.dl.providers.Provider;

@LocalBean
@Stateless
public class Ubase implements Serializable {
	private static final long serialVersionUID = 1L;
	@PersistenceContext
	private EntityManager em;

	public void submit(Provider provider, File data) throws Exception {
		
	}
}
