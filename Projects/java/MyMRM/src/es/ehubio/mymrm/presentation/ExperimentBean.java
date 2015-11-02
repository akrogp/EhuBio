package es.ehubio.mymrm.presentation;

import java.io.Serializable;

import es.ehubio.mymrm.business.ExperimentFeed;
import es.ehubio.mymrm.data.Experiment;

public class ExperimentBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Experiment entity;
	private ExperimentFeed feed;
	
	public Experiment getEntity() {
		if( feed != null )
			return feed.getExperiment();
		return entity;
	}
	
	public void setEntity(Experiment entity) {
		this.entity = entity;
	}

	public ExperimentFeed getFeed() {
		return feed;
	}

	public void setFeed(ExperimentFeed feed) {
		this.feed = feed;
	}
}
