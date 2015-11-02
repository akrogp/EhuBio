package es.ehubio.proteomics.test;

import static org.junit.Assert.*;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.junit.Test;

public class Gamma {

	@Test
	public void test() {
		assertEquals(0.00062046, testStep(10, 3), 0.00000001);
		assertEquals(0.00000000, testStep(200, 6), 0.00000001);
	}

	private double testStep( double pep, double nq ) {
		double g, p, s=0.0;
		for( int n = 1; n <= 3; n++ ) {
			GammaDistribution gamma = new GammaDistribution(n, 1);
			g = gamma.cumulativeProbability(pep);
			
			PoissonDistribution poisson = new PoissonDistribution(nq);
			p = poisson.probability(n);
			
			s = p * (1-g);
			
			//System.out.println(String.format("g=%s, p=%s, s=%s", g, p, s));
		}
		return s;
	}
}
