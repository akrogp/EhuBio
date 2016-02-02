package es.ehubio.proteomics.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import es.ehubio.Numbers;
import es.ehubio.model.ProteinModificationType;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Ptm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.Spectrum;

public class MascotDat extends MsMsFile {

	@Override
	public String getFilenameExtension() {
		return "dat";
	}

	@Override
	protected boolean checkSignatureStream(InputStream input) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		return br.readLine().toLowerCase().contains("mascot version");
	}
	
	@Override
	protected MsMsData loadStream(InputStream input, boolean loadFragments) throws Exception {				
		Map<String, Spectrum> mapSpectra = new HashMap<>();
		Map<String, Peptide> mapPeptide = new HashMap<>();
		Map<String, Protein> mapProtein = new HashMap<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		String mgf = getMgf(br);
		Map<String, String> mapExp = loadExp(br);
		String line;
		String[] fields, lfields;
		while( (line=br.readLine()) != null ) {
			if( !mapSpectra.isEmpty() && line.startsWith("--") )
				break;
			if( line.length() < 20 || line.charAt(0) != 'q' || !Character.isDigit(line.charAt(1)) )
				continue;			
			fields = line.split("=",2);
			if( fields.length != 2 )
				continue;
			lfields = fields[0].split("_");			
			if( lfields.length != 2 )
				continue;
			String query = lfields[0].substring(1);			
			Spectrum spectrum = getSpectrum(query, mgf, mapSpectra);
			int rank = Integer.parseInt(lfields[1].substring(1));
			fields = fields[1].split(";");
			Peptide peptide = getPeptide(fields[0], mapPeptide);
			Psm psm = getPsm(rank,mapExp.get(query),fields[0]);
			psm.linkSpectrum(spectrum);
			psm.linkPeptide(peptide);
			for( String protLine : fields[1].split(",") ) {
				Protein protein = getProtein(protLine, mapProtein);
				protein.linkPeptide(peptide);
			}
		}
		MsMsData data = new MsMsData();
		data.loadFromSpectra(mapSpectra.values());
		return data;
	}

	private Map<String, String> loadExp(BufferedReader br) throws IOException {
		Map<String, String> mapCharges = new HashMap<>();
		String line;
		String[] fields;
		while( (line=br.readLine()) != null ) {
			if( !mapCharges.isEmpty() && line.startsWith("--") )
				break;
			if( !line.startsWith("qexp") )
				continue;
			fields = line.split("=");
			mapCharges.put(fields[0].substring(4),fields[1]);
		}
		return mapCharges;
	}

	private Psm getPsm(int rank, String exp, String str) throws ParseException {
		String[] fields = str.split(",");
		Psm psm = new Psm();
		psm.setRank(rank);
		psm.putScore(new Score(ScoreType.MASCOT_SCORE, Double.parseDouble(fields[7])));
		if( exp != null ) {
			String[] subs = exp.split(",");
			psm.setExpMz(Numbers.parseDouble(subs[0]));
			psm.setCharge(Integer.parseInt(subs[1].substring(0, subs[1].length()-1)));
		}
		return psm;
	}

	private Protein getProtein(String protLine, Map<String, Protein> mapProtein) {
		String[] fields = protLine.split(":");
		String acc = fields[0].replaceAll("[ \\t\"]", "");
		Protein protein = mapProtein.get(acc);
		if( protein != null )
			return protein;
		protein = new Protein();
		protein.setAccession(acc);
		mapProtein.put(acc, protein);
		return protein;
	}

	private Peptide getPeptide(String pepLine, Map<String, Peptide> mapPeptide) {
		String[] fields = pepLine.split(",");		
		String seq = fields[4];
		Peptide newPeptide = new Peptide();
		newPeptide.setSequence(seq);
		String mods = fields[6];
		if( mods.charAt(0) == 1 || mods.endsWith("1") )
			throw new UnsupportedOperationException("Terminal modifications not supported");
		for( int i = 1; i < mods.length()-1; i++ ) {
			if( mods.charAt(i) == '0' )
				continue;
			char aa = Character.toLowerCase(seq.charAt(i-1)); 
			if( aa != 'm' && aa != 'x' )
				//throw new UnsupportedOperationException("Only oxidation of methionine is supported");
				throw new UnsupportedOperationException(String.format("Modification not supported: %s, %s", seq, mods));
			Ptm ptm = new Ptm();
			ptm.setPosition(i);
			ptm.setType(ProteinModificationType.OXIDATION);
			ptm.guessMissing(seq);
			newPeptide.addPtm(ptm);
		}
		Peptide peptide = mapPeptide.get(newPeptide.getUniqueString());
		if( peptide != null )
			return peptide;
		mapPeptide.put(newPeptide.getUniqueString(), newPeptide);
		return newPeptide;
	}

	private Spectrum getSpectrum(String query, String mgf, Map<String, Spectrum> mapSpectra) {
		Spectrum spectrum = mapSpectra.get(query);
		if( spectrum != null )
			return spectrum;
		spectrum = new Spectrum();
		spectrum.setFileName(mgf);
		spectrum.setFileId(query);
		spectrum.setScan(query);
		mapSpectra.put(query, spectrum);
		return spectrum;
	}

	private String getMgf(BufferedReader br) throws IOException {
		String line;
		while( (line=br.readLine()) != null )
			if( line.startsWith("FILE=") )
				return line.split("=")[1];
		return null;
	}
	
	//private static final Logger logger = Logger.getLogger(MascotDat.class.getName());
}
