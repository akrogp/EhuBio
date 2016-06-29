package es.ehubio.dna;

import es.ehubio.model.Aminoacid;

public class Codon {
	public boolean isStart() {
		return start;
	}
	public void setStart(boolean start) {
		this.start = start;
	}
	public boolean isStop() {
		return stop;
	}
	public void setStop(boolean stop) {
		this.stop = stop;
	}
	public Aminoacid getAa() {
		return aa;
	}
	public void setAa(Aminoacid aa) {
		this.aa = aa;
	}
	private boolean start, stop;
	private Aminoacid aa;
}
