package es.ehubio.proteomics.test;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.io.MsMsFile;
import es.ehubio.proteomics.io.Mzid;

public class Merging {
	private static final Logger logger = Logger.getLogger(Merging.class.getName());
	private static final String PATH1 = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/extractor/platelets/velos0036-panalyzer/velos003607.mzid";
	private static final String PATH2 = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/XTandem/experiment/SPHPP_UPV_JURKAT_QE_CHAPS_RPB_R2_1_10.mzid";

	@Test
	public void testSame() throws Exception {		
		MsMsFile file1 = new Mzid();
		MsMsData data1 = file1.load(PATH1,false).markDecoys("decoy");
		int spectra1 = data1.getSpectra().size();
		int psms1 = data1.getPsms().size();
		int peptides1 = data1.getPeptides().size();
		int proteins1 = data1.getProteins().size();
		logger.info("Data1: "+data1.toString());		
		
		MsMsFile file2 = new Mzid();
		MsMsData data2 = file2.load(PATH1,false).markDecoys("decoy");
		logger.info("Data2: "+data2.toString());
		
		data1.mergeFromPeptide(data2);
		int spectraMerge = data1.getSpectra().size();
		int psmsMerge = data1.getPsms().size();
		int peptidesMerge = data1.getPeptides().size();
		int proteinsMerge = data1.getProteins().size();
		logger.info("Merge: "+data1.toString());
		
		assertEquals(2*spectra1, spectraMerge);
		assertEquals(2*psms1, psmsMerge);
		assertEquals(peptides1, peptidesMerge);
		assertEquals(proteins1, proteinsMerge);
	}
	
	@Test
	public void testDifferent() throws Exception {		
		MsMsFile file1 = new Mzid();
		MsMsData data1 = file1.load(PATH1,false).markDecoys("decoy");
		int spectra1 = data1.getSpectra().size();
		int psms1 = data1.getPsms().size();
		int peptides1 = data1.getPeptides().size();
		int proteins1 = data1.getProteins().size();
		logger.info("Data1: "+data1.toString());		
		
		MsMsFile file2 = new Mzid();
		MsMsData data2 = file2.load(PATH2,false).markDecoys("decoy");
		int spectra2 = data2.getSpectra().size();
		int psms2 = data2.getPsms().size();
		int peptides2 = data2.getPeptides().size();
		int proteins2 = data2.getProteins().size();
		logger.info("Data2: "+data2.toString());
		
		data1.mergeFromPeptide(data2);
		int spectraMerge = data1.getSpectra().size();
		int psmsMerge = data1.getPsms().size();
		int peptidesMerge = data1.getPeptides().size();
		int proteinsMerge = data1.getProteins().size();
		logger.info("Merge: "+data1.toString());
		
		assertEquals(spectra1+spectra2, spectraMerge);
		assertEquals(psms1+psms2, psmsMerge);
		assertTrue(peptidesMerge >= peptides1 && peptidesMerge >= peptides2 && peptidesMerge <= (peptides1+peptides2));
		assertTrue(proteinsMerge >= proteins1 && proteinsMerge >= proteins2 && proteinsMerge <= (proteins1+proteins2));
	}
}