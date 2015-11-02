package es.ehubio.mymrm.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PrecursorBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private double mz;
	private String charge;
	private List<DetailsBean> experiments = new ArrayList<>();

	public double getMz() {
		return mz;
	}
	
	public void setMz(double mz) {
		this.mz = mz;
	}

	public List<DetailsBean> getExperiments() {
		return experiments;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = String.format("%s%c", Math.abs(charge), charge>=0?'+':'-');
	}
}
