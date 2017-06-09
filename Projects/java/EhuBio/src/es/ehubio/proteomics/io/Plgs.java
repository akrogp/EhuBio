package es.ehubio.proteomics.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import es.ehubio.io.Streams;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Ptm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.Spectrum;
import es.ehubio.proteomics.waters.plgs.HIT;
import es.ehubio.proteomics.waters.plgs.MASSMATCH;
import es.ehubio.proteomics.waters.plgs.MATCHMODIFIER;
import es.ehubio.proteomics.waters.plgs.PARAM;
import es.ehubio.proteomics.waters.plgs.PEPTIDE;
import es.ehubio.proteomics.waters.plgs.PROTEIN;
import es.ehubio.proteomics.waters.plgs.QUERYMASS;
import es.ehubio.proteomics.waters.plgs.RESULT;

public final class Plgs extends MsMsFile {
	
	@Override
	protected boolean checkSignatureStream(InputStream input) throws Exception {
		byte[] bytes = new byte[100];
		input.read(bytes);
		return new String(bytes).contains("<RESULT");
	}
	
	@Override
	protected MsMsData loadPath(String path, boolean loadFragments) throws Exception {
		File txtFile = new File(path.replaceAll("workflow.xml", "Log.txt"));
		
		if( txtFile.isFile() )
			loadThresholds(txtFile);
		
		Reader rd = Streams.getTextReader(path);
		MsMsData data = loadData(rd, loadFragments);		
		rd.close();
		data.mergeDuplicatedPeptides();
		data.updateRanks(ScoreType.PSM_PLGS_SCORE);
		
		return data;
	}

	private MsMsData loadData(Reader rd, boolean loadFragments) throws JAXBException, IOException, ParseException {
		JAXBContext jaxbContext = JAXBContext.newInstance(RESULT.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		
		BufferedReader br = new BufferedReader(rd);
		br.readLine();	// Skip first line
		RESULT result = (RESULT)unmarshaller.unmarshal(br);				
		Map<Integer, Protein> mapProteins = loadProteins(result);
		Map<Integer, Psm> mapPsms = loadPsms(result); 
		loadPeptides(result, mapProteins, mapPsms);			
		
		Set<Spectrum> spectra = new HashSet<>();
		for( Psm psm : mapPsms.values() )
			spectra.add(psm.getSpectrum());
		MsMsData data = new MsMsData();
		data.loadFromSpectra(spectra);
		
		return data;
	}

	private void loadPeptides(RESULT result, Map<Integer, Protein> mapProteins, Map<Integer, Psm> mapPsms) throws ParseException {
		for( PEPTIDE p : result.getPEPTIDE() ) {
			Protein protein = mapProteins.get(p.getPROTID().intValue());
			if( protein == null )
				continue;
			Psm psm = mapPsms.get(p.getQUERYMASSID().intValue());
			if( psm == null )
				continue;
			Peptide peptide = new Peptide();			
			peptide.setSequence(p.getSEQUENCE());
			psm.linkPeptide(peptide);
			protein.linkPeptide(peptide);
			for( MATCHMODIFIER mod : p.getMATCHMODIFIER() ) {
				Ptm ptm = new Ptm();
				int i = mod.getNAME().indexOf('+');
				ptm.setName(i<0?mod.getNAME():mod.getNAME().substring(0, i));
				ptm.setPosition(mod.getPOS().intValue());
				ptm.guessMissing(peptide.getSequence());
				peptide.addPtm(ptm);
			}
		}
	}

	private Map<Integer, Psm> loadPsms(RESULT result) {		
		String raw = "unknown";
		for( PARAM param : result.getPARAMS().getPARAM() )
			if( param.getNAME().equalsIgnoreCase("RawFile") ) {
				raw = param.getVALUE();
				break;
			}
		
		Map<Integer, Psm> mapPsms = new HashMap<>();
		for( QUERYMASS mass : result.getQUERYMASS() ) {
			MASSMATCH match = mass.getMASSMATCH();
			if( match == null )
				continue;
			
			Psm psm = new Psm();
			psm.setExpMz(mass.getMASS().doubleValue());
			psm.setMassPpm(match.getMASSERRORPPM().doubleValue());
			psm.setCharge(mass.getCHARGE().intValue());
			double score = match.getSCORE().doubleValue();
			psm.putScore(new Score(ScoreType.PSM_PLGS_SCORE, score));
			if( isUsingThresholds() ) {
				int num;
				if( score >= YellowGreenTh ) num = 3;
				else if( score >= RedYellowTh ) num = 2;
				else num = 1;
				psm.putScore(new Score(ScoreType.PSM_PLGS_COLOR, num));
			}
			
			Spectrum spectrum = new Spectrum();
			spectrum.setFileName(raw);
			spectrum.setFileId(mass.getLEID().toString());
			spectrum.setRt(mass.getRETENTIONTIME().doubleValue());
			spectrum.setIntensity(mass.getINTENSITY().doubleValue());
			
			psm.linkSpectrum(spectrum);
			
			mapPsms.put(mass.getID().intValue(), psm);
		}
		return mapPsms;
	}

	private Map<Integer, Protein> loadProteins(RESULT result) {
		Map<Integer, Protein> mapProteins = new HashMap<>();		
		for( HIT hit : result.getHIT() )
			for( PROTEIN p : hit.getPROTEIN() ) {
				Protein protein = mapProteins.get(p.getID().intValue());
				if( protein != null )
					continue;
				protein = new Protein();
				mapProteins.put(p.getID().intValue(), protein);
				protein.setAccession(p.getACCESSION());
				protein.setName(p.getENTRY());
				protein.setDescription(p.getDESCRIPTION());
				protein.setSequence(p.getSEQUENCE());
				protein.putScore(new Score(ScoreType.PROTEIN_PLGS_SCORE, p.getSCORE().doubleValue()));
			}
		return mapProteins;
	}

	private void loadThresholds(File txtFile) throws IOException {		
		BufferedReader rd = new BufferedReader(Streams.getTextReader(txtFile));
		String line;
		while( (line=rd.readLine()) != null && (YellowGreenTh == null || RedYellowTh == null) ) {
			if( YellowGreenTh == null )
				YellowGreenTh = getThreshold(line, YELLOW_GREEN_TH);
			if( RedYellowTh == null )
				RedYellowTh = getThreshold(line, RED_YELLOW_TH);
		}
		rd.close();
		
		if( isUsingThresholds() )
			logger.info(String.format("Loaded threshols from %s: Red-Yellow=%s Yellow-Green=%s", txtFile.getName(), RedYellowTh, YellowGreenTh));
	}
	
	private Double getThreshold( String line, String name ) {
		int i = line.indexOf(name);
		if( i < 0 )
			return null;
		Matcher matcher = thPattern.matcher(line.substring(i));
		if( !matcher.find() )
			return null;
		return Double.parseDouble(matcher.group(1));
	}
	
	public boolean isUsingThresholds() {
		return YellowGreenTh != null && RedYellowTh != null;
	}

	@Override
	public String getFilenameExtension() {
		return "xml";
	}

	private Double YellowGreenTh;
	private Double RedYellowTh;
	private static final String RED_YELLOW_TH = "Red-Yellow Threshold";
	private static final String YELLOW_GREEN_TH = "Yellow-Green Threshold";
	private static final Pattern thPattern = Pattern.compile(".*?(\\d+(?:\\.\\d+)?).*");
	private static final Logger logger = Logger.getLogger(Plgs.class.getName());
}
