package es.ehubio.dubase.bl;

import java.util.HashMap;
import java.util.Map;

public class ReplicateBean {
	private Map<Integer, RepScoreBean> scores;

	public Map<Integer, RepScoreBean> getMapScores() {
		if( scores == null )
			scores = new HashMap<>();
		return scores;
	}
	
	public void putScore(Score score, Double value, boolean imputed) {
		RepScoreBean bean = new RepScoreBean();
		bean.setScore(score.ordinal());
		bean.setValue(value);
		bean.setImputed(imputed);
		getMapScores().put(bean.getScore(), bean);
	}
}
