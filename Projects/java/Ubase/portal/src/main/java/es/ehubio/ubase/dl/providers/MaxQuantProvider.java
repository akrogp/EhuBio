package es.ehubio.ubase.dl.providers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

public class MaxQuantProvider implements Provider {

	@Override
	public String getName() {
		return "MaxQuant";
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public List<FileType> getInputFiles() {
		if( types == null ) {
			types = new ArrayList<>();
			types.add(new CsvFileType("peptides.txt", null, PEP_SEQ, PEP_MISSED));
			types.add(new CsvFileType("proteinGroups.txt", null, GRP_PIDS));
			types.add(new CsvFileType("GlyGly (K)Sites.txt", null, GLY_PROB, GLY_SIG));
		}
		return types;
	}
	
	@Override
	public List<String> getSamples(File data) {
		List<String> samples = new ArrayList<>();
		File file = new File(data, "peptides.txt");
		try( BufferedReader br = new BufferedReader(new FileReader(file)) ) {
			String[] fields = br.readLine().split("\\t");
			for( String field : fields )
				if( field.startsWith("Experiment ") )
					samples.add(field.substring(11));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return samples;
	}
	
	@Override
	public void persist(EntityManager em, File data) {
	}
	
	private List<FileType> types;
	private static final String PEP_SEQ = "Sequence";
	private static final String PEP_MISSED = "Missed cleavages";
	private static final String GRP_PIDS = "Protein IDs";
	private static final String GLY_PROB = "Localization prob";
	private static final String GLY_SIG = "GlyGly (K)";
}
