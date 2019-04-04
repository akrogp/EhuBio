package es.ehubio.dubase.pl.views;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class VolcanoView implements Serializable {
	static final long serialVersionUID = 1L;
	private String gene;
	
	public String getGene() {
		return gene;
	}
	
	public void setGene(String gene) {
		this.gene = gene;
	}
	
	public String plot() {
		return "volcano";
	}
}
