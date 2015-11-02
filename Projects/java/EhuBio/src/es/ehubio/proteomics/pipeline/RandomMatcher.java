package es.ehubio.proteomics.pipeline;

import es.ehubio.proteomics.Protein;

public interface RandomMatcher {
	
	public static class Result {				
		public Result(double Nq, double Mq) {
			this.Nq = Nq;
			this.Mq = Mq;
		}
		
		public double getNq() {
			return Nq;
		}
		
		public double getMq() {
			return Mq;
		}
		
		private final double Nq, Mq;
	}
	
	Result getExpected( Protein protein );
}
