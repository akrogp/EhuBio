package es.ehubio.proteomics.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import es.ehubio.model.ProteinModificationType;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Ptm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.Spectrum;

public class PepXml extends MsMsFile {

	@Override
	public String getFilenameExtension() {
		return "pep.xml";
	}

	@Override
	protected boolean checkSignatureStream(InputStream input) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(input));		
		return br.readLine().toLowerCase().contains("xml") && br.readLine().toLowerCase().contains("pepxml");
	}
	
	@Override
	protected MsMsData loadStream(InputStream input, boolean loadFragments) throws Exception {
		XMLInputFactory f = XMLInputFactory.newInstance();
		XMLEventReader r = f.createXMLEventReader(input);
		String base = null;		
		Spectrum spectrum = null;
		Psm psm = null;
		Peptide peptide = null;
		List<Spectrum> spectra = new ArrayList<>();
		Map<String, Peptide> mapPeptides = new HashMap<>();
		Double expMass = null;
		Integer charge = null;
		boolean hit = false;
		
		while( r.hasNext() ) {
			XMLEvent event = r.nextEvent();
			switch( event.getEventType() ) {
				case XMLStreamConstants.START_ELEMENT: {
					StartElement element = event.asStartElement();
					String name = element.getName().getLocalPart().toLowerCase();
					if( name.equals("search_summary") ) {
						base = getAttribute(element, "base_name");
					} else if( name.equals("spectrum_query") ) {
						hit = false;
						spectrum = new Spectrum();
						spectrum.setFileName(base);
						spectrum.setFileId(getAttribute(element, "spectrumNativeID"));
						spectrum.setScan(getAttribute(element, "start_scan"));
						spectrum.setRt(Double.parseDouble(getAttribute(element, "retention_time_sec")));
						expMass = Double.parseDouble(getAttribute(element, "precursor_neutral_mass"));
						charge = Integer.parseInt(getAttribute(element, "assumed_charge"));
					} else if (name.equals("search_hit") ) {
						hit = true;
						psm = new Psm();
						psm.linkSpectrum(spectrum);
						psm.setRank(Integer.parseInt(getAttribute(element, "hit_rank")));
						psm.setExpMz(expMass/charge);
						psm.setCharge(charge);
						peptide = new Peptide();
						peptide.setSequence(getAttribute(element, "peptide"));
					} else if (name.equals("mod_aminoacid_mass") ) {
						//double mass = Double.parseDouble(getAttribute(element, "mass"));
						int pos = Integer.parseInt(getAttribute(element, "position"));
						char aa = peptide.getSequence().toLowerCase().charAt(pos-1);
						Ptm ptm = new Ptm();
						ptm.setPosition(pos);
						if( aa == 'c' )
							ptm.setType(ProteinModificationType.CARBAMIDOMETHYLATION);
						else if( aa == 'm' )
							ptm.setType(ProteinModificationType.OXIDATION);
						else
							throw new Exception("Unsupported PTM");
						ptm.guessMissing(null);
						peptide.addPtm(ptm);
					} else if (name.equals("search_score") ) {
						if( getAttribute(element, "name").equals("xcorr") )
							psm.putScore(new Score(ScoreType.SEQUEST_XCORR, Double.parseDouble(getAttribute(element, "value"))));
					}
				} break;
				case XMLStreamConstants.END_ELEMENT: {
					EndElement element = event.asEndElement();
					String name = element.getName().getLocalPart().toLowerCase();
					if( name.equals("search_hit") && peptide != null ) {
						Peptide prev = mapPeptides.get(peptide.getUniqueString());
						if( prev == null ) {
							mapPeptides.put(peptide.getUniqueString(), peptide);
							prev = peptide;
						}
						psm.linkPeptide(peptide);
						psm = null;
						peptide = null;
					}
					else if( name.equals("spectrum_query") ) {
						if( hit )
							spectra.add(spectrum);
						hit= false;
						spectrum = null;
						expMass = null;
						charge = null;
					}
				} break;
			}
		}
		
		MsMsData data = new MsMsData();
		data.loadFromSpectra(spectra);
		return data;
	}
	
	private String getAttribute(StartElement element, String attribute) {
		return element.getAttributeByName(new QName(attribute)).getValue();
	}
}
