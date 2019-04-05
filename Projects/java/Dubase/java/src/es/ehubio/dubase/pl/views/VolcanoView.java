package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import es.ehubio.io.UrlBuilder;

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
	
	public String getDataUrl() {
		try {
			return new UrlBuilder("rest/analyze")
				.pathf("%s.json", gene)
				.param("xth", 0)
				.param("yth", 0)
				.build();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
