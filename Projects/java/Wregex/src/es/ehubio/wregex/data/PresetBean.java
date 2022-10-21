package es.ehubio.wregex.data;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "preset")
@XmlAccessorType(XmlAccessType.FIELD)
public class PresetBean {
	private String name;
	private String mainMotif;
	private String auxMotif;
	private String target;
	private String targetInput;
	private boolean grouping = true;
	private boolean filterSimilar;
	private boolean cosmic;
	@XmlElement(name="ptm")
	private List<String> ptms;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getMainMotif() {
		return mainMotif;
	}

	public void setMainMotif(String mainMotif) {
		this.mainMotif = mainMotif;
	}
	
	public String getAuxMotif() {
		return auxMotif;
	}
	
	public void setAuxMotif(String auxMotif) {
		this.auxMotif = auxMotif;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public String getTargetInput() {
		return targetInput;
	}
	
	public void setTargetInput(String targetInput) {
		this.targetInput = targetInput;
	}
	
	public boolean isGrouping() {
		return grouping;
	}
	
	public void setGrouping(boolean grouping) {
		this.grouping = grouping;
	}
	
	public boolean isFilterSimilar() {
		return filterSimilar;
	}
	
	public void setFilterSimilar(boolean filterSimilar) {
		this.filterSimilar = filterSimilar;
	}

	public boolean isCosmic() {
		return cosmic;
	}

	public void setCosmic(boolean cosmic) {
		this.cosmic = cosmic;
	}
	
	public List<String> getPtms() {
		return ptms;
	}
	
	public void setPtms(List<String> ptms) {
		this.ptms = ptms;
	}
}
