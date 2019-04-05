package es.ehubio.dubase.bl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.bl.beans.ClassBean;
import es.ehubio.dubase.bl.beans.EnzymeBean;
import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.bl.beans.Flare;
import es.ehubio.dubase.bl.beans.SuperfamilyBean;
import es.ehubio.dubase.bl.beans.TreeBean;
import es.ehubio.dubase.dl.entities.Clazz;
import es.ehubio.dubase.dl.entities.Enzyme;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.Superfamily;

@LocalBean
@Singleton
@Path("/browse")
public class Browser {
	@PersistenceContext
	private EntityManager em;
	private static final double DEFAULT_SIZE = 1;
	private static final double MAX_SCORE = 4;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("flare.json")
	public Flare getFlare(@QueryParam("xth") Double xth, @QueryParam("yth") Double yth) {
		Thresholds th = new Thresholds();
		if( xth != null )
			th.setLog2FoldChange(xth);
		if( yth != null )
			th.setLog10PValue(yth);
		TreeBean tree = getTree(th);		
		Flare flare = new Flare("DUBs");
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
						subs.setSize(DEFAULT_SIZE);//Math.round(evBean.getMapScores().get(Score.FOLD_CHANGE.ordinal())));
						subs.setGradient(calcGradient(evBean));
						enzyme.addChild(subs);
					}
					if( enzyme.getChildren() != null)
						Collections.sort(enzyme.getChildren(), new Comparator<Flare>() {
							@Override
							public int compare(Flare o1, Flare o2) {
								return o1.getGradient().compareTo(o2.getGradient());
							}
						});
				}
			}
		}
		
		return flare;
	}
	
	private Double calcGradient(EvidenceBean evBean) {
		double score = evBean.getMapScores().get(Score.FOLD_CHANGE.ordinal());
		double sign = Math.signum(score);
		score = Math.min(MAX_SCORE, Math.abs(score));
		return sign * score / MAX_SCORE;
	}

	public TreeBean getTree(Thresholds th) {
		TreeBean tree = new TreeBean();
		for( Clazz clazz : getClasses() ) {
			ClassBean classBean = new ClassBean(clazz);
			for( Superfamily family : getSuperfamiliesByClass(clazz.getId()) ) {
				SuperfamilyBean superfamilyBean = new SuperfamilyBean(family);
				for( Enzyme enzyme : getEnzymesBySuperfamily(family.getId()) ) {
					EnzymeBean enzymeBean = new EnzymeBean(enzyme);
					enzymeBean.getSubstrates().addAll(getSubstrateByEnzyme(enzyme.getId(), th));
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
	
	private List<EvidenceBean> getSubstrateByEnzyme(int enzymeId, Thresholds th) {
		List<Evidence> evidences = em
			.createQuery("SELECT e FROM Evidence e WHERE e.experimentBean.enzymeBean.id = :enzymeId AND (SELECT COUNT(s) FROM e.evScores s WHERE s.scoreType.id = :t1 AND ABS(s.value) > :s1) > 0 AND (SELECT COUNT(s) FROM e.evScores s WHERE s.scoreType.id = :t2 AND s.value > :s2) > 0", Evidence.class)
			.setParameter("enzymeId", enzymeId)
			.setParameter("t1", Score.FOLD_CHANGE.ordinal())
			.setParameter("s1", th.getLog2FoldChange())
			.setParameter("t2", Score.P_VALUE.ordinal())
			.setParameter("s2", th.getLog10PValue())
			.getResultList();
		List<EvidenceBean> results = DbUtils.buildEvidences(evidences);
		return DbUtils.filter(results, th);
	}
}
