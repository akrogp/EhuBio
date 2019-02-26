package es.ehubio.dubase.bl;

import java.util.ArrayList;
import java.util.List;

public class ReplicateBean {
	private List<RepScoreBean> scores;

	public List<RepScoreBean> getScores() {
		if( scores == null )
			scores = new ArrayList<>();
		return scores;
	}
}
