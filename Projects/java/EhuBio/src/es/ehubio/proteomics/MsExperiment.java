package es.ehubio.proteomics;

import java.util.ArrayList;
import java.util.List;

public class MsExperiment {
	private MsMsData data;
	private List<Replicate> replicates;
	
	public void merge() {
		for( Replicate rep : getReplicates() ) {
			for( Spectrum spectrum : rep.getData().getSpectra() )
				spectrum.setRepName(rep.getName());
			if( data == null ) {
				data = rep.getData();
				continue;
			}
			data.mergeFromPeptide(rep.getData());
		}
	}
	
	public MsMsData getData() {
		return data;
	}
	
	public List<Replicate> getReplicates() {
		if( replicates == null )
			replicates = new ArrayList<>();
		return replicates;
	}

	public void setReplicates(List<Replicate> replicates) {
		this.replicates = replicates;
	}

	public static class Replicate {
		private final String name;
		private MsMsData data;
		
		public Replicate( String name ) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		public void mergeFraction( MsMsData fraction ) {
			if( data == null ) {
				data = fraction;
				return;
			}
			data.mergeFromPeptide(fraction);
		}
		
		public MsMsData getData() {
			return data;
		}
	}
}
