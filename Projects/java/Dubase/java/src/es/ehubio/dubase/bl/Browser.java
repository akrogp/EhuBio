package es.ehubio.dubase.bl;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import es.ehubio.dubase.bl.beans.Flare;
import es.ehubio.dubase.dl.entities.Clazz;
import es.ehubio.dubase.dl.entities.Enzyme;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.Superfamily;

@LocalBean
@Stateless
@Path("/browse")
public class Browser {
	@PersistenceContext
	private EntityManager em;
	private static final double DEFAULT_SIZE = 1;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("flare.json")
	public Flare getFlare() {
		List<String> db = getEnzymesWithEvidences();		
		Flare flare = new Flare("DUBs");
		for( Clazz classBean : getClasses() ) {
			Flare clazz = new Flare(classBean.getName());
			flare.addChild(clazz);
			for( Superfamily sfBean : getSuperfamiliesByClass(classBean.getId()) ) {
				Flare sf = new Flare(sfBean.getShortname());
				sf.setDesc(sfBean.getName());
				clazz.addChild(sf);
				for( Enzyme enzymeBean : getEnzymesBySuperfamily(sfBean.getId()) ) {
					Flare enzyme = new Flare(enzymeBean.getGene());
					enzyme.setDesc(enzymeBean.getDescription());
					enzyme.setSize(DEFAULT_SIZE);
					enzyme.setDb(db.contains(enzymeBean.getGene()));
					sf.addChild(enzyme);
					/*if("USP1".equals(enzymeBean.getGene())) {
						enzyme.setSize(0);
						Flare proteomics = new Flare("Omics");
						proteomics.setSize(DEFAULT_SIZE);
						proteomics.setDb(true);
						Flare manual = new Flare("Manual");
						manual.setSize(DEFAULT_SIZE);
						manual.setDb(true);
						enzyme.addChild(proteomics);
						enzyme.addChild(manual);
					}*/
				}
			}
		}		
		return flare;
	}
	
	public List<Clazz> getClasses() {
		return em.createNamedQuery("Clazz.findAll", Clazz.class).getResultList();
	}
	
	public List<Superfamily> getSuperfamiliesByClass(int classId) {
		return em
			.createQuery("SELECT s FROM Superfamily s WHERE s.clazz.id = :classId", Superfamily.class)
			.setParameter("classId", classId)
			.getResultList();
	}
	
	public List<Enzyme> getEnzymesBySuperfamily(int familyId) {
		return em
			.createQuery("SELECT e FROM Enzyme e WHERE e.superfamilyBean.id = :familyId", Enzyme.class)
			.setParameter("familyId", familyId)
			.getResultList();
	}
	
	public List<String> getEnzymesWithEvidences() {
		return em
			.createQuery("SELECT DISTINCT e.enzymeBean.gene FROM Experiment e", String.class)
			.getResultList();
	}
	
	public List<Experiment> getExperiments() {
		return em.createNamedQuery("Experiment.findAll", Experiment.class).getResultList();
	}
}
