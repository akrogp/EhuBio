package es.ehubio.ubase.dl.providers;

public enum ScoreType {
	INTENSITY(1),
	LOC_PROB(2),
	MQ_SCORE(3),
	LFQ_INTENSITY(4),
	Q_VALUE(6),
	PEP(7);
	
	ScoreType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	private final int id;
}
