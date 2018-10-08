package es.ehubio.ubase;

import java.util.ArrayList;
import java.util.List;

import es.ehubio.ubase.dl.providers.MaxQuantProvider;
import es.ehubio.ubase.dl.providers.Provider;

public class ProductionLocator implements LocatorInterface {

	@Override
	public List<Provider> getProviders() {
		if( providers == null ) {
			providers = new ArrayList<>();
			providers.add(new MaxQuantProvider());
		}
		return providers;
	}

	@Override
	public Configuration getConfiguration() {
		if( config == null )
			config = new WebConfiguration();
		return config;
	}
	
	private List<Provider> providers;
	private Configuration config;
}
