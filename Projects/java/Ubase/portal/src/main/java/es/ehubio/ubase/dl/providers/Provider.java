package es.ehubio.ubase.dl.providers;

public enum Provider {
	MAXQUANT(MaxQuantDao.class, "MaxQuant");
	
	Provider(Class<? extends Dao> dao, String name) {
		this.dao = dao;
		this.name = name;
	}
	
	public Class<? extends Dao> getDao() {
		return dao;
	}
	
	public String getName() {
		return name;
	}
	
	private final Class<? extends Dao> dao;
	private final String name;
}
