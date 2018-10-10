package es.ehubio.ubase.dl.providers;

import java.util.HashMap;
import java.util.Map;

public enum ScoreType {
	INTENSITY(1),
	LOC_PROB(2),
	MQ_SCORE(3),
	LFQ_INTENSITY(4),
	Q_VALUE(6),
	PEP(7),
	MQ_LOC_SCORE(8);
	
	ScoreType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static synchronized ScoreType fromId(int id) {
		if( map == null ) {
			map = new HashMap<>();
			for( ScoreType type : values() )
				map.put(type.getId(), type);
		}
		return map.get(id);
	}
	
	private final int id;
	private static Map<Integer, ScoreType> map;
}
