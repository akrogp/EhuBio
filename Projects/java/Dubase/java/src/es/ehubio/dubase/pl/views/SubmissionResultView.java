package es.ehubio.dubase.pl.views;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class SubmissionResultView implements Serializable {
	private static final long serialVersionUID = 1L;
	private String reference;
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
}
