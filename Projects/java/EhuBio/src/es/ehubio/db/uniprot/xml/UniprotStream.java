package es.ehubio.db.uniprot.xml;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class UniprotStream {
	public static Stream<Entry> featureStreamFrom(InputStream is) throws XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory.createXMLEventReader(is);
        Iterable<Entry> iterable = () -> new IteratorFromReader(reader);
        return StreamSupport.stream(iterable.spliterator(), false);
	}
	
	private static class IteratorFromReader implements java.util.Iterator<Entry> {
		private final XMLEventReader reader;
		private StartElement startElement;

        public IteratorFromReader(XMLEventReader reader) {
            this.reader = reader;
        }

        @Override
        public boolean hasNext() {
        	if( startElement != null )
        		return true;
        	try {
	            while( reader.hasNext() ) {
	            	XMLEvent nextEvent = reader.nextEvent();
	            	if( nextEvent.isStartElement() ) {
	            		StartElement startElement = nextEvent.asStartElement();
	            		if( startElement.getName().getLocalPart().equals("entry") ) {
	            			this.startElement = startElement;
	            			return true;
	            		}
	            	}
	            }
	            return false;
        	} catch (XMLStreamException e) {
				throw new RuntimeException(e);
			}            
        }
        
        @Override
        public Entry next() {
        	if( !hasNext() )
        		throw new NoSuchElementException();
        	this.startElement = null;
        	Entry entry = new Entry();
        	try {
	        	while( reader.hasNext() ) {
	            	XMLEvent nextEvent = reader.nextEvent();
	            	if( nextEvent.isStartElement() ) {
	            		StartElement startElement = nextEvent.asStartElement();
	            		switch( startElement.getName().getLocalPart() ) {
	            			case "accession":
	            				entry.getAccession().add(reader.nextEvent().asCharacters().getData());
	            				//System.out.println(entry.getAccession().get(0));
	            				break;
	            			case "feature":
	            				entry.getFeature().add(parseFeature(startElement));
	            				break;
	            				
	            		}
	            	}
	            	if( nextEvent.isEndElement() ) {
	            		EndElement endElement = nextEvent.asEndElement();
	            		if( endElement.getName().getLocalPart().equals("entry") )
	            			break;
	            	}
	        	}
	        	return entry;
        	} catch (XMLStreamException e) {
				throw new RuntimeException(e);
			}
        }

		private FeatureType parseFeature(StartElement featElement) throws XMLStreamException {
			FeatureType feature = new FeatureType();
			feature.setType(getAttributeValue(featElement, "type"));
			feature.setDescription(getAttributeValue(featElement, "description"));
			while( reader.hasNext() ) {
				XMLEvent nextEvent = reader.nextEvent();
				if( nextEvent.isStartElement() ) {
            		StartElement startElement = nextEvent.asStartElement();
            		switch( startElement.getName().getLocalPart() ) {
        				case "location":
        					feature.setLocation(parseLocation(startElement));
        					break;
            		}
				}
				if( nextEvent.isEndElement() ) {
            		EndElement endElement = nextEvent.asEndElement();
            		if( endElement.getName().getLocalPart().equals("feature") )
            			break;
            	}
			}
			return feature;
		}
		
		private LocationType parseLocation(StartElement locElement) throws XMLStreamException {
			LocationType location = new LocationType();
			while( reader.hasNext() ) {
				XMLEvent nextEvent = reader.nextEvent();
				if( nextEvent.isStartElement() ) {
					StartElement startElement = nextEvent.asStartElement();
					switch( startElement.getName().getLocalPart() ) {
	    				case "begin":
	    					location.setBegin(parsePosition(startElement));
	    					break;
	    				case "end":
	    					location.setEnd(parsePosition(startElement));
	    					break;
	    				case "position":
	    					location.setPosition(parsePosition(startElement));
	    					break;
	        		}
				}
				if( nextEvent.isEndElement() ) {
            		EndElement endElement = nextEvent.asEndElement();
            		if( endElement.getName().getLocalPart().equals("location") )
            			break;
            	}
			}
			return location;
		}

		private PositionType parsePosition(StartElement posElement) {
			PositionType position = new PositionType();
			String attribute = getAttributeValue(posElement, "position");
			if( attribute != null )
				position.setPosition(new BigInteger(attribute));
			return position;
		}

		private String getAttributeValue(StartElement startElement, String attributeName) {
		    Attribute attribute = startElement.getAttributeByName(new QName(attributeName));
		    return (attribute != null) ? attribute.getValue() : null;
		}
	}
}