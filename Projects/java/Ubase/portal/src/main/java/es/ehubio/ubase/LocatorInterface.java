package es.ehubio.ubase;

import java.util.List;

import es.ehubio.ubase.dl.providers.Dao;

public interface LocatorInterface {
	List<Dao> getProviders();
	Configuration getConfiguration();
}
