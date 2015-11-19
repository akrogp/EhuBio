package es.ehubio;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

public class MathUtil {
	public static double median( List<Double> list ) {
		Collections.sort(list);
		int i = list.size()/2;
		if( list.size()%2 == 1 )
			return list.get(i);
		return (list.get(i)+list.get(i+1))/2;
	}
	
	public static double mean( Collection<Double> list ) {
		double mean = 0.0;
		for( Double value : list )
			mean += value;
		if( !list.isEmpty() )
			mean /= list.size();
		return mean;
	}
	
	public static double percentile( double p, List<Double> list ) {
		Collections.sort(list);
		Percentile percentile = new Percentile(p);
		double[] values = new double[list.size()];
		int i = 0;
		for( Double value : list )
			values[i++] = value;
		percentile.setData(values);
		return percentile.evaluate();
	}
	
	public static double pow2(double num) {
		return num*num;
	}
	
	public static double r2(Collection<Double> obs, Collection<Double> exp) {
		double m = mean(obs);
		double res = 0.0, tot = 0.0;
		Iterator<Double> o = obs.iterator();
		Iterator<Double> e = exp.iterator();
		while( o.hasNext() && e.hasNext() ) {
			double y = o.next();
			tot += pow2(y-m);
			res += pow2(y-e.next());
		}
		return 1-res/tot;
	}
}
