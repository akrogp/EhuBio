package es.ehubio.dubase.bl;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import es.ehubio.dubase.bl.beans.ClassBean;
import es.ehubio.dubase.bl.beans.EnzymeBean;
import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.bl.beans.Flare;
import es.ehubio.dubase.bl.beans.SuperfamilyBean;
import es.ehubio.dubase.bl.beans.TreeBean;
import es.ehubio.dubase.dl.Clazz;
import es.ehubio.dubase.dl.Enzyme;
import es.ehubio.dubase.dl.Evidence;
import es.ehubio.dubase.dl.Superfamily;

@LocalBean
@Singleton
@Path("/browse")
public class Browser {
	@PersistenceContext
	private EntityManager em;
	private Flare flare;
	private TreeBean tree;
	private static final double DEFAULT_SIZE = 1;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("flare.json")
	public Flare getFlare() {
		if( flare != null )
			return flare;
		getTree();
		
		flare = new Flare("DUBs");
		for( ClassBean classBean : tree.getClassess() ) {
			Flare clazz = new Flare(classBean.getEntity().getName());
			if( classBean.getSuperfamilies().isEmpty() )
				clazz.setSize(DEFAULT_SIZE);
			flare.addChild(clazz);
			for( SuperfamilyBean sfBean : classBean.getSuperfamilies() ) {
				Flare sf = new Flare(sfBean.getEntity().getShortname());
				sf.setDesc(sfBean.getEntity().getName());
				if( sfBean.getEnzymes().isEmpty() )
					sf.setSize(DEFAULT_SIZE);
				clazz.addChild(sf);
				for( EnzymeBean enzymeBean : sfBean.getEnzymes() ) {
					Flare enzyme = new Flare(enzymeBean.getEntity().getGene());
					enzyme.setDesc(enzymeBean.getEntity().getDescription());
					if( enzymeBean.getSubstrates().isEmpty() )
						enzyme.setSize(DEFAULT_SIZE);
					sf.addChild(enzyme);
					for( EvidenceBean evBean : enzymeBean.getSubstrates() ) {
						Flare subs = new Flare(evBean.getGenes().get(0));
						subs.setDesc(evBean.getDescriptions() == null || evBean.getDescriptions().isEmpty() ? null : evBean.getDescriptions().get(0));
						subs.setSize(1);//Math.round(evBean.getMapScores().get(Score.FOLD_CHANGE.ordinal())));
						enzyme.addChild(subs);
					}
				}
			}
		}
		
		return flare;
	}
	
	public TreeBean getTree() {
		if( tree != null )
			return tree;
		tree = new TreeBean();
		for( Clazz clazz : getClasses() ) {
			ClassBean classBean = new ClassBean(clazz);
			for( Superfamily family : getSuperfamiliesByClass(clazz.getId()) ) {
				SuperfamilyBean superfamilyBean = new SuperfamilyBean(family);
				for( Enzyme enzyme : getEnzymesBySuperfamily(family.getId()) ) {
					EnzymeBean enzymeBean = new EnzymeBean(enzyme);
					enzymeBean.getSubstrates().addAll(getSubstrateByEnzyme(enzyme.getId()));
					superfamilyBean.getEnzymes().add(enzymeBean);
				}
				classBean.getSuperfamilies().add(superfamilyBean);
			}
			tree.getClassess().add(classBean);
		}
		return tree;
	}
	
	@SuppressWarnings("unchecked")
	public List<Clazz> getClasses() {
		return em.createNamedQuery("Clazz.findAll").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Superfamily> getSuperfamiliesByClass(int classId) {
		return em
			.createQuery("SELECT s FROM Superfamily s WHERE s.clazz.id = :classId")
			.setParameter("classId", classId)
			.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Enzyme> getEnzymesBySuperfamily(int familyId) {
		return em
			.createQuery("SELECT e FROM Enzyme e WHERE e.superfamilyBean.id = :familyId")
			.setParameter("familyId", familyId)
			.getResultList();
	}
	
	private List<EvidenceBean> getSubstrateByEnzyme(int enzymeId) {
		List<Evidence> evidences = em
			.createQuery("SELECT e FROM Evidence e WHERE e.experimentBean.enzymeBean.id = :enzymeId", Evidence.class)
			.setParameter("enzymeId", enzymeId)
			.getResultList();
		List<EvidenceBean> results = DbUtils.fillEvidences(em, evidences);
		return results;
	}
}
