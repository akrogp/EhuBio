package es.ehubio.ubase;

import java.util.List;

import es.ehubio.ubase.dl.providers.Provider;

public interface LocatorInterface {
	List<Provider> getProviders();
	Configuration getConfiguration();
}
