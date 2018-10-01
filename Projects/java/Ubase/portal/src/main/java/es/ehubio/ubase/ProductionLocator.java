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

	private List<Provider> providers;
}
