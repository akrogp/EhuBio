package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;

import org.primefaces.PrimeFaces;

import es.ehubio.dubase.bl.Browser;
import es.ehubio.dubase.bl.Stats;
import es.ehubio.dubase.bl.beans.Flare;
import es.ehubio.dubase.dl.entities.Clazz;
import es.ehubio.dubase.dl.entities.Enzyme;
import es.ehubio.dubase.dl.entities.Superfamily;

@Named
@RequestScoped
public class BrowseView implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final double DEFAULT_SIZE = 1;
	@Inject
	private PrefView prefs;
	@EJB
	private Browser db;	
	
	public void getFlare() throws JAXBException {		
		Flare flare = buildFlare();
		PrimeFaces.current().ajax().addCallbackParam("flare", flare);
	}

	private Flare buildFlare() {
		Map<String, Stats> map = db.getEnzymesStats();
		Flare flare = new Flare("DUBs");
		for( Clazz classBean : db.getClasses() ) {
			Flare clazz = new Flare(classBean.getName());
			flare.addChild(clazz);
			for( Superfamily sfBean : db.getSuperfamiliesByClass(classBean.getId()) ) {
				Flare sf = new Flare(sfBean.getShortname());
				sf.setDesc(sfBean.getName());
				clazz.addChild(sf);
				for( Enzyme enzymeBean : db.getEnzymesBySuperfamily(sfBean.getId()) ) {
					String dub = enzymeBean.getGene();
					Flare enzyme = new Flare(dub);
					Stats stats = map.get(enzymeBean.getGene());
					enzyme.setDesc(dub);
					if( stats != null ) {
						Flare counts = new Flare();
						int substratesCount = db.getSubstrates(dub, prefs.getMapThresholds()).size();
						counts.setName(dub);
						counts.setDesc(String.format("%d (%d)", substratesCount, stats.getPapersCount()));
						counts.setSize(DEFAULT_SIZE);
						counts.setDb(true);
						enzyme.addChild(counts);
					} else
						enzyme.setSize(DEFAULT_SIZE);
					sf.addChild(enzyme);
				}
			}
		}
		return flare;
	}
}
