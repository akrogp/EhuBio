package es.ehubio.dubase.pl.views;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class VennView implements Serializable {
	private static final long serialVersionUID = 1L;
	private String gene1, gene2;
	
	public String getGene1() {
		return gene1;
	}
	public void setGene1(String gene1) {
		this.gene1 = gene1;
	}
	
	public String getGene2() {
		return gene2;
	}
	public void setGene2(String gene2) {
		this.gene2 = gene2;
	}
	
	public void compare() {
	
	}
}
