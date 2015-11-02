package es.ehubio.proteomics.test;

import static org.junit.Assert.*;

import org.junit.Test;

import es.ehubio.panalyzer.Configuration;
import es.ehubio.panalyzer.Configuration.Replicate;
import es.ehubio.panalyzer.MainModel;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.ScoreType;

public class Deterministic {
	@Test
	public void testPAnalyzer() throws Exception {
		MainModel m1 = new MainModel();
		MainModel m2 = new MainModel();
		
		process(m1);
		process(m2);
		
		MsMsData d1 = m1.getData();
		MsMsData d2 = m2.getData();
		
		/*System.out.println(String.format("%s =? %s",d1.getSpectra().size(),d2.getSpectra().size()));
		System.out.println(String.format("%s =? %s",d1.getPsms().size(),d2.getPsms().size()));
		System.out.println(String.format("%s =? %s",d1.getPeptides().size(),d2.getPeptides().size()));
		System.out.println(String.format("%s =? %s",d1.getProteins().size(),d2.getProteins().size()));
		System.out.println(String.format("%s =? %s",d1.getGroups().size(),d2.getGroups().size()));*/
		
		assertEquals(d1.getSpectra().size(),d2.getSpectra().size());
		assertEquals(d1.getPsms().size(),d2.getPsms().size());
		assertEquals(d1.getPeptides().size(),d2.getPeptides().size());
		assertEquals(d1.getProteins().size(),d2.getProteins().size());
		assertEquals(d1.getGroups().size(),d2.getGroups().size());
	}

	private void process( MainModel m ) throws Exception {
		Configuration cfg = new Configuration();
		cfg.setPsmScore(ScoreType.SEQUEST_XCORR);
		cfg.setPsmRankThreshold(1);
		cfg.setBestPsmPerPrecursor(true);
		cfg.setMinPeptideLength(7);
		cfg.setPsmFdr(0.01);
		cfg.setProteinFdr(0.01);
		Replicate rep = new Replicate();
		rep.setName("Single");
		rep.getFractions().add("/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Adult_Frontalcortex/Adult_Frontalcortex_bRP_Elite_85.Target_TargetPeptideSpectrumMatch.txt.gz");
		rep.getFractions().add("/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Adult_Frontalcortex/Adult_Frontalcortex_bRP_Elite_85.Decoy_TargetPeptideSpectrumMatch.txt.gz");
		cfg.getReplicates().add(rep);
		cfg.setDecoyRegex("XXX");
		cfg.setDescription("Deterministic test");
		
		m.setConfig(cfg);
		m.loadData();
		m.filterData();
	}
}