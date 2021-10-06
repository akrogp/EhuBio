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
import es.ehubio.dubase.dl.entities.EvScore;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.ModScore;
import es.ehubio.dubase.dl.entities.ModType;
import es.ehubio.dubase.dl.entities.Modification;
import es.ehubio.dubase.dl.entities.Protein;
import es.ehubio.dubase.dl.entities.ScoreType;

public class PhuProteomicsProvider implements Provider {

	@Override
	public List<Evidence> loadEvidences(String dir, Experiment exp) throws Exception {
		File xlsProts = new File(dir, "mmc2.xlsx");
		File xlsMods = new File(dir, "mmc3.xlsx");
		
		Map<String, String> mapProts = new HashMap<>();
		Map<String, List<Modification>> mapMods = new HashMap<>();
		loadMods(xlsMods, mapProts, mapMods);
		
		List<Evidence> evs = loadProts(xlsProts, exp, mapProts, mapMods);
		
		return evs;
	}

	@SuppressWarnings("deprecation")
	private void loadMods(File xls, Map<String, String> mapProts, Map<String, List<Modification>> mapMods) throws Exception {
		try(
			FileInputStream fis = new FileInputStream(xls);
			Workbook workbook = new XSSFWorkbook(fis);
		) {
			Sheet sheet = workbook.getSheetAt(0);
			ModType modType = new ModType();
			modType.setId(es.ehubio.dubase.dl.input.ModType.GLYGLY.ordinal());
			ScoreType foldType = new ScoreType();
			foldType.setId(es.ehubio.dubase.dl.input.ScoreType.FOLD_CHANGE.ordinal());
			ScoreType pvalueType = new ScoreType();
			pvalueType.setId(es.ehubio.dubase.dl.input.ScoreType.P_VALUE.ordinal());
			for( Row row : sheet ) {
				if( row.getRowNum() < 2 )	// Skip headers
					continue;
				
				Cell foldCell = row.getCell(IDX_FOLD);
				Cell pvalueCell = row.getCell(IDX_PVALUE);
				if( foldCell.getCellTypeEnum() != CellType.NUMERIC || pvalueCell.getCellTypeEnum() != CellType.NUMERIC )
					continue;				
				
				String entry = row.getCell(IDX_PROT).getStringCellValue();
				String[] fields = entry.split("\\|");
				String name = fields[0].split("_")[0];
				String[] mods = fields[1].split("_");
				String acc = mods[0];
				mapProts.put(name, acc);
				
				List<Modification> list = mapMods.get(name);
				if( list == null ) {
					list = new ArrayList<>();
					mapMods.put(name, list);
				}
				for( int i = 1; i < mods.length; i++ ) {
					if( mods[i].equals("KNA") )
						continue;
					Modification mod = new Modification();
					mod.setModType(modType);
					mod.setPosition(Integer.parseInt(mods[i].substring(1)));
					mod.setScores(new ArrayList<>(2));
					ModScore foldScore = new ModScore();
					foldScore.setScoreType(foldType);
					foldScore.setValue(Math.pow(2, foldCell.getNumericCellValue()));
					ModScore pvalueScore = new ModScore();
					pvalueScore.setScoreType(pvalueType);
					pvalueScore.setValue(pvalueCell.getNumericCellValue());
					mod.addScore(foldScore);
					mod.addScore(pvalueScore);
					list.add(mod);
				}
			}
		}		
	}

	@SuppressWarnings("deprecation")
	private List<Evidence> loadProts(File xls, Experiment exp, Map<String, String> mapProts, Map<String, List<Modification>> mapMods) throws Exception {
		List<Evidence> evs = new ArrayList<>();
		try(
			FileInputStream fis = new FileInputStream(xls);
			Workbook workbook = new XSSFWorkbook(fis);
		) {
			Sheet sheet = workbook.getSheetAt(0);
			ScoreType foldType = new ScoreType();
			foldType.setId(es.ehubio.dubase.dl.input.ScoreType.FOLD_CHANGE.ordinal());
			ScoreType pvalueType = new ScoreType();
			pvalueType.setId(es.ehubio.dubase.dl.input.ScoreType.P_VALUE.ordinal());
			for( Row row : sheet ) {
				if( row.getRowNum() < 2 )	// Skip headers
					continue;
				
				Cell foldCell = row.getCell(IDX_FOLD);
				Cell pvalueCell = row.getCell(IDX_PVALUE);
				if( foldCell.getCellTypeEnum() != CellType.NUMERIC || pvalueCell.getCellTypeEnum() != CellType.NUMERIC )
					continue;
				
				Cell protCell = row.getCell(IDX_PROT);
				if( protCell.getCellTypeEnum() != CellType.STRING )		// some prot names are taken as dates ...
					continue;
				String name = protCell.getStringCellValue();
				String acc = mapProts.get(name);
				if( acc == null )
					continue;
				Protein prot = new Protein();
				prot.setName(name);
				prot.setAccession(acc);
				Ambiguity amb = new Ambiguity();
				amb.setProteinBean(prot);
				amb.setModifications(mapMods.get(name));
				Evidence ev = new Evidence();
				ev.setExperimentBean(exp);
				ev.setAmbiguities(new ArrayList<>(1));
				ev.addAmbiguity(amb);
				ev.setEvScores(new ArrayList<>(2));
				EvScore foldScore = new EvScore();
				foldScore.setScoreType(foldType);
				foldScore.setValue(Math.pow(2, foldCell.getNumericCellValue()));
				ev.addEvScore(foldScore);
				EvScore pvalueScore = new EvScore();
				pvalueScore.setScoreType(pvalueType);
				pvalueScore.setValue(pvalueCell.getNumericCellValue());
				ev.addEvScore(pvalueScore);
				evs.add(ev);				
			}
		}
		return evs;
	}
	
	private final static int IDX_PROT = 0;
	private final static int IDX_FOLD = 3;
	private final static int IDX_PVALUE = 8;
}
