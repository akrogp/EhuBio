package es.ehubio.ubase.pl;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import es.ehubio.ubase.bl.Ubase;
import es.ehubio.ubase.bl.stats.UbStats;

@Named
@RequestScoped
public class HomeView extends BaseView {
	@EJB
	private Ubase ubase;
	private UbStats stats;
	
	public UbStats getStats() {
		if( stats == null )
			stats = ubase.queryStats();
		return stats;
	}
}
