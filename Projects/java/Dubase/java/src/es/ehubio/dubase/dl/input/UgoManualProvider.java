package es.ehubio.dubase.dl.input;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.db.uniprot.Fetcher;
import es.ehubio.dubase.dl.entities.Ambiguity;
import es.ehubio.dubase.dl.entities.Author;
import es.ehubio.dubase.dl.entities.Cell;
import es.ehubio.dubase.dl.entities.Enzyme;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.FileType;
import es.ehubio.dubase.dl.entities.Gene;
import es.ehubio.dubase.dl.entities.Method;
import es.ehubio.dubase.dl.entities.MethodType;
import es.ehubio.dubase.dl.entities.Protein;
import es.ehubio.dubase.dl.entities.Publication;
import es.ehubio.dubase.dl.entities.SupportingFile;
import es.ehubio.dubase.dl.entities.Taxon;

public class UgoManualProvider implements Provider {
	public static List<Experiment> loadExperiments(String path) throws InvalidFormatException, IOException {
		try(
			FileInputStream fis = new FileInputStream(path);
			Workbook workbook = new XSSFWorkbook(fis);
		) {
			Sheet sheet = workbook.getSheetAt(0);
			Map<String, Integer> map = null;			
			List<Experiment> experiments = new ArrayList<>();
			for( Row row : sheet ) {
				if( map == null ) {
					map = new HashMap<>();
					int index = 0;
					for( org.apache.poi.ss.usermodel.Cell cell : row )
						map.put(cell.getStringCellValue(), index++);
				} else {			
					Experiment exp = new Experiment();		
					String dub = getCell(row, map, "DUB");
					if( dub == null || dub.isEmpty() )
						break;	// Premature finish
					
					exp.setIndex(row.getRowNum());
					exp.setEnzymeBean(new Enzyme());
					exp.getEnzymeBean().setGene(dub);
					
					exp.setAuthorBean(new Author());
					exp.getAuthorBean().setName("Ugo Mayor");
					exp.getAuthorBean().setMail("ugo.mayor@ehu.eus");
					exp.getAuthorBean().setAffiliation("UPV/EHU");
					
					exp.setPublications(new ArrayList<>());
					Publication pub = new Publication();
					pub.setDoi(getCell(row, map, "DOI"));
					pub.setPmid(getCell(row, map, "PMID"));
					exp.addPublication(pub);
					
					Cell cell = new Cell();
					cell.setName(getCell(row, map, "Cell"));
					cell.setTaxonBean(new Taxon());
					cell.getTaxonBean().setId(getInt(row, map, "Organism"));
					exp.setCellBean(cell);
					
					Method method = new Method();
					method.setType(new MethodType());
					method.getType().setId(es.ehubio.dubase.dl.input.MethodType.MANUAL.ordinal());
					method.setProteasomeInhibition(getCell(row, map, "Proteasome inhibition").equals("1"));
					method.setDescription("Manual curation");
					exp.setMethodBean(method);
					
					exp.setSupportingFiles(new ArrayList<>());
					SupportingFile supp = new SupportingFile();
					supp.setFileType(new FileType());
					supp.getFileType().setId(es.ehubio.dubase.dl.input.FileType.PUB_RES.ordinal());
					supp.setName(getCell(row, map, "Figure"));
					String url = getCell(row, map, "Figure URL");
					if( url.length() < 10 || url.length() > 255 )
						url = "https://doi.org/" + pub.getDoi();
					supp.setUrl(url);
					exp.addSupportingFile(supp);
					
					exp.setTitle(exp.getEnzymeBean().getGene());
					exp.setDescription("Manual curation");
					exp.setExpDate(new Date());
					
					experiments.add(exp);				
				}
			}
			return experiments;
		}
	}
	
	private static String getCell(Row row, Map<String, Integer> map, String col) {
		DataFormatter df = new DataFormatter();
		return df.formatCellValue(row.getCell(map.get(col))).replaceAll("\u00A0", " ").strip();
	}
	
	private static int getInt(Row row, Map<String, Integer> map, String col) {
		return Integer.parseInt(getCell(row, map, col));
	}

	@Override
	public List<Evidence> loadEvidences(String path, Experiment exp) throws Exception {
		List<Evidence> evs = new ArrayList<>();
		try(
			FileInputStream fis = new FileInputStream(path);
			Workbook workbook = new XSSFWorkbook(fis);
		) {
			Sheet sheet = workbook.getSheetAt(0);
			Map<String, Integer> map = null;			
			for( Row row : sheet ) {
				if( map == null ) {
					map = new HashMap<>();
					int index = 0;
					for( org.apache.poi.ss.usermodel.Cell cell : row )
						map.put(cell.getStringCellValue(), index++);
				} else if( exp.getIndex() == row.getRowNum() ) {										
					String[] genes = getCell(row, map, "Gene").split(";");
					String[] prots = getCell(row, map, "UniProt").split(";");
					if( genes.length != prots.length )
						throw new Exception("Genes and proteins numbers do no match");
					for( int i = 0; i < prots.length; i++ ) {
						Evidence ev = new Evidence();
						ev.setExperimentBean(exp);
						ev.setAmbiguities(new ArrayList<>());
						Gene gene = new Gene();
						gene.setName(genes[i]);
						Protein prot = new Protein();					
						prot.setAccession(prots[i]);
						prot.setGeneBean(gene);
						try {
							Fasta fasta = Fetcher.downloadFasta(prot.getAccession(), SequenceType.PROTEIN);						
							prot.setName(fasta.getProteinName());
							prot.setDescription(fasta.getDescription());
						} catch( Exception e ) {
							//e.printStackTrace();
							System.err.println(String.format("Could not download UniProt accession '%s'", prot.getAccession()));
							//prot.setName(prot.getAccession());
							//prot.setDescription(prot.getAccession());
						}
						Ambiguity amb = new Ambiguity();
						amb.setProteinBean(prot);			
						ev.addAmbiguity(amb);
						evs.add(ev);
					}
					break;
				}
			}
		}
		return evs;
	}
}
