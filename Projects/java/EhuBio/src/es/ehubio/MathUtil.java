package es.ehubio;

import java.util.Collections;
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
}
