package es.ehubio.mymrm.presentation;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import es.ehubio.mymrm.data.InstrumentType;

@ManagedBean
@RequestScoped
public class InstrumentTypeMB implements Serializable {
	private static final long serialVersionUID = 1L;
	private final InstrumentType entity = new InstrumentType();
	
	public InstrumentType getEntity() {
		return entity;
	}
}
