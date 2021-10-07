package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import es.ehubio.dubase.bl.Analyzer;
import es.ehubio.dubase.bl.beans.Overlap;

@Named
@RequestScoped
public class OverlapsView implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private Analyzer db;
	@Inject
	private PrefView prefs;
	private List<Overlap> overlaps;
	
	@PostConstruct
	private void init() {
		overlaps = db.findOverlaps(prefs.getMapThresholds());
	}
	
	public List<Overlap> getOverlaps() {
		return overlaps;
	}
}
