package es.ehubio.dubase.bl.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Flare {
	private String name;
	private String desc;
	private Double size;
	private Double gradient;
	private List<Flare> children;
	
	public Flare() {
	}
	
	public Flare(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public Double getSize() {
		return size;
	}
	public void setSize(double size) {
		this.size = size;
	}
	
	public List<Flare> getChildren() {
		return children;
	}
	public void setChildren(List<Flare> children) {
		this.children = children;
	}
	
	public void addChild(Flare child) {
		if( children == null )
			children = new ArrayList<>();
		children.add(child);
	}

	public Double getGradient() {
		return gradient;
	}

	public void setGradient(Double gradient) {
		this.gradient = gradient;
	}
}
