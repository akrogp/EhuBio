package es.ehubio.mymrm.presentation;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import es.ehubio.mymrm.data.IonizationType;

@ManagedBean
@RequestScoped
public class IonizationTypeMB {
	private final IonizationType entity = new IonizationType();

	public IonizationType getEntity() {
		return entity;
	}
}
