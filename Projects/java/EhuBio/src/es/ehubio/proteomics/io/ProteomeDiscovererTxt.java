package es.ehubio.proteomics.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ehubio.io.CsvReader;
import es.ehubio.io.Streams;
import es.ehubio.model.ProteinModificationType;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Ptm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.Spectrum;

public class ProteomeDiscovererTxt extends MsMsFile {
	@Override
	public boolean checkSignatureStream(InputStream input) throws Exception {
		BufferedReader rd = new BufferedReader(new InputStreamReader(input));
		String str = rd.readLine();
		return str.contains("Annotated Sequence") && str.contains("Protein Accessions");
	}
	
	@Override
	protected MsMsData loadPath(String path, boolean loadFragments) throws Exception {
		CsvReader input = new CsvReader("\t", true, true);
		input.open(Streams.getTextReader(path));		
		MsMsData data;
		
		File file = new File(path.replaceAll("\\..*", ".rel.gz"));		
		if( file.exists() ) {
			CsvReader relations = new CsvReader(" ", false, true);
			relations.open(Streams.getTextReader(file));
			data = loadStream(input, loadFragments, relations);
			relations.close();
		} else
			data = loadStream(input, loadFragments, null);
		
		input.close();
		return data;
	}

	public MsMsData loadStream(CsvReader input, boolean loadFragments, CsvReader relations ) throws Exception {
		Map<String,Spectrum> scans = new HashMap<>();
		Map<String,Peptide> peptides = new HashMap<>();
		Map<String,Protein> proteins = new HashMap<>();
		Map<String,List<Protein>> ext = null;
		
		if( relations != null )
			ext = loadExternalRelations(relations, proteins);
		
		while( input.readLine() != null ) {
			String scan = input.getField("First Scan");
			Spectrum spectrum = scans.get(scan);
			if( spectrum == null ) {
				spectrum = loadSpectrum(input, scan);
				scans.put(scan, spectrum);
			}
			Peptide newPeptide = loadPeptide(input);
			Peptide peptide = peptides.get(newPeptide.getUniqueString());
			if( peptide == null ) {
				peptides.put(newPeptide.getUniqueString(), newPeptide);
				peptide = newPeptide;
			}			
			Psm psm = loadPsm(input);
			psm.linkSpectrum(spectrum);
			psm.linkPeptide(peptide);
			if( ext == null )
				loadProteins(input,proteins,peptide);
			else
				loadProteins(ext,peptide);
		}
		
		MsMsData data = new MsMsData();
		data.loadFromSpectra(scans.values());
		return data;
	}		

	private Map<String, List<Protein>> loadExternalRelations( CsvReader rel, Map<String, Protein> proteins) throws IOException {
		logger.info("Loading peptide-protein relations from external file ...");
		Map<String, List<Protein>> map = new HashMap<>();
		while( rel.readLine() != null ) {
			List<Protein> list = new ArrayList<>();
			for( String acc : rel.getField(1).split(";") ) {
				Protein protein = proteins.get(acc);
				if( protein == null ) {
					protein = new Protein();
					protein.setAccession(acc);
					proteins.put(acc, protein);
				}
				list.add(protein);
			}
			map.put(rel.getField(0).toLowerCase().replaceAll("\\[.*?\\]",""), list);
		}
		return map;
	}

	private Spectrum loadSpectrum( CsvReader csv, String scan ) {
		Spectrum spectrum = new Spectrum();
		spectrum.setScan(scan);
		spectrum.setFileId(scan);
		spectrum.setFileName(csv.getField("Spectrum File"));
		spectrum.setRt(csv.getDoubleField("RT [min]"));
		return spectrum;
	}
	
	private Peptide loadPeptide(CsvReader csv) throws Exception {
		Peptide peptide = new Peptide();
		peptide.setSequence(csv.getField("Annotated Sequence"));
		String modString = csv.getField("Modifications");
		peptide.setUniqueString(peptide.getSequence()+modString);
		if( !modString.isEmpty() ) {
			String[] mods = modString.split("; ");
			for( String mod : mods ) {
				Ptm ptm = new Ptm();
				Matcher matcher = ptmPattern.matcher(mod);
				if( matcher.find() ) {
					ptm.setResidues(matcher.group(1));
					if( !matcher.group(2).isEmpty() )
						ptm.setPosition(Integer.parseInt(matcher.group(2)));
					ptm.setName(matcher.group(3));
					ProteinModificationType type = ProteinModificationType.getByName(ptm.getName());
					if( type != null ) { 
						ptm.setMassDelta(type.getMass());
						ptm.setType(type);
					}
				} else
					ptm.setName(mod);
				peptide.addPtm(ptm);
			}
		}
		return peptide;
	}
	
	private Psm loadPsm( CsvReader csv ) {
		Psm psm = new Psm();
		psm.setCharge(csv.getIntField("Charge"));
		psm.setExpMz(csv.getDoubleField("m/z [Da]"));
		psm.setRank(csv.getIntField("Rank"));
		Score score = new Score(ScoreType.SEQUEST_XCORR, csv.getDoubleField("XCorr"));
		psm.putScore(score);
		return psm;
	}
	
	private void loadProteins(CsvReader csv, Map<String, Protein> proteins, Peptide peptide) {
		String accString = csv.getField("Protein Accessions");
		String[] accs = accString.split("; ");
		for( String acc : accs ) {
			Protein protein = proteins.get(acc);
			if( protein == null ) {
				protein = new Protein();
				protein.setAccession(acc);
				proteins.put(acc, protein);
			}
			peptide.linkProtein(protein);
		}
	}
	
	private void loadProteins(Map<String, List<Protein>> ext, Peptide peptide) {
		List<Protein> proteins = ext.get(peptide.getSequence().toLowerCase());
		if( proteins == null || proteins.isEmpty() ) {
			throw new AssertionError(String.format("No proteins for peptide %s", peptide.getSequence()));
			/*logger.warning(String.format("No proteins for peptide %s", peptide.getSequence()));
			return;*/
		}
		for( Protein protein : proteins )
			peptide.linkProtein(protein);
	}

	@Override
	public String getFilenameExtension() {
		return "txt";
	}

	private final static Logger logger = Logger.getLogger(ProteomeDiscovererTxt.class.getName());
	private final Pattern ptmPattern = Pattern.compile("(\\D+)(\\d*)\\((.*)\\)");	
}
