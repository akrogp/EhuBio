package es.ehubio.wregex.data;

import java.io.Reader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "presets")
public class PresetConfiguration {	
	private List<PresetBean> presets;
	
	@XmlElement(name="preset")
	public List<PresetBean> getPresets() {
		return presets;
	}
	
	public void setPresets(List<PresetBean> presets) {
		this.presets = presets;
	}
	
	public static PresetConfiguration load( Reader rd ) {
		PresetConfiguration configuration = null;
		try {
			JAXBContext context = JAXBContext.newInstance(PresetConfiguration.class);
			Unmarshaller um = context.createUnmarshaller();
			configuration = (PresetConfiguration)um.unmarshal(rd);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		return configuration;
	}
}
