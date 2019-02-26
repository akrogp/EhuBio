package es.ehubio.dubase.bl;

public class EvScoreBean {
	private int score;
	private Double value;
	
	public EvScoreBean(Score score, Double value) {
		this.score = score.ordinal();
		this.value = value;
	}
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
}
