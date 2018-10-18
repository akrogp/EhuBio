package es.ehubio.ubase.bl.stats;

import es.ehubio.ubase.dl.entities.Modification;

public class ModStats {
	private Modification modification;
	private long count;
	public Modification getModification() {
		return modification;
	}
	public void setModification(Modification modification) {
		this.modification = modification;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	} 
}
