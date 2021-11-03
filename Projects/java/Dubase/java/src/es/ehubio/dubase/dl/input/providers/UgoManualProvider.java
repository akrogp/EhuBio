package es.ehubio.dubase.dl.input.providers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

import es.ehubio.dubase.dl.entities.Ambiguity;
import es.ehubio.dubase.dl.entities.Author;
import es.ehubio.dubase.dl.entities.Cell;
import es.ehubio.dubase.dl.entities.Enzyme;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.FileType;
import es.ehubio.dubase.dl.entities.Gene;
import es.ehubio.dubase.dl.entities.Method;
import es.ehubio.dubase.dl.entities.MethodSubtype;
import es.ehubio.dubase.dl.entities.MethodType;
import es.ehubio.dubase.dl.entities.Protein;
import es.ehubio.dubase.dl.entities.Publication;
import es.ehubio.dubase.dl.entities.SupportingFile;
import es.ehubio.dubase.dl.entities.Taxon;

public class UgoManualProvider implements Provider {
	public enum Header {
		DUB, DOI, PMID, Gene, UniProt, Organism, Cell, Figure, FigureURL("Figure URL"), ProteasomeInhibition("Proteasome inhibition"), Method;
		
		Header() {	
			header = null;
		}
		
		Header(String header) {
			this.header = header;
		}
		
		@Override
		public String toString() {
			return header == null ? super.toString() : header;
		}
		
		private final String header;
	}
	
	private static final Header[] PK = {Header.DUB, Header.PMID, Header.Organism, Header.Cell, Header.Figure, Header.ProteasomeInhibition, Header.Method};
	
	public static Collection<Experiment> loadExperiments(String path) throws InvalidFormatException, IOException {
		try(
			FileInputStream fis = new FileInputStream(path);
			Workbook workbook = new XSSFWorkbook(fis);
		) {
			Sheet sheet = workbook.getSheetAt(0);
			Map<String, Integer> mapHeader = null;			
			Map<String, Experiment> mapExperiment = new HashMap<>();
			for( Row row : sheet ) {
				if( mapHeader == null ) {
					mapHeader = new HashMap<>();
					int index = 0;
					for( org.apache.poi.ss.usermodel.Cell cell : row )
						mapHeader.put(cell.getStringCellValue(), index++);
				} else {			
					String dub = getCell(row, mapHeader, "DUB");
					if( dub == null || dub.isEmpty() )
						break;	// Premature finish
					
					String extId = buildId(row, mapHeader);
					Experiment exp = mapExperiment.get(extId);
					if( exp == null ) {
						exp = new Experiment();
						mapExperiment.put(extId, exp);
						exp.setExtId(extId);
						
						exp.setTitle(dub);
						exp.setDescription("Manual curation");
						exp.setExpDate(new Date());

						exp.setEnzymeBean(new Enzyme());
						exp.getEnzymeBean().setGene(dub);
						
						exp.setAuthorBean(new Author());
						exp.getAuthorBean().setName("Ugo Mayor");
						exp.getAuthorBean().setMail("ugo.mayor@ehu.eus");
						exp.getAuthorBean().setAffiliation("UPV/EHU");
						
						exp.setPublications(new ArrayList<>());
						Publication pub = new Publication();
						pub.setDoi(getCell(row, mapHeader, "DOI"));
						pub.setPmid(getCell(row, mapHeader, "PMID"));
						exp.addPublication(pub);
						
						Cell cell = new Cell();
						cell.setName(getCell(row, mapHeader, "Cell"));
						cell.setTaxonBean(new Taxon());
						cell.getTaxonBean().setId(getInt(row, mapHeader, "Organism"));
						exp.setCellBean(cell);
						
						Method method = new Method();
						method.setType(new MethodType());
						method.getType().setId(es.ehubio.dubase.dl.input.MethodType.MANUAL.ordinal());
						method.setProteasomeInhibition(getCell(row, mapHeader, "Proteasome inhibition").equals("1"));
						method.setDescription("Manual curation");
						method.setSubtype(new MethodSubtype());
						method.getSubtype().setId(getInt(row, mapHeader, "Method"));
						exp.setMethodBean(method);
					}
					
					exp.setSupportingFiles(new ArrayList<>());
					SupportingFile supp = new SupportingFile();
					supp.setFileType(new FileType());
					supp.getFileType().setId(es.ehubio.dubase.dl.input.FileType.PUB_RES.ordinal());
					supp.setName(getCell(row, mapHeader, "Figure"));
					String url = getCell(row, mapHeader, "Figure URL");
					if( url.length() < 10 || url.length() > 255 )
						url = exp.getPublications().get(0).getUrl();
					supp.setUrl(url);
					exp.addSupportingFile(supp);				
				}
			}
			return mapExperiment.values();
		}
	}
	
	private static String buildId(Row row, Map<String, Integer> mapHeader) {
		StringBuilder expId = new StringBuilder();
		for( Header header : PK )
			expId.append(getCell(row, mapHeader, header.toString()));
		return expId.toString();
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
			Map<String, Integer> mapHeader = null;			
			for( Row row : sheet ) {
				if( mapHeader == null ) {
					mapHeader = new HashMap<>();
					int index = 0;
					for( org.apache.poi.ss.usermodel.Cell cell : row )
						mapHeader.put(cell.getStringCellValue(), index++);
				} else if( exp.getExtId().equals(buildId(row, mapHeader)) ) {
					String cell = getCell(row, mapHeader, "Gene");
					String[] genes = cell == null || cell.isBlank() ? null : cell.split(";");
					cell = getCell(row, mapHeader, "UniProt");					
					String[] prots = cell == null || cell.isBlank() ? null : cell.split(";");
					if( genes != null && prots != null && genes.length != prots.length )
						throw new Exception("Genes and proteins numbers do no match");
					int len = prots != null ? prots.length : genes.length;
					for( int i = 0; i < len; i++ ) {
						Evidence ev = new Evidence();
						ev.setExperimentBean(exp);
						ev.setAmbiguities(new ArrayList<>());
						Gene gene = new Gene();
						gene.setName(genes == null ? null : genes[i]);
						Protein prot = new Protein();					
						prot.setAccession(prots == null ? null : prots[i]);
						prot.setGeneBean(gene);
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
