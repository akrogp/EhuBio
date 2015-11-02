package es.ehubio.proteomics;

import java.io.Serializable;

public class FragmentIon implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public double getMzExp() {
		return mzExp;
	}
	
	public void setMzExp(double mzExp) {
		this.mzExp = mzExp;
	}
	
	public double getMzCalc() {
		return getMzExp()-getMzError();
	}
	
	public double getIntensity() {
		return intensity;
	}
	
	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}
	
	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getMzError() {
		return mzError;
	}
	
	public double getPpms() {
		return mzError/getMzCalc()*1000000;
	}

	public void setMzError(double mzError) {
		this.mzError = mzError;
	}
	
	public IonType getType() {
		return type;
	}

	public void setType(IonType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for( int i = 1; i < charge; i++ )
			str.append('+');
		return String.format("%s%s%s%s (%.3f ppm)",
			type.getCode(), index==0?"":""+index, str.toString(),
			type.getLoss()==null?"":String.format("-%s", type.getLoss()),
			getPpms());
	}

	private double mzExp;
	private double intensity;
	private int charge;
	private int index;
	private double mzError;
	private IonType type;
}
