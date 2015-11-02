package es.ehubio.mymrm.presentation;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import es.ehubio.mymrm.data.Instrument;

@ManagedBean
@RequestScoped
public class InstrumentMB implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Instrument instrument = new Instrument();
	private String typeId;
	
	public Instrument getEntity() {
		return instrument;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
}
