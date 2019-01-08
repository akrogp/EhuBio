package es.ehubio.dubase.bl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import es.ehubio.dubase.Constants;
import es.ehubio.dubase.dl.Author;
import es.ehubio.dubase.dl.Clazz;
import es.ehubio.dubase.dl.Enzyme;
import es.ehubio.dubase.dl.Evidence;
import es.ehubio.dubase.dl.Experiment;
import es.ehubio.dubase.dl.Method;
import es.ehubio.dubase.dl.Substrate;
import es.ehubio.dubase.dl.Superfamily;

@LocalBean
@Stateless
public class Database {
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
		return em
			.createQuery("SELECT new es.ehubio.dubase.bl.EvidenceBean(e.substrateBean.gene, e.foldChange, e.PValue) FROM Evidence e WHERE e.experimentBean.enzymeBean.id = :enzymeId AND ABS(e.foldChange) >= :foldChange AND e.PValue >= :pValue", EvidenceBean.class)
			.setParameter("enzymeId", enzymeId)
			.setParameter("foldChange", Constants.FOLD_CHANGE)
			.setParameter("pValue", Constants.P_VALUE)
			.getResultList();
	}
	
	public void saveExperiment(ExperimentBean experimentBean) throws IOException {
		Author author = new Author();
		author.setName(experimentBean.getContactName());
		author.setMail(experimentBean.getContactMail());
		author.setAffiliation(experimentBean.getContactAffiliation());
		em.persist(author);
		
		Method method = new Method();
		method.setOpenDescription(experimentBean.getMethod());
		em.persist(method);
		
		Enzyme enzyme = (Enzyme) em.createQuery("SELECT e FROM Enzyme e WHERE e.gene = :gene")
				.setParameter("gene", experimentBean.getEnzyme())
				.getSingleResult();
		
		Experiment experiment = new Experiment();
		experiment.setExpDate(experimentBean.getDate());
		experiment.setPubDate(new Date());
		experiment.setEnzymeBean(enzyme);
		experiment.setAuthorBean(author);
		experiment.setMethodBean(method);
		em.persist(experiment);
		
		saveEvidences(experiment, experimentBean.getEvidencesPath());
	}

	private void saveEvidences(Experiment experiment, String evidencesPath) throws IOException {
		for( EvidenceBean evBean : EvidenceFile.loadEvidences(evidencesPath) ) {
			for( String gene : evBean.getGene().split(";") ) {
				Evidence ev = new Evidence();
				Substrate subs;
				try {
					subs = em.createQuery("SELECT s FROM Substrate s WHERE s.gene = :gene", Substrate.class)
						.setParameter("gene", gene)
						.getSingleResult();
				} catch (NoResultException e) {
					subs = new Substrate();
					subs.setGene(gene);
					em.persist(subs);
				}
				ev.setExperimentBean(experiment);
				ev.setSubstrateBean(subs);
				ev.setFoldChange(evBean.getFoldChange());
				ev.setPValue(evBean.getPValue());
				em.persist(ev);
			}
		}
	}
}
