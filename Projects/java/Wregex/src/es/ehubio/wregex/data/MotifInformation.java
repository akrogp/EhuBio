package es.ehubio.wregex.data;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "motif")
@XmlAccessorType(XmlAccessType.FIELD)
public final class MotifInformation implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String summary;
	private String replaces;
	private Integer wregexVersion;
	@XmlElement(name="definition")
	private List<MotifDefinition> definitions;
	@XmlElement(name="reference")
	private List<MotifReference> references;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public List<MotifDefinition> getDefinitions() {
		return definitions;
	}
	
	public void setDefinitions(List<MotifDefinition> definitions) {
		this.definitions = definitions;
	}

	public List<MotifReference> getReferences() {
		return references;
	}

	public void setReferences(List<MotifReference> references) {
		this.references = references;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public String getReplaces() {
		return replaces;
	}

	public void setReplaces(String replaces) {
		this.replaces = replaces;
	}

	public Integer getWregexVersion() {
		return wregexVersion;
	}

	public void setWregexVersion(Integer wregexVersion) {
		this.wregexVersion = wregexVersion;
	}
}