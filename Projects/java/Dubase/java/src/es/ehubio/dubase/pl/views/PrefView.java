package es.ehubio.dubase.pl.views;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import es.ehubio.dubase.Thresholds;

@Named
@SessionScoped
public class PrefView implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Thresholds thresholds = new Thresholds();
	
	public Thresholds getThresholds() {
		return thresholds;
	}
}
