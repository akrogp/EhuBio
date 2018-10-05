package es.ehubio.dl.input;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Condition {
	private String name;
	private String description;
	private List<String> replicas;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@XmlElement(name="replica")
	public List<String> getReplicas() {
		return replicas;
	}
	public void setReplicas(List<String> replicas) {
		this.replicas = replicas;
	}
}
