package es.ehubio.proteomics.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Ptm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.Spectrum;

public class MayuCsv extends MsMsFile {

	@Override
	protected MsMsData loadStream(InputStream input, boolean loadFragments) throws Exception {
		data = new MsMsData();
		BufferedReader rd = new BufferedReader(new InputStreamReader(input));
		String line;
		Set<Spectrum> spectra = new HashSet<>();
		Map<String, Peptide> mapPeptide = new HashMap<>();
		Map<String, Protein> mapProtein = new HashMap<>();
		while( (line=rd.readLine()) != null ) {
			String[] fields = line.split(",");
			if( fields.length != 5 )
				throw new Exception("Incorrect file format");
			Spectrum spectrum = new Spectrum();
			spectra.add(spectrum);
			Psm psm = new Psm();
			psm.linkSpectrum(spectrum);
			psm.setScore(new Score(ScoreType.PROPHET_PROBABILITY,Double.parseDouble(fields[4])));
			String peptideId = fields[1]+fields[3];
			Peptide peptide = mapPeptide.get(peptideId);
			if( peptide == null ) {
				peptide = new Peptide();
				mapPeptide.put(peptideId, peptide);
				peptide.setSequence(fields[1]);
				String[] mods = fields[3].split(":");
				for( String mod : mods ) {
					if( mod.isEmpty() )
						continue;
					Ptm ptm = new Ptm();
					String[] subfields = mod.split("=");					
					ptm.setPosition(Integer.parseInt(subfields[0]));
					ptm.setMassDelta(Double.parseDouble(subfields[1]));
					peptide.addPtm(ptm);
				}				
			}
			psm.linkPeptide(peptide);
			Protein protein = mapProtein.get(fields[2]);
			if( protein == null ) {
				protein = new Protein();
				mapProtein.put(fields[2], protein);
				protein.setAccession(fields[2]);
			}
			protein.linkPeptide(peptide);
		}
		data.loadFromSpectra(spectra);
		return data;
	}

	@Override
	public String getFilenameExtension() {
		return "csv";
	}
}