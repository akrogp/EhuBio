package es.ehubio.mymrm.presentation;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import es.ehubio.mymrm.data.FastaFile;

@ManagedBean
@RequestScoped
public class FastaMB implements Serializable {
	private static final long serialVersionUID = 1L;
	private final FastaFile entity = new FastaFile();
	
	public FastaFile getEntity() {
		return entity;
	}	
}
