package es.ehubio.dubase.bl.beans;

public class RepScoreBean {
	private int score;
	private Double value;
	private boolean imputed;
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
	public boolean isImputed() {
		return imputed;
	}
	public void setImputed(boolean imputed) {
		this.imputed = imputed;
	}
}
