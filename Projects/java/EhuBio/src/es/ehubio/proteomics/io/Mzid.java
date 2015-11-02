package es.ehubio.proteomics.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.HeaderParser;
import es.ehubio.model.ProteinModificationType;
import es.ehubio.proteomics.AmbiguityGroup;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.DecoyBase;
import es.ehubio.proteomics.FragmentIon;
import es.ehubio.proteomics.IonType;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Ptm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.Spectrum;
import es.ehubio.proteomics.psi.mzid11.AbstractContactType;
import es.ehubio.proteomics.psi.mzid11.AbstractParamType;
import es.ehubio.proteomics.psi.mzid11.AnalysisSoftwareType;
import es.ehubio.proteomics.psi.mzid11.BibliographicReferenceType;
import es.ehubio.proteomics.psi.mzid11.CVParamType;
import es.ehubio.proteomics.psi.mzid11.DBSequenceType;
import es.ehubio.proteomics.psi.mzid11.FragmentArrayType;
import es.ehubio.proteomics.psi.mzid11.InputSpectrumIdentificationsType;
import es.ehubio.proteomics.psi.mzid11.IonTypeType;
import es.ehubio.proteomics.psi.mzid11.MeasureType;
import es.ehubio.proteomics.psi.mzid11.ModificationType;
import es.ehubio.proteomics.psi.mzid11.MzIdentML;
import es.ehubio.proteomics.psi.mzid11.OrganizationType;
import es.ehubio.proteomics.psi.mzid11.PeptideEvidenceRefType;
import es.ehubio.proteomics.psi.mzid11.PeptideEvidenceType;
import es.ehubio.proteomics.psi.mzid11.PeptideHypothesisType;
import es.ehubio.proteomics.psi.mzid11.PeptideType;
import es.ehubio.proteomics.psi.mzid11.PersonType;
import es.ehubio.proteomics.psi.mzid11.ProteinAmbiguityGroupType;
import es.ehubio.proteomics.psi.mzid11.ProteinDetectionHypothesisType;
import es.ehubio.proteomics.psi.mzid11.ProteinDetectionListType;
import es.ehubio.proteomics.psi.mzid11.ProteinDetectionProtocolType;
import es.ehubio.proteomics.psi.mzid11.ProteinDetectionType;
import es.ehubio.proteomics.psi.mzid11.SpectraDataType;
import es.ehubio.proteomics.psi.mzid11.SpectrumIdentificationItemRefType;
import es.ehubio.proteomics.psi.mzid11.SpectrumIdentificationItemType;
import es.ehubio.proteomics.psi.mzid11.SpectrumIdentificationListType;
import es.ehubio.proteomics.psi.mzid11.SpectrumIdentificationResultType;
import es.ehubio.proteomics.psi.mzid11.UserParamType;

/**
 * Proxy class for managing MS/MS information in an mzid file.
 */
public final class Mzid extends MsMsFile {
	private final Logger logger = Logger.getLogger(Mzid.class.getName());
	private MzIdentML mzid;
	private Map<String,Protein> mapProteins = new HashMap<>();
	private Map<Protein,String> mapSequences = new HashMap<>();	
	private Map<String,Peptide> mapPeptides = new HashMap<>();
	private Map<String,PeptideEvidenceType> mapEvidences = new HashMap<>();
	private Map<String,Peptide> mapEvidencePeptide = new HashMap<>();
	private Map<String,PeptideEvidenceType> mapProteinPeptideEvidence = new HashMap<>();
	private Map<Psm,SpectrumIdentificationItemType> mapSii = new HashMap<>();
	private Map<SpectrumIdentificationItemType,Psm> mapPsm = new HashMap<>();
	private MsMsData data;	
	private ProteinDetectionListType proteinDetectionList;
	private static final String PSIMS = "PSI-MS";	// PSI-MS id hardcoded for the momment
	
	@Override
	protected boolean checkSignatureStream(InputStream input) throws Exception {
		BufferedReader rd = new BufferedReader(new InputStreamReader(input));
		String str = rd.readLine()+rd.readLine();
		return str.contains("<MzIdentML");
	}
	
	@Override
	protected MsMsData loadStream( InputStream input, boolean loadFragments ) throws Exception {
		//logger.info("Parsing XML ...");
		JAXBContext jaxbContext = JAXBContext.newInstance(MzIdentML.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		mzid = (MzIdentML)unmarshaller.unmarshal(input);
		
		//logger.info("Building model ...");
		data = new MsMsData();
		loadProteins();
		loadPeptides();
		loadRelations();
		loadGroups();
		loadSpectra();		
		//logger.info("finished!");		
		return data;
	}		
	
	@Override
	protected boolean saveStream( OutputStream output ) throws Exception {
		//logger.info("Updating mzid data ...");
		updateOrganization();
		updateAuthor();
		updateSoftware();
		updateProteinDetectionList();
		updateProteinDetectionProtocol();
		updateSpectrumIdentificationLists();
		updateDecoys();
		updateReferences();
		
		//logger.info("Serializing to XML ...");
		JAXBContext jaxbContext = JAXBContext.newInstance(MzIdentML.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(mzid, output);
		//logger.info("finished!");
		
		return true;
	}
	
	@Override
	public List<File> getPeakRefs(String optionalPath) throws Exception {
		List<File> files = new ArrayList<>();
		for( SpectraDataType spectrum : mzid.getDataCollection().getInputs().getSpectraDatas() ) {
			if( !spectrum.getFileFormat().getCvParam().getAccession().equals("MS:1001062") ||
				!spectrum.getSpectrumIDFormat().getCvParam().getAccession().equals("MS:1000774") )
				throw new UnsupportedOperationException("Spectra format not supported");
			File file = getPeaksFile(optionalPath, spectrum.getLocation());
			String path = file.getAbsolutePath();
			loadPeaks( path, spectrum.getLocation() );
			files.add(file);
		}
		return files;
	}
	
	private File getPeaksFile(String optionalPath, String location) {
		// 1) Try spectrum path in mzid
		File origFile = new File(location);
		File file = origFile;
		if( !file.isFile() && optionalPath != null ) {
			// 2) Try spectrum file provided by the user  
			File optFile = new File(optionalPath);
			file = optFile;
			if( !file.isFile() ) {
				// 3) Try spectrum file in mzid in the directory provided by the user
				if( !optFile.isDirectory() )
					optFile = optFile.getParentFile();
				file = new File(optFile,origFile.getName());
			}
		}
		return file;
	}

	private void loadPeaks(String path, String orig) throws FileNotFoundException, IOException {		
		logger.info(String.format("Loading spectra from %s ...", path));
		List<Spectrum> mgfs = MgfFile.loadSpectra(path);
		for( Spectrum spectrum : data.getSpectra() )
			if( spectrum.getFileName().equals(orig) ) {
				Spectrum mgf = mgfs.get(Integer.parseInt(spectrum.getFileId().replaceAll("index=","")));
				spectrum.setPeaks(mgf.getPeaks());
				spectrum.setScan(mgf.getScan());
				spectrum.setRt(mgf.getRt());
				/*spectrum.setMass(mgf.getMass());
				spectrum.setCharge(mgf.getCharge());*/
			}
	}

	private void loadProteins() {
		//logger.info("Building proteins ...");
		mapProteins.clear();
		mapSequences.clear();
		Map<String,String> mapSequenceIds = new HashMap<>();
		for( DBSequenceType dbSequence : mzid.getSequenceCollection().getDBSequences() ) {
			// Protein details in mzid
			Protein protein = new Protein();									
			protein.setAccession(dbSequence.getAccession());
			if( dbSequence.getName() != null )
				protein.setName(dbSequence.getName());
			CVParamType cv = getCVParam("MS:1001088", dbSequence.getCvParamsAndUserParams());
			if( cv != null )
				protein.setDescription(cv.getValue());
			protein.setSequence(dbSequence.getSeq());
			
			// Replace protein details if fasta header is present
			HeaderParser header = Fasta.guessParser(dbSequence.getName());
			if( header != null ) {
				protein.setAccession(header.getAccession());				
				protein.setName(header.getProteinName());				
				protein.setDescription(header.getHeader());
				
				dbSequence.setAccession(header.getAccession());
				mapSequenceIds.put(dbSequence.getId(), header.getAccession());
				dbSequence.setId(header.getAccession());
				dbSequence.setName(header.getProteinName());
				if( cv == null ) {
					cv = new CVParamType();
					cv.setAccession("MS:1001088");
					cv.setName("protein description");
					cv.setCvRef(PSIMS);
					dbSequence.getCvParamsAndUserParams().add(cv);
				}
				//cv.setValue(header.getDescription());
				cv.setValue(header.getHeader());
			} else
				mapSequenceIds.put(dbSequence.getId(), dbSequence.getId());
			
			mapProteins.put(dbSequence.getId(), protein);
			mapSequences.put(protein, dbSequence.getId());
		}
		
		// Update IDs
		for( PeptideEvidenceType peptideEvidence : mzid.getSequenceCollection().getPeptideEvidences() )
			peptideEvidence.setDBSequenceRef(mapSequenceIds.get(peptideEvidence.getDBSequenceRef()));
		if( mzid.getDataCollection().getAnalysisData().getProteinDetectionList() != null )
			for( ProteinAmbiguityGroupType pag : mzid.getDataCollection().getAnalysisData().getProteinDetectionList().getProteinAmbiguityGroups() )
				for( ProteinDetectionHypothesisType pdh : pag.getProteinDetectionHypothesises() )
					if( pdh.getDBSequenceRef() != null )
						pdh.setDBSequenceRef(mapSequenceIds.get(pdh.getDBSequenceRef()));
	}
	
	private void updateDecoys() {
		for( PeptideEvidenceType peptideEvidence : mzid.getSequenceCollection().getPeptideEvidences() ) {			
			Protein protein = mapProteins.get(peptideEvidence.getDBSequenceRef());
			peptideEvidence.setIsDecoy(protein.getDecoy());
		}
	}
	
	private void loadPeptides() {
		//logger.info("Building peptides ...");
		mapPeptides.clear();
		for( PeptideType peptideType : mzid.getSequenceCollection().getPeptides() ) {
			Peptide peptide = new Peptide();
			mapPeptides.put(peptideType.getId(), peptide);
			peptide.setSequence(peptideType.getPeptideSequence());
			for( ModificationType modificationType : peptideType.getModifications() ) {
				Ptm ptm = new Ptm();
				StringBuilder builder = new StringBuilder();
				for( String residue : modificationType.getResidues() )
					builder.append(residue);
				ptm.setResidues(builder.toString());
				ptm.setPosition(modificationType.getLocation());
				ptm.setMassDelta(modificationType.getMonoisotopicMassDelta());
				for( CVParamType param : modificationType.getCvParams() )
					if( param.getCvRef().equalsIgnoreCase("UNIMOD") ) {
						ptm.setName(param.getName());
						if( ptm.getName().startsWith("Phospho") )
							ptm.setType(ProteinModificationType.PHOSPHORYLATION);
						break;
					}
				peptide.addPtm(ptm);
			}			
		}
	}
	
	private void loadRelations() {
		//logger.info("Building protein-peptide map ...");
		mapEvidencePeptide.clear();
		mapProteinPeptideEvidence.clear();
		for( PeptideEvidenceType peptideEvidence : mzid.getSequenceCollection().getPeptideEvidences() ) {
			mapEvidences.put(peptideEvidence.getId(), peptideEvidence);
			Peptide peptide = mapPeptides.get(peptideEvidence.getPeptideRef());
			Protein protein = mapProteins.get(peptideEvidence.getDBSequenceRef());
			protein.linkPeptide(peptide);
			if( peptideEvidence.isIsDecoy() )
				peptide.setDecoy(true);
			mapEvidencePeptide.put(peptideEvidence.getId(), peptide);
			mapProteinPeptideEvidence.put(protein.getAccession()+peptide.getSequence(),peptideEvidence);
		}
	}
	
	private void loadGroups() {
		//logger.info("Building protein groups ...");
		for( ProteinAmbiguityGroupType pag : mzid.getDataCollection().getAnalysisData().getProteinDetectionList().getProteinAmbiguityGroups() ) {
			AmbiguityGroup group = new AmbiguityGroup();
			for( ProteinDetectionHypothesisType pdh : pag.getProteinDetectionHypothesises() ) {
				if( pdh.getPeptideHypothesises().isEmpty() )
					continue;
				PeptideHypothesisType ph = pdh.getPeptideHypothesises().get(0);
				PeptideEvidenceType peptideEvidence = mapEvidences.get(ph.getPeptideEvidenceRef());
				Protein protein = mapProteins.get(peptideEvidence.getDBSequenceRef());
				protein.linkGroup(group);
			}
		}
	}

	private void loadSpectra() {
		//logger.info("Building spectra ...");
		Map<String,String> mapSpectraFile = new HashMap<>();
		for( SpectraDataType spectrum : mzid.getDataCollection().getInputs().getSpectraDatas() )
			mapSpectraFile.put(spectrum.getId(), spectrum.getLocation());
		
		Set<Spectrum> spectra = new HashSet<>();
		mapSii.clear();
		mapPsm.clear();
		for( SpectrumIdentificationListType sil : mzid.getDataCollection().getAnalysisData().getSpectrumIdentificationLists() ) {
			MeasureIds ids = getMeasureIds(sil);
			for( SpectrumIdentificationResultType sir : sil.getSpectrumIdentificationResults() ) {
				Spectrum spectrum = new Spectrum();			
				spectrum.setFileName(mapSpectraFile.get(sir.getSpectraDataRef()));
				loadSpectrum(spectrum,sir);
				for( SpectrumIdentificationItemType sii : sir.getSpectrumIdentificationItems() ) {
					Psm psm = new Psm();
					psm.linkSpectrum(spectrum);					
					loadPsm(psm,sii,ids);					
					mapSii.put(psm, sii);
					mapPsm.put(sii, psm);
					if( sii.getPeptideEvidenceReves() == null )
						continue;
					for( PeptideEvidenceRefType peptideEvidenceRefType : sii.getPeptideEvidenceReves() ) {
						Peptide peptide = mapEvidencePeptide.get(peptideEvidenceRefType.getPeptideEvidenceRef());
						if( peptide != null ) {
							psm.linkPeptide(peptide);
							break;
						}
					}
				}
				spectra.add(spectrum);
			}
		}
		data.loadFromSpectra(spectra);
	}	

	private void loadSpectrum(Spectrum spectrum, SpectrumIdentificationResultType sir) {
		spectrum.setFileId(sir.getSpectrumID());
		CVParamType cv = getCVParam("MS:1000796", sir.getCvParamsAndUserParams());
		if( cv != null ) {
			spectrum.setTitle(cv.getValue());
			Pattern pattern = Pattern.compile("RTINSECONDS=([0-9]*\\.?[0-9]*)",Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(cv.getValue());
			if( matcher.find() )
				spectrum.setRt(Double.parseDouble(matcher.group(1)));
			pattern = Pattern.compile("scan=([0-9]*)",Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(cv.getValue());
			if( matcher.find() )
				spectrum.setScan(matcher.group(1));
		}
		cv = getCVParam("MS:1001114", sir.getCvParamsAndUserParams());
		if( cv == null )
			return;
		spectrum.setRt(Double.parseDouble(cv.getValue()));
	}
	
	private void loadPsm(Psm psm, SpectrumIdentificationItemType sii, MeasureIds ids) {
		psm.setCharge(sii.getChargeState());
		psm.setExpMz(sii.getExperimentalMassToCharge());
		psm.setCalcMz(sii.getCalculatedMassToCharge());
		psm.setRank(sii.getRank());
		psm.setPassThreshold(sii.isPassThreshold());
		loadScores(psm, sii);		
		loadIons(psm,sii,ids);
	}
	
	private MeasureIds getMeasureIds( SpectrumIdentificationListType sil ) {
		if( sil.getFragmentationTable() == null )
			return null;
		MeasureIds ids = new MeasureIds();
		for( MeasureType measure : sil.getFragmentationTable().getMeasures() ) {
			CVParamType cv = getCVParam("MS:1001225", measure.getCvParams());
			if( cv != null ) {
				ids.mz = measure.getId();
				continue;
			}
			cv = getCVParam("MS:1001227", measure.getCvParams());
			if( cv != null ) {
				ids.error = measure.getId();
				continue;
			}
			cv = getCVParam("MS:1001226", measure.getCvParams());
			if( cv != null )
				ids.intensity = measure.getId();
		}
		return ids;
	}

	private void loadIons(Psm psm, SpectrumIdentificationItemType sii, MeasureIds ids) {
		if( ids == null || sii.getFragmentation() == null )
			return;
		
		for( IonTypeType ion : sii.getFragmentation().getIonTypes() ) {
			List<FragmentIon> fragments = new ArrayList<>();
			IonType type = IonType.getByAccession(ion.getCvParam().getAccession());
			for( Number i : ion.getIndices() ) {
				FragmentIon fragment = new FragmentIon();
				fragment.setIndex(i.intValue());
				fragment.setCharge(ion.getCharge());
				fragment.setType(type);
				fragments.add(fragment);
			}
			for( FragmentArrayType array : ion.getFragmentArraies() ) {
				for( int i = 0; i < array.getValues().size(); i++ ) {
					FragmentIon fragment = fragments.get(i);
					if( array.getMeasureRef().equals(ids.mz) )
						fragment.setMzExp(array.getValues().get(i));
					else if( array.getMeasureRef().equals(ids.error) )
						fragment.setMzError(array.getValues().get(i));
					else if( array.getMeasureRef().equals(ids.intensity) )
						fragment.setIntensity(array.getValues().get(i));
				}
			}
			psm.getIons().addAll(fragments);
		}
	}

	private void loadScores( Psm psm, SpectrumIdentificationItemType sii ) {
		for( AbstractParamType param : sii.getCvParamsAndUserParams() ) {
			ScoreType type;
			if( !CVParamType.class.isInstance(param) )
				type = ScoreType.getByName(param.getName());
			else {
				CVParamType cv = (CVParamType)param;
				type = ScoreType.getByAccession(cv.getAccession());
			}
			if( type != null )				
				psm.setScore(new Score(type, param.getName(), Double.parseDouble(param.getValue())));
		}
	}	
	
	private CVParamType getCVParam( String accession, List<? extends AbstractParamType> params ) {
		for( AbstractParamType param : params ) {
			if( !CVParamType.class.isInstance(param) )
				continue;
			CVParamType cv = (CVParamType)param;
			if( cv.getAccession().equalsIgnoreCase(accession) )
				return cv;
		}
		return null;
	}
	
	private UserParamType getUserParam( String name, List<? extends AbstractParamType> params ) {
		for( AbstractParamType param : params ) {
			if( !UserParamType.class.isInstance(param) )
				continue;
			UserParamType up = (UserParamType)param;
			if( up.getName().equalsIgnoreCase(name) )
				return up;
		}
		return null;
	}
	
	private void updateReferences() {
		BibliographicReferenceType paper= new BibliographicReferenceType();
		paper.setTitle("PAnalyzer: A software tool for protein inference in shotgun proteomics");
		paper.setName(paper.getTitle());
		paper.setAuthors("Gorka Prieto, Kerman Aloria, Nerea Osinalde, Asier Fullaondo, Jesus M. Arizmendi and Rune Matthiesen");
		paper.setDoi("10.1186/1471-2105-13-288");
		paper.setId(paper.getDoi());
		paper.setVolume("13");
		paper.setIssue("288");
		paper.setYear(2012);
		paper.setPublication("BMC Bioinformatics");
		paper.setPublisher("BioMed Central Ltd.");
		BibliographicReferenceType remove = null;
		for( BibliographicReferenceType reference : mzid.getBibliographicReferences() )
			if( reference.getId().equals(paper.getId() )) {
				remove = reference;
				break;
			}
		if( remove != null )
			mzid.getBibliographicReferences().remove(remove);
		mzid.getBibliographicReferences().add(paper);
	}

	private void updateProteinDetectionList() {
		proteinDetectionList = new ProteinDetectionListType();
		proteinDetectionList.setId("PDL_EhuBio");
		
		CVParamType cvThreshold = new CVParamType();
		cvThreshold.setAccession("MS:1002415");
		cvThreshold.setName("protein group passes threshold");
		cvThreshold.setCvRef(PSIMS);
		cvThreshold.setValue("true");
		
		int pagCount = 0;
		for( AmbiguityGroup group : data.getGroups() ) {
			if( group.getConfidence() == Protein.Confidence.NON_CONCLUSIVE || group.getItems().isEmpty() )
				continue;
			pagCount++;
			ProteinAmbiguityGroupType pag = new ProteinAmbiguityGroupType();
			pag.setId(String.format("PAG_%d", group.getId()));
			pag.setName(group.buildName());
			pag.getCvParamsAndUserParams().add(cvThreshold);
			for( Protein protein : group.getProteins() ) {		
				pag.getProteinDetectionHypothesises().add(buildPdh(protein));
				// Include non-conclusive proteins (redundantly)
				Set<Protein> nonConclusive = new HashSet<>();
				for( Peptide peptide : protein.getPeptides() ) {
					if( peptide.getConfidence() != Peptide.Confidence.NON_DISCRIMINATING )
						continue;					
					for( Protein protein2 : peptide.getProteins() )
						if( protein2.getConfidence() == Protein.Confidence.NON_CONCLUSIVE )
							nonConclusive.add(protein2);					
				}
				for( Protein protein2 : nonConclusive )
					pag.getProteinDetectionHypothesises().add(buildPdh(protein2));
			}
			updateScores(group, pag.getCvParamsAndUserParams());
			proteinDetectionList.getProteinAmbiguityGroups().add(pag);
		}
		
		CVParamType cvCount = new CVParamType();
		cvCount.setAccession("MS:1002404");
		cvCount.setName("count of identified proteins");
		cvCount.setCvRef(PSIMS);
		cvCount.setValue(""+pagCount);
		proteinDetectionList.getCvParamsAndUserParams().add(cvCount);
		
		mzid.getDataCollection().getAnalysisData().setProteinDetectionList(proteinDetectionList);
	}
	
	private ProteinDetectionHypothesisType buildPdh( Protein protein ) {
		ProteinDetectionHypothesisType pdh = new ProteinDetectionHypothesisType();
		pdh.setId(String.format("PDH_%s", protein.getAccession()));
		pdh.setPassThreshold(true);
		pdh.setDBSequenceRef(mapSequences.get(protein));
		CVParamType cvEvidence = new CVParamType();
		CVParamType cvLeading = new CVParamType();
		cvEvidence.setCvRef(PSIMS);
		cvLeading.setCvRef(PSIMS);
		switch( protein.getConfidence() ) {			
			case CONCLUSIVE:
				cvEvidence.setAccession("MS:1002213");
				cvEvidence.setName("PAnalyzer:conclusive protein");
				cvLeading.setAccession("MS:1002401");
				cvLeading.setName("leading protein");
				break;
			case INDISTINGUISABLE_GROUP:
				cvEvidence.setAccession("MS:1002214");
				cvEvidence.setName("PAnalyzer:indistinguishable protein");
				cvLeading.setAccession("MS:1002401");
				cvLeading.setName("leading protein");
				break;
			case AMBIGUOUS_GROUP:
				cvEvidence.setAccession("MS:1002216");
				cvEvidence.setName("PAnalyzer:ambiguous group member");
				cvLeading.setAccession("MS:1002401");
				cvLeading.setName("leading protein");
				break;
			case NON_CONCLUSIVE:
				cvEvidence.setAccession("MS:1002215");
				cvEvidence.setName("PAnalyzer:non-conclusive protein");
				cvLeading.setAccession("MS:1002402");
				cvLeading.setName("non-leading protein");
				break;
		}
		if( cvEvidence.getAccession() != null ) {
			pdh.getCvParamsAndUserParams().add(cvEvidence);
			pdh.getCvParamsAndUserParams().add(cvLeading);
		}
		pdh.setPassThreshold(protein.isPassThreshold());
		updateScores(protein, pdh.getCvParamsAndUserParams());
		for( Peptide peptide : protein.getPeptides() )
			pdh.getPeptideHypothesises().add(buildPh(protein,peptide));
		return pdh;
	}

	private PeptideHypothesisType buildPh(Protein protein, Peptide peptide) {
		PeptideHypothesisType ph = new PeptideHypothesisType();
		PeptideEvidenceType peptideEvidence = mapProteinPeptideEvidence.get(protein.getAccession()+peptide.getSequence());
		if( peptide.getDecoy() != null )
			peptideEvidence.setIsDecoy(peptide.getDecoy());
		ph.setPeptideEvidenceRef(peptideEvidence.getId());
		for( Psm psm : peptide.getPsms() ) {
			SpectrumIdentificationItemRefType siiRef = new SpectrumIdentificationItemRefType();
			siiRef.setSpectrumIdentificationItemRef(mapSii.get(psm).getId());
			ph.getSpectrumIdentificationItemReves().add(siiRef);
		}
		return ph;
	}

	private void updateProteinDetectionProtocol() {
		if( data.getSoftware() == null )
			return;
		
		ProteinDetectionProtocolType proteinDetectionProtocol = new ProteinDetectionProtocolType();
		proteinDetectionProtocol.setId("PDP_EhuBio");
		proteinDetectionProtocol.setAnalysisSoftwareRef(data.getSoftware().getId());
		proteinDetectionProtocol.setAnalysisParams(data.getAnalysisParams());
		if( data.getThresholds() == null ) {
			CVParamType cv = new CVParamType();
			cv.setAccession("MS:1001494");
			cv.setName("no threshold");
			cv.setCvRef(PSIMS);
			data.setThreshold(cv);
		}
		proteinDetectionProtocol.setThreshold(data.getThresholds());
		mzid.getAnalysisProtocolCollection().setProteinDetectionProtocol(proteinDetectionProtocol);;
		
		ProteinDetectionType proteinDetection = new ProteinDetectionType();
		proteinDetection.setId("PD_EhuBio");
		proteinDetection.setProteinDetectionListRef(proteinDetectionList.getId());
		proteinDetection.setProteinDetectionProtocolRef(proteinDetectionProtocol.getId());
		for( SpectrumIdentificationListType sil : mzid.getDataCollection().getAnalysisData().getSpectrumIdentificationLists() ) {
			InputSpectrumIdentificationsType inputSpectrumIdentifications = new InputSpectrumIdentificationsType();
			inputSpectrumIdentifications.setSpectrumIdentificationListRef(sil.getId());
			proteinDetection.getInputSpectrumIdentifications().add(inputSpectrumIdentifications);			
		}
		mzid.getAnalysisCollection().setProteinDetection(proteinDetection);
	}
	
	private void updateSpectrumIdentificationLists() {
		for( SpectrumIdentificationListType sil : mzid.getDataCollection().getAnalysisData().getSpectrumIdentificationLists() )
			updateSprectrumIdentificationList(sil);			
	}
	
	private void updateSprectrumIdentificationList( SpectrumIdentificationListType sil ) {
		Set<SpectrumIdentificationItemType> remove = new HashSet<>();
		for( SpectrumIdentificationResultType sir : sil.getSpectrumIdentificationResults() ) {
			remove.clear();
			for( SpectrumIdentificationItemType sii : sir.getSpectrumIdentificationItems() )
				if( sii.getPeptideEvidenceReves().isEmpty() )
					remove.add(sii);
				else {
					Psm psm = mapPsm.get(sii);
					sii.setPassThreshold(psm.isPassThreshold());
					updateScores(psm, sii.getCvParamsAndUserParams());
				}
			sir.getSpectrumIdentificationItems().removeAll(remove);
		}
	}
	
	private void updateScores( DecoyBase item, List<AbstractParamType> list ) {
		for( Score score : item.getScores() ) {
			if( score.getType().getAccession() != null ) {
				CVParamType cv = getCVParam(score.getType().getAccession(), list);
				if( cv == null ) {					
					cv = new CVParamType();
					cv.setAccession(score.getType().getAccession());
					cv.setName(score.getType().getName());
					cv.setCvRef(PSIMS);
					list.add(cv);
				}
				cv.setValue(String.format("%s", score.getValue()));
			} else {
				UserParamType up = getUserParam(score.getType().getName(), list);
				if( up == null ) {
					up = new UserParamType();
					up.setName(score.getType().getName());
					up.setType("xsd:double");
					list.add(up);
				}
				up.setValue(String.format("%s", score.getValue()));
			}
		}
	}

	private void updateSoftware() {
		if( data.getSoftware() == null )
			return;
		AnalysisSoftwareType remove = null;
		for( AnalysisSoftwareType s : mzid.getAnalysisSoftwareList().getAnalysisSoftwares() ) {
			if( s.getId().equalsIgnoreCase(data.getSoftware().getId()) ) {
				remove = s;
				break;
			}
		}
		if( remove != null )
			mzid.getAnalysisSoftwareList().getAnalysisSoftwares().remove(remove);
		mzid.getAnalysisSoftwareList().getAnalysisSoftwares().add(data.getSoftware());
	}
    
	private void updateAuthor() {
		if( data.getAuthor() == null )
			return;
		
		AbstractContactType remove = null;
		for( AbstractContactType contact : mzid.getAuditCollection().getPersonsAndOrganizations() ) {
			if( !PersonType.class.isInstance(contact) )
				continue;
			PersonType person = (PersonType)contact;
			if( person.getId().equals(data.getAuthor().getId()) ) {
				remove = person;
				break;
			}
		}
		if( remove != null )
			mzid.getAuditCollection().getPersonsAndOrganizations().remove(remove);
		mzid.getAuditCollection().getPersonsAndOrganizations().add(data.getAuthor());
	}

	private void updateOrganization() {
		if( data.getOrganization() == null )
			return;
		
		AbstractContactType remove = null;
		for( AbstractContactType contact : mzid.getAuditCollection().getPersonsAndOrganizations() ) {
			if( !OrganizationType.class.isInstance(contact) )
				continue;
			OrganizationType organization2 = (OrganizationType)contact;
			if( organization2.getId().equalsIgnoreCase(data.getOrganization().getId()) ) {
				remove = organization2;
				break;
			}
		}
		if( remove != null )
			mzid.getAuditCollection().getPersonsAndOrganizations().remove(remove);
		mzid.getAuditCollection().getPersonsAndOrganizations().add(data.getOrganization());
	}
	
	private static class MeasureIds {
		public String mz;
		public String error;
		public String intensity;
	}

	@Override
	public String getFilenameExtension() {
		return "mzid";
	}
}
