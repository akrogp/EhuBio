package es.ehubio.dubase.dl.input;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class Metafile {
	public static void save(Metadata data, File file) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Metadata.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(data, file);
	}
	
	public static Metadata load(File file) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Metadata.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return (Metadata)unmarshaller.unmarshal(file);
	}
}
