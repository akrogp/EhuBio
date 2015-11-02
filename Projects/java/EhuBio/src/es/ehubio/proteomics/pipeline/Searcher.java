package es.ehubio.proteomics.pipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.ehubio.model.Aminoacid;

public class Searcher {
	public static class Config {
		public Config(int minLength, int maxLength) {
			this(minLength,maxLength,0);
		}
		
		public Config(int minLength, int maxLength, int maxMods, Collection<Aminoacid> varMods) {
			this(minLength,maxLength,maxMods,varMods.toArray(new Aminoacid[0]));
		}

		public Config(int minLength, int maxLength, int maxMods, Aminoacid... varMods) {
			this.minLength = minLength;
			this.maxLength = maxLength;
			this.maxMods = maxMods;
			for( Aminoacid aa : varMods )
				this.varMods.add(aa);
		}
		
		public int getMinLength() {
			return minLength;
		}

		public int getMaxLength() {
			return maxLength;
		}

		public int getMaxMods() {
			return maxMods;
		}

		public List<Aminoacid> getVarMods() {
			return varMods;
		}
		
		private final int minLength, maxLength, maxMods;
		private final List<Aminoacid> varMods = new ArrayList<>();
	}
}
