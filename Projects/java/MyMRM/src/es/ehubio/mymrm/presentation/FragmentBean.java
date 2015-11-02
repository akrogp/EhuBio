package es.ehubio.mymrm.presentation;

import java.io.Serializable;

import es.ehubio.mymrm.data.Fragment;
import es.ehubio.proteomics.IonType;

public class FragmentBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Fragment entity;
	private String name;
	private double ppm;
	private double mzExp;
	
	public Fragment getEntity() {
		return entity;
	}
	
	public void setEntity(Fragment entity) {
		this.entity = entity;
		mzExp = entity.getMz()+entity.getError();
		ppm = entity.getError()/entity.getMz()*1000000;
		
		IonType type = IonType.getByName(entity.getIonType().getName());
		if( type == null ) {
			name = entity.getIonType().getName();
			return;
		}
		
		StringBuilder builder = new StringBuilder(type.getCode());
		if( entity.getPosition() > 0 )
			builder.append(String.format("%d", entity.getPosition()));
		if( type.getLoss() != null )
			builder.append(String.format(" - %s", type.getLoss()));
		builder.append(String.format(" (%d%c)", entity.getCharge(), entity.getCharge()>0?'+':'-'));
		name = builder.toString();
	}
	
	public String getType() {
		return name;
	}
	
	public double getPpm() {
		return ppm;
	}

	public double getMzExp() {
		return mzExp;
	}
}
