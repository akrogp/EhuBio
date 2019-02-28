package es.ehubio.dubase.bl;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.ehubio.dubase.bl.beans.ClassBean;
import es.ehubio.dubase.bl.beans.EnzymeBean;
import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.bl.beans.SuperfamilyBean;
import es.ehubio.dubase.bl.beans.TreeBean;
import es.ehubio.dubase.dl.Clazz;
import es.ehubio.dubase.dl.Enzyme;
import es.ehubio.dubase.dl.Evidence;
import es.ehubio.dubase.dl.Superfamily;

@LocalBean
@Stateless
public class Browser {
	@PersistenceContext
	private EntityManager em;
	
	public TreeBean getTree() {
		TreeBean tree = new TreeBean();
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
