package es.ehubio.mymrm.presentation;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import es.ehubio.mymrm.data.Chromatography;

@ManagedBean
@RequestScoped
public class ChromatographyMB implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Chromatography entity = new Chromatography();
	public Chromatography getEntity() {
		return entity;
	}
}
