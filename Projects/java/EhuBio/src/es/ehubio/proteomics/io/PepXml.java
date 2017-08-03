package es.ehubio.proteomics.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.OperationNotSupportedException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

import es.ehubio.model.ProteinModificationType;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Ptm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.Spectrum;
import es.ehubio.proteomics.isb.MsmsPipelineAnalysis;
import es.ehubio.proteomics.isb.MsmsPipelineAnalysis.MsmsRunSummary;
import es.ehubio.proteomics.isb.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import es.ehubio.proteomics.isb.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import es.ehubio.proteomics.isb.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo;
import es.ehubio.proteomics.isb.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo.ModAminoacidMass;
import es.ehubio.proteomics.isb.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.SearchScore;

public class PepXml extends MsMsFile {
	private static class XMLReaderWithoutNamespace extends StreamReaderDelegate {
	    public XMLReaderWithoutNamespace(XMLStreamReader reader) {
	      super(reader);
	    }
	    @Override
	    public String getAttributeNamespace(int arg0) {
	      return "";
	    }
	    @Override
	    public String getNamespaceURI() {
	      return "";
	    }
	}
	
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
		if( loadFragments == true )
			throw new OperationNotSupportedException("Fragments not still supported");
		JAXBContext jaxbContext = JAXBContext.newInstance(MsmsPipelineAnalysis.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		
		XMLInputFactory xif = XMLInputFactory.newFactory();
		XMLStreamReader xsr = xif.createXMLStreamReader(input);
		XMLReaderWithoutNamespace xr = new XMLReaderWithoutNamespace(xsr);
		
		MsmsPipelineAnalysis pepXml = (MsmsPipelineAnalysis)unmarshaller.unmarshal(xr);
		return loadPepXml(pepXml);
	}

	private MsMsData loadPepXml(MsmsPipelineAnalysis pepXml) throws Exception {
		MsmsRunSummary run = pepXml.getMsmsRunSummary(); 
		String mgf = run.getBaseName();
		Set<Spectrum> spectra = new HashSet<>();
		Map<String,Peptide> mapPeptides = new HashMap<>();
		for( SpectrumQuery query : run.getSpectrumQuery() ) {
			if( query.getSearchResult().getSearchHit().isEmpty() )
				continue;
			Spectrum spectrum = new Spectrum();
			spectrum.setFileName(mgf);
			spectrum.setFileId(query.getSpectrumNativeID()!=null?query.getSpectrumNativeID():query.getSpectrum());
			spectrum.setScan(query.getStartScan().toString());
			spectrum.setRt(query.getRetentionTimeSec());
			spectra.add(spectrum);
			for( SearchHit hit : query.getSearchResult().getSearchHit() ) {
				String pepId;
				ModificationInfo modInfo = hit.getModificationInfo(); 
				if( modInfo != null )
					pepId = hit.getModificationInfo().getModifiedPeptide();
				else
					pepId = hit.getPeptide();
				Peptide peptide = mapPeptides.get(pepId);
				if( peptide == null ) {
					peptide = new Peptide();
					mapPeptides.put(pepId, peptide);
					peptide.setSequence(hit.getPeptide());
					if( modInfo != null )
						for( ModAminoacidMass mod : modInfo.getModAminoacidMass() ) {
							Ptm ptm = new Ptm();
							int pos = mod.getPosition();
							ptm.setPosition(pos);
							//TOFIX: ptm.setMassDelta(mod.getMass());
							char aa = Character.toLowerCase(peptide.getSequence().charAt(pos-1));
							if( aa == 'c' )
								ptm.setType(ProteinModificationType.CARBAMIDOMETHYLATION);
							else if( aa == 'm' )
								ptm.setType(ProteinModificationType.OXIDATION);
							else
								throw new Exception("Unsupported PTM");
							ptm.guessMissing(null);
							peptide.addPtm(ptm);
						}					
				}
				Psm psm = new Psm();
				psm.linkSpectrum(spectrum);
				psm.linkPeptide(peptide);
				psm.setCharge(query.getAssumedCharge());
				psm.setExpMz(query.getPrecursorNeutralMass());
				psm.setCalcMz(hit.getCalcNeutralPepMass());
				psm.setRank(hit.getHitRank());
				for(SearchScore hitScore : hit.getSearchScore() ) {
					ScoreType scoreType;
					if( hitScore.getName().equalsIgnoreCase("xcorr") )
						scoreType = ScoreType.SEQUEST_XCORR;
					else if( hitScore.getName().equalsIgnoreCase("deltacn") )
						scoreType = ScoreType.SEQUEST_DELTACN;
					else if( hitScore.getName().equalsIgnoreCase("spscore") )
						scoreType = ScoreType.SEQUEST_SPSCORE;
					else if( hitScore.getName().equalsIgnoreCase("sprank") )
						scoreType = ScoreType.SEQUEST_SPRANK;
					else if( hitScore.getName().equalsIgnoreCase("expect") )
						scoreType = ScoreType.EVALUE;
					else if( hitScore.getName().equalsIgnoreCase("hyperscore") )
						scoreType = ScoreType.XTANDEM_HYPERSCORE;
					else
						continue;
					psm.putScore(new Score(scoreType, hitScore.getValue()));
				}
			}
		}
		MsMsData data = new MsMsData();
		data.loadFromSpectra(spectra);
		//data.updateRanks(ScoreType.SEQUEST_XCORR);
		return data;
	}
		
	/*protected MsMsData loadStream(InputStream input, boolean loadFragments) throws Exception {
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
	}*/
}
