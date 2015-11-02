package es.ehubio.wregex.data;

import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "motifs")
@XmlAccessorType(XmlAccessType.FIELD)
public final class MotifConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	@XmlElement(name="motif")
	private List<MotifInformation> motifs;

	public List<MotifInformation> getMotifs() {
		return motifs;
	}

	public void setMotifs(List<MotifInformation> motifs) {
		this.motifs = motifs;
	}
	
	public static MotifConfiguration load( Reader rd ) {
		MotifConfiguration configuration = null;
		try {
			JAXBContext context = JAXBContext.newInstance(MotifConfiguration.class);
			Unmarshaller um = context.createUnmarshaller();
			configuration = (MotifConfiguration)um.unmarshal(rd);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		return configuration;
	}
	
	public void save( PrintStream writer ) {
		try {
			JAXBContext context = JAXBContext.newInstance(MotifConfiguration.class);
			Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		    m.marshal(this,writer);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}		
	}
}
