package es.ehubio.wregex.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "definition")
@XmlAccessorType(XmlAccessType.FIELD)
public final class MotifDefinition implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String regex;
	private String pssm;
	
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
	
	public String getRegex() {
		return regex;
	}
	
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	public String getPssm() {
		return pssm;
	}
	
	public void setPssm(String pssm) {
		this.pssm = pssm;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}