package es.ehubio.wregex.view;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class SearchOptionsView implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean grouping = true;
	private boolean filterEqual = false;
	private double scoreThreshold = 0.0;
	private int flanking = 0;
	
	public boolean isGrouping() {
		return grouping;
	}
	
	public void setGrouping(boolean grouping) {
		this.grouping = grouping;
	}
	
	public boolean isFilterEqual() {
		return filterEqual;
	}
	
	public void setFilterEqual(boolean filterEqual) {
		this.filterEqual = filterEqual;
	}
	
	public double getScoreThreshold() {
		return scoreThreshold;
	}
	
	public void setScoreThreshold(double scoreThreshold) {
		this.scoreThreshold = scoreThreshold;
	}
	
	public int getFlanking() {
		return flanking;
	}
	
	public void setFlanking(int flanking) {
		this.flanking = flanking;
	}
}
