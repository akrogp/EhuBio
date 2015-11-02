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

@XmlRootElement(name = "databases")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	@XmlElement(name="database")
	private List<DatabaseInformation> databases;
	
	public List<DatabaseInformation> getDatabases() {
		return databases;
	}
	
	public void setDatabases(List<DatabaseInformation> databses) {
		this.databases = databses;
	}
	
	public static DatabaseConfiguration load( Reader rd ) {
		DatabaseConfiguration configuration = null;
		try {
			JAXBContext context = JAXBContext.newInstance(DatabaseConfiguration.class);
			Unmarshaller um = context.createUnmarshaller();
			configuration = (DatabaseConfiguration)um.unmarshal(rd);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		return configuration;
	}
	
	public void save( PrintStream writer ) {
		try {
			JAXBContext context = JAXBContext.newInstance(DatabaseConfiguration.class);
			Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		    m.marshal(this,writer);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}		
	}
}
