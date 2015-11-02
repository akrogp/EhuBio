package es.ehubio.mymrm.presentation;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import es.ehubio.mymrm.data.FragmentationType;

@ManagedBean
@RequestScoped
public class FragmentationTypeMB {
	private final FragmentationType entity = new FragmentationType();

	public FragmentationType getEntity() {
		return entity;
	}
}
