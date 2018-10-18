package es.ehubio.ubase.bl.stats;

import java.util.Date;
import java.util.List;

public class UbStats {
	private List<ExpStats> experiments;
	private long modifications;
	private Date updated;
	
	public List<ExpStats> getExperiments() {
		return experiments;
	}
	public void setExperiments(List<ExpStats> experiments) {
		this.experiments = experiments;
	}
	public long getModifications() {
		return modifications;
	}
	public void setModifications(long modifications) {
		this.modifications = modifications;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
}
