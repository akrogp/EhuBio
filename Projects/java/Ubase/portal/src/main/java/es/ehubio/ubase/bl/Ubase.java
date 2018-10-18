package es.ehubio.ubase.bl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.ehubio.ubase.bl.stats.ExpStats;
import es.ehubio.ubase.bl.stats.ModStats;
import es.ehubio.ubase.bl.stats.UbStats;
import es.ehubio.ubase.dl.entities.Experiment;
import es.ehubio.ubase.dl.entities.Modification;
import es.ehubio.ubase.dl.entities.Taxon;
import es.ehubio.ubase.dl.providers.ModificationType;

@LocalBean
@Stateless
public class Ubase implements Serializable {
	private static final long serialVersionUID = 1L;
	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<Taxon> queryTaxon(String query) {
		if( query != null && !query.isEmpty() )
			return em.createQuery("SELECT t FROM Taxon t WHERE sciName LIKE :q ORDER BY ISNULL(commonName),sciName")
				.setParameter("q", "%"+query+"%")
				.setMaxResults(10)
				.getResultList();
		List<Taxon> taxons = new ArrayList<>();
		int[] ids = {
			9606,	// Human
			9544,	// Rhesus macaque
			10090,	// Mouse
			10116,	// Rat
			9031,	// Chicken
			7955,	// Zebrafish
			7227,	// Fruit fly
		};
		for( int id : ids )
			taxons.add(em.find(Taxon.class, id));
		return taxons;
	}
	
	@SuppressWarnings("unchecked")
	public UbStats queryStats() {
		UbStats stats = new UbStats();
		stats.setExperiments(new ArrayList<>());
		List<Experiment> exps = em.createNamedQuery("Experiment.findAll").getResultList();
		for( Experiment exp : exps ) {
			ExpStats expStat = new ExpStats();
			expStat.setExperiment(exp);
			if( stats.getUpdated() == null || stats.getUpdated().before(exp.getPubDate()) )
				stats.setUpdated(exp.getPubDate());
			ModStats modStat = new ModStats();
			modStat.setModification(em.find(Modification.class, ModificationType.GLYGLY.getId()));
			long count = (Long)em.createQuery(
					"SELECT COUNT(mev) FROM ModificationEvidence mev WHERE mev.peptideEvidenceBean.experimentBean = :exp AND mev.modificationBean = :mod")
					.setParameter("exp", exp)
					.setParameter("mod", modStat.getModification())
					.getSingleResult();
			modStat.setCount(count);
			expStat.getModStats().put(modStat.getModification().getId(), modStat);
			stats.getExperiments().add(expStat);
			stats.setModifications(stats.getModifications()+count);
		}
		return stats;
	}
}
