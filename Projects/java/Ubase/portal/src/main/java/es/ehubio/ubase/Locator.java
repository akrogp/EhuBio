package es.ehubio.ubase;

import java.util.List;

import es.ehubio.ubase.dl.providers.Provider;

public class Locator {
	public static synchronized List<Provider> getProviders() {
		return locator.getProviders();
	}
	
	private static LocatorInterface locator = new ProductionLocator(); 
}
