package es.ehubio.wregex.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import es.ehubio.db.cosmic.Census;
import es.ehubio.db.cosmic.Census.Entry;
import es.ehubio.io.CsvReader;

public class Mutant2CMC {
	private static final String INPUT_PATH = "/home/gorka/Descargas/DataBases/Sequences/Cosmic/v96/CosmicMutantExport.tsv.gz";
	private static final String OUTPUT_PATH = "/home/gorka/Descargas/DataBases/Sequences/Cosmic/v96/cmc_custom.tsv";
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Map<String, Census.Entry> map = readMutant();
		saveCustomCmc(map);
	}

	private static void saveCustomCmc(Map<String, Entry> map) throws FileNotFoundException {
		try(PrintWriter pw = new PrintWriter(OUTPUT_PATH)) {
			pw.println("GENE_NAME\tMutation AA\tMutation Description AA\tCOSMIC_SAMPLE_MUTATED");
			for( Census.Entry entry : map.values() )
				pw.printf("%s\t%s\t%s\t%d\n", entry.getGeneName(), entry.getMutationAa(), entry.getMutationDescriptionAa(), entry.getRecurrence());
		}
	}

	private static Map<String, Entry> readMutant() throws IOException {
		try( CsvReader csv = new CsvReader("\t") ) {
			csv.open(INPUT_PATH);
			int iGene = csv.getIndex("Gene name");
			int iMut = csv.getIndex("Mutation AA");
			int iDesc = csv.getIndex("Mutation Description");
			Map<String, Census.Entry> map = new HashMap<>(20000);
			while( csv.readLine() != null ) {
				String gene = csv.getField(iGene);
				if( gene.contains("_ENST") )
					continue;
				String desc = csv.getField(iDesc);
				if( !desc.contains("issense") )
					continue;
				String mut = csv.getField(iMut);				
				String key = gene+"@"+mut;
				Census.Entry entry = map.get(key);
				if( entry == null ) {
					entry = new Census.Entry();
					entry.setGeneName(gene);
					entry.setMutationAa(mut);
					entry.setMutationDescriptionAa(desc);
					entry.setRecurrence(1);
					map.put(key, entry);
				} else
					entry.setRecurrence(entry.getRecurrence()+1);
			}
			return map;
		}
	}

}
