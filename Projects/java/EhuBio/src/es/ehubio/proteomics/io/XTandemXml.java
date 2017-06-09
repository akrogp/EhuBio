package es.ehubio.proteomics.io;

import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.OperationNotSupportedException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.HeaderParser;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Ptm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.Spectrum;
import es.ehubio.proteomics.thegpm.Bioml;
import es.ehubio.proteomics.thegpm.Bioml.Group;
import es.ehubio.proteomics.thegpm.Bioml.Group.Protein.Peptide.Domain;
import es.ehubio.proteomics.thegpm.Bioml.Group.Protein.Peptide.Domain.Aa;

public class XTandemXml extends MsMsFile {

	@Override
	public String getFilenameExtension() {
		return "t.xml";
	}

	@Override
	protected boolean checkSignatureStream(InputStream input) throws Exception {
		byte[] buffer = new byte[500];
		input.read(buffer);
		return new String(buffer).contains("<bioml");
	}
	
	@Override
	protected MsMsData loadStream(InputStream input, boolean loadFragments) throws Exception {
		if( loadFragments == true )
			throw new OperationNotSupportedException("Fragments not still supported");
		JAXBContext jaxbContext = JAXBContext.newInstance(Bioml.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Bioml bioml = (Bioml)unmarshaller.unmarshal(input);
		return loadBioml(bioml);
	}

	private MsMsData loadBioml(Bioml bioml) throws ParseException {
		String mgf = bioml.getLabel().split("'")[1];
		Set<Spectrum> spectra = new HashSet<>();
		Map<Integer, Protein> mapProteins = new HashMap<>();
		Map<String,Peptide> mapPeptides = new HashMap<>();
		Set<Peptide> psms = new HashSet<>();
		for( Group group : bioml.getGroup() ) {
			Spectrum spectrum = new Spectrum();
			spectrum.setFileName(mgf);
			spectrum.setFileId(group.getId()+"");
			//spectrum.setRt(group.getRt());
			spectrum.setIntensity(group.getSumI());
			spectra.add(spectrum);
			psms.clear();
			for( es.ehubio.proteomics.thegpm.Bioml.Group.Protein p : group.getProtein() ) {
				Protein protein = loadProtein(mapProteins,p);				
				Domain d = p.getPeptide().getDomain();
				Peptide peptide = loadPeptide(mapPeptides,d);
				protein.linkPeptide(peptide);
				if( !psms.add(peptide) )
					continue;
				Psm psm = new Psm();
				psm.linkSpectrum(spectrum);
				psm.linkPeptide(peptide);
				psm.setCalcMz(d.getMh());
				psm.setMassError(d.getDelta());
				psm.setCharge(group.getZ());
				psm.putScore(new Score(ScoreType.XTANDEM_EVALUE, d.getExpect()));
				psm.putScore(new Score(ScoreType.XTANDEM_HYPERSCORE, d.getHyperscore()));
			}
		}
		MsMsData data = new MsMsData();
		data.loadFromSpectra(spectra);
		data.updateRanks(ScoreType.XTANDEM_EVALUE);
		return data;
	}	

	private Protein loadProtein(Map<Integer, Protein> mapProteins, es.ehubio.proteomics.thegpm.Bioml.Group.Protein p) {
		Protein protein = mapProteins.get(p.getUid());
		if( protein == null ) {
			protein = new Protein();
			HeaderParser header = Fasta.guessParser(p.getNote().getValue());
			if( header != null ) {
				protein.setAccession(header.getAccession());
				protein.setDescription(header.getDescription());
				protein.setName(header.getProteinName());
			} else {
				protein.setAccession(p.getUid()+"");
				protein.setDescription(p.getLabel());
			}
			mapProteins.put(p.getUid(), protein);
		}
		return protein;
	}
	
	private Peptide loadPeptide(Map<String, Peptide> mapPeptides, Domain d) throws ParseException {
		Peptide newPeptide = new Peptide();
		newPeptide.setSequence(d.getSeq());
		for( Aa aa : d.getAa() ) {
			Ptm ptm = new Ptm();	
			ptm.setPosition(aa.getAt()-d.getStart()+1);
			ptm.setMassDelta(aa.getModified());
			ptm.setResidues(aa.getType());
			ptm.guessMissing(d.getSeq());
			newPeptide.addPtm(ptm);
		}
		Peptide peptide = mapPeptides.get(newPeptide.getUniqueString());
		if( peptide == null ) {
			peptide = newPeptide;
			mapPeptides.put(peptide.getUniqueString(), peptide);
		}
		return peptide;
	}
}
