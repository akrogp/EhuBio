package es.ehubio.dubase.dl.input.providers;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import es.ehubio.dubase.dl.entities.Ambiguity;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.Gene;
import es.ehubio.dubase.dl.entities.ModRepScore;
import es.ehubio.dubase.dl.entities.ModScore;
import es.ehubio.dubase.dl.entities.ModType;
import es.ehubio.dubase.dl.entities.Modification;
import es.ehubio.dubase.dl.entities.Protein;
import es.ehubio.dubase.dl.entities.Replicate;
import es.ehubio.dubase.dl.entities.ScoreType;

public class LiuUbiquitomicsProvider implements Provider {

	@SuppressWarnings("deprecation")
	@Override
	public List<Evidence> loadEvidences(String dir, Experiment exp) throws Exception {
		List<Evidence> evs = new ArrayList<>();
		File xls = new File(dir, "41467_2018_7185_MOESM5_ESM.xlsx");
		try(
			FileInputStream fis = new FileInputStream(xls);
			Workbook workbook = new XSSFWorkbook(fis);
		) {
			Sheet sheet = workbook.getSheetAt(1);
			Map<String, Ambiguity> map = new HashMap<>();
			for( Row row : sheet ) {
				if( row.getRowNum() < 3 )	// Skip headers
					continue;
				if( row.getCell(IDX_PROT) == null )	{	// End of file ...
					//System.out.println(row.getRowNum());
					break;
				}
				String acc = row.getCell(IDX_PROT).getStringCellValue();			
				Ambiguity amb = map.get(acc);
				if( amb == null ) {
					amb = new Ambiguity();
					map.put(acc, amb);
					amb.setModifications(new ArrayList<>());
					
					Evidence ev = new Evidence();
					ev.setAmbiguities(new ArrayList<>());
					ev.addAmbiguity(amb);					
					ev.setExperimentBean(exp);
					evs.add(ev);
					
					Protein prot = new Protein();
					prot.setAccession(row.getCell(IDX_PROT).getStringCellValue());
					Gene gene = new Gene();
					Cell name = row.getCell(IDX_GENE);
					if( name.getCellTypeEnum() == CellType.STRING ) {						
						gene.setName(name.getStringCellValue());
						prot.setGeneBean(gene);
					}
					amb.setProteinBean(prot);
				}
				loadEvidence(amb, row);
			}
		}		
		return evs;
	}
	
	@SuppressWarnings("deprecation")
	private void loadEvidence(Ambiguity amb, Row row) {
		Modification mod = new Modification();
		mod.setScores(new ArrayList<>(2));
		mod.setRepScores(new ArrayList<>(4));
		amb.addModification(mod);
		
		ModType modType = new ModType();
		modType.setId(es.ehubio.dubase.dl.input.ModType.GLYGLY.ordinal());
		mod.setModType(modType);
		mod.setPosition((int)Math.round(row.getCell(IDX_POS).getNumericCellValue()));
		
		ScoreType scoreType = new ScoreType();
		scoreType.setId(es.ehubio.dubase.dl.input.ScoreType.P_VALUE.ordinal());
		ModScore modScore = new ModScore();
		modScore.setScoreType(scoreType);
		modScore.setValue(row.getCell(IDX_PVALUE).getNumericCellValue());
		mod.addScore(modScore);
		
		scoreType = new ScoreType();
		scoreType.setId(es.ehubio.dubase.dl.input.ScoreType.FOLD_CHANGE.ordinal());
		modScore = new ModScore();
		modScore.setScoreType(scoreType);
		modScore.setValue(row.getCell(IDX_RATIO).getNumericCellValue());
		mod.addScore(modScore);
		
		int i = 0;
		for( Replicate rep : amb.getEvidenceBean().getExperimentBean().getConditions().get(1).getReplicates() ) {
			ModRepScore modRepScore = new ModRepScore();
			modRepScore.setReplicateBean(rep);			
			modRepScore.setImputed(false);
			modRepScore.setScoreType(scoreType);
			Cell cell = row.getCell(IDX_REP1+i);			
			if( cell.getCellTypeEnum() == CellType.NUMERIC )
				modRepScore.setValue(cell.getNumericCellValue());
			mod.addRepScore(modRepScore);
			i++;
		}
	}

	private final static int IDX_GENE = 0;
	private final static int IDX_PROT = 1;
	private final static int IDX_POS = 2;
	private final static int IDX_REP1 = 5;
	private final static int IDX_RATIO = 9;
	private final static int IDX_PVALUE = 11;
}
