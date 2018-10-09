package es.ehubio.ubase;

import java.util.ArrayList;
import java.util.List;

import es.ehubio.ubase.dl.providers.MaxQuantDao;
import es.ehubio.ubase.dl.providers.Dao;

public class ProductionLocator implements LocatorInterface {

	@Override
	public List<Dao> getProviders() {
		if( providers == null ) {
			providers = new ArrayList<>();
			providers.add(new MaxQuantDao());
		}
		return providers;
	}

	@Override
	public Configuration getConfiguration() {
		if( config == null )
			config = new WebConfiguration();
		return config;
	}
	
	private List<Dao> providers;
	private Configuration config;
}
