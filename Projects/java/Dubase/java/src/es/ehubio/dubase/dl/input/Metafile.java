package es.ehubio.dubase.dl.input;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import es.ehubio.dubase.dl.entities.Experiment;

public class Metafile {
	public static void save(Experiment data, File file) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Experiment.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(data, file);
	}
	
	public static Experiment load(File file) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Experiment.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return (Experiment)unmarshaller.unmarshal(file);
	}
}
