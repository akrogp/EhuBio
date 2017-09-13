package es.ehubio.db.cosmic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import es.ehubio.io.CsvReader;

public class Cosmic {	
	public void openTsvGz( String path ) throws IOException {
		rd.open(new InputStreamReader(new GZIPInputStream(new FileInputStream(path))));
	}
	
	public void closeDb() {
		try {
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Entry nextEntry() throws IOException {
		if( rd.readLine() == null )
			return null;
		
		Entry entry = new Entry();
		entry.setGeneName(rd.getField("Gene name"));		
		entry.setAccession(rd.getField("Accession Number"));
		entry.setGeneCdsLength(rd.getField("Gene CDS length"));
		entry.setHgncId(rd.getField("HGNC ID"));
		entry.setSampleName(rd.getField("Sample name"));
		entry.setIdSample(rd.getField("ID_sample"));
		entry.setIdTumour(rd.getField("ID_tumour"));
		entry.setPrimarySite(rd.getField("Primary site"));
		entry.setSiteSubtype1(rd.getField("Site subtype"));
		entry.setPrimaryHistology(rd.getField("Primary histology"));
		entry.setHistologySubtype1(rd.getField("Histology subtype"));
		entry.setGws(rd.getField("Genome-wide screen"));
		entry.setMutationId(rd.getField("Mutation ID"));
		entry.setMutationCds(rd.getField("Mutation CDS"));
		entry.setMutationAa(rd.getField("Mutation AA"));
		entry.setMutationDescription(rd.getField("Mutation Description"));
		entry.setMutationZygosity(rd.getField("Mutation zygosity"));
		entry.setMutationGenomePosition(rd.getField("Mutation genome position"));
		entry.setGrch(rd.getField("GRCh"));
		entry.setMutationStrand(rd.getField("Mutation strand"));
		entry.setSnp(rd.getField("SNP"));
		entry.setFathmmPrediction(rd.getField("FATHMM prediction"));
		entry.setMutationSomaticStatus(rd.getField("Mutation somatic status"));
		entry.setPubmedId(rd.getField("Pubmed_PMID"));
		entry.setIdStudy(rd.getField("ID_STUDY"));
		entry.setSampleSource(rd.getField("Sample source"));
		entry.setTumourOrigin(rd.getField("Tumour origin"));
		entry.setAge(rd.getField("Age"));
		entry.setComments(rd.getField("Comments"));
		
		return entry;
	}
	
	public String[] getFieldNames() {
		return rd.getHeaders();
	}

	private CsvReader rd = new CsvReader("\\t", true);
}
