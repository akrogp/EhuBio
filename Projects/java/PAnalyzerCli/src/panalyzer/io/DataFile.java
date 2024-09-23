package panalyzer.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import panalyzer.PAnalyzer;
import panalyzer.model.Item;
import panalyzer.model.Model;
import panalyzer.model.PeptideType;
import panalyzer.model.ProteinType;

public class DataFile {
	public static final String SEP1 = "\t";
	public static final String SEP2 = ";";
	
	public static Model load(String path, String pepId, String protId) throws FileNotFoundException, IOException {
		Model model = new Model();
		int iPep = 0, iProt = 0;
		
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {			
			String line;
			String[] header = null;
			while( (line = br.readLine()) != null ) {
				final String[] columns = line.split(SEP1, -1);
				if( header == null ) {
					header = columns;
					iPep = IntStream.range(0, columns.length).filter(i -> pepId.equals(columns[i])).findFirst().getAsInt();
					iProt = IntStream.range(0, columns.length).filter(i -> protId.equals(columns[i])).findFirst().getAsInt();
				} else {
					Item peptide = model.getPeptides().get(columns[iPep]);
					if( peptide == null ) {
						peptide = new Item(columns[iPep]);
						model.getPeptides().put(peptide.getId(), peptide);
						for( int i = 0; i < header.length; i++ )
							peptide.getProps().put(header[i], columns[i]);
					}
					String[] ids = columns[iProt].split(SEP2);
					for( String id : ids ) {
						Item protein = model.getProteins().get(id);
						if( protein == null ) {
							protein = new Item(id);
							model.getProteins().put(id, protein);
						}
						protein.getItems().add(peptide);
						peptide.getItems().add(protein);
					}
				}
			}
		}
		return model;
	}
	
	public static void save(Model model, String path, String protId) throws FileNotFoundException, IOException {
		try(PrintWriter pw = new PrintWriter(path)) {
			String header = model.getPeptides().values().iterator().next().getProps().keySet().stream().collect(Collectors.joining(SEP1));
			header = header + SEP1 + "pepType" + SEP1 + "protType" + SEP1 + "groupId";
			pw.println(header);
			for( Item pep : model.getPeptides().values() )
				for( Item prot : pep.getItems() ) {
					pep.getProps().put(protId, prot.getId());
					String line = pep.getProps().values().stream().collect(Collectors.joining(SEP1));
					line = line + SEP1 + ((PeptideType)pep.getType()).label + SEP1 + ((ProteinType)prot.getType()).label + SEP1 + prot.getProps().get(PAnalyzer.GROUP);
					pw.println(line);
				}
		}
	}
}
