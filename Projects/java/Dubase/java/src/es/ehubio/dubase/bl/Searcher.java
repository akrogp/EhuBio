package es.ehubio.dubase.bl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.dl.entities.Enzyme;
import es.ehubio.dubase.dl.entities.Evidence;

@LocalBean
@Stateful
public class Searcher {
	@PersistenceContext
	private EntityManager em;
	private Thresholds thresholds = new Thresholds();
	
	public Thresholds getThresholds() {
		return thresholds;
	}
	public void setThresholds(Thresholds thresholds) {
		this.thresholds = thresholds;
	}
	
	public Set<EvidenceBean> search(String gene) {
		Set<EvidenceBean> set = new LinkedHashSet<>();
		set.addAll(searchEnzyme(gene));
		set.addAll(searchSubstrate(gene));
		return set;
	}

	public List<EvidenceBean> searchSubstrate(String gene) {
		List<Evidence> evidences = em
			.createQuery("SELECT a.evidenceBean FROM Ambiguity a WHERE a.substrateBean.gene = :gene", Evidence.class)
			.setParameter("gene", gene)
			.getResultList();
		List<EvidenceBean> evBeans = DbUtils.buildEvidences(evidences);
		return DbUtils.filter(evBeans, thresholds);
	}
	
	public List<EvidenceBean> searchEnzyme(String gene) {
		List<Evidence> evidences = em
			.createQuery("SELECT e FROM Evidence e WHERE e.experimentBean.enzymeBean.gene = :gene", Evidence.class)
			.setParameter("gene", gene)
			.getResultList();
		List<EvidenceBean> evBeans = DbUtils.buildEvidences(evidences);
		return DbUtils.filter(evBeans, thresholds);
	}
	
	public List<Enzyme> searchEnzymesWithData() {
		return em
			.createQuery("SELECT DISTINCT e.experimentBean.enzymeBean FROM Evidence e", Enzyme.class)
			.getResultList();
	}
}
