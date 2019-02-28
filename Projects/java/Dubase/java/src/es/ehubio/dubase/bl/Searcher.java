package es.ehubio.dubase.bl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.ehubio.dubase.bl.beans.SearchBean;

@LocalBean
@Stateless
public class Searcher {
	@PersistenceContext
	private EntityManager em;
	
	public Set<SearchBean> search(String gene) {
		Set<SearchBean> set = new LinkedHashSet<>();
		set.addAll(searchEnzyme(gene));
		set.addAll(searchSubstrate(gene));
		return set;
	}

	public List<SearchBean> searchSubstrate(String gene) {
		return null;
	}
	
	public List<SearchBean> searchEnzyme(String gene) {
		return null;
	}
}
