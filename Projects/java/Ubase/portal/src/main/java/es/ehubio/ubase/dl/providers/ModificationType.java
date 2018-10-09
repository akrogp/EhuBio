package es.ehubio.ubase.dl.providers;

public enum ModificationType {
	GLYGLY(1);
	
	ModificationType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	private final int id;
}
