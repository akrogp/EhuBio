package es.ehubio.db.cosmic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import es.ehubio.org.hgvs.Hgvs;
import es.ehubio.org.hgvs.ProteinMutation;

public class CosmicStats {
	private Map<Integer,Locus> loci = new HashMap<>();
	private String gene;

	public Map<Integer,Locus> getLoci() {
		return loci;
	}

	public void setLoci(Map<Integer,Locus> loci) {
		this.loci = loci;
	}

	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}
	
	public static Map<String,CosmicStats> load( String path ) throws FileNotFoundException, IOException {
		Map<String,CosmicStats> results = new HashMap<>();
		CosmicStats result = null;
		String isSnp = "y";
		
		Cosmic cosmic = new Cosmic();
		cosmic.openTsvGz(path);
		Entry entry;
		while( (entry=cosmic.nextEntry()) != null ) {
			if( entry.getMutationAa() == null || isSnp.equalsIgnoreCase(entry.getSnp()) )
				continue;
			ProteinMutation mut = Hgvs.parseProteinMutation(entry.getMutationAa());
			if( mut == null || mut.getType() != ProteinMutation.Type.Missense )
				continue;
			result = results.get(entry.getGeneName());
			if( result == null ) {				
				result = new CosmicStats();
				result.setGene(entry.getGeneName());
				results.put(entry.getGeneName(), result);
			}
			Locus locus = result.getLoci().get(mut.getPosition());
			if( locus == null ) {
				locus = new Locus();
				locus.setPosition(mut.getPosition());
				locus.setOriginal(mut.getOriginal());
				result.getLoci().put(locus.getPosition(), locus);
			}
			locus.incMutationCount(mut.getMutated());
		}
		cosmic.closeDb();
		
		return results;
	}
}
