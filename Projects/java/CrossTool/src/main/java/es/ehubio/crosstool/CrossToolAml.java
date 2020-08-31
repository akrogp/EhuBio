package es.ehubio.crosstool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class CrossToolAml {
	public static class Result {
		public double corr;
		public double pvalue;
	}
	
	public static void main(String[] args) throws IOException {
		String path = "/home/gorka/Descargas/Sequences/cBioPortal/aml_ohsu_2018/data_RNA_Seq_expression_median.txt";
		String output = "/home/gorka/Bio/Workspace/CancerTool/gorka/aml_ohsu_2018.csv";
		Map<String, List<Double>> mapExpression = load(path);
		List<Double> orig = mapExpression.get("XPO1");
		try(PrintWriter pw = new PrintWriter(output)) {		
			for(Entry<String, List<Double>> dest : mapExpression.entrySet()) {
				Result result = compare(orig, dest.getValue());
				pw.printf(Locale.ENGLISH, "%s,%f,%e", dest.getKey(), result.corr, result.pvalue);
				pw.println();
			}
		}
		System.out.println("finished");
	}

	private static Result compare(List<Double> orig, List<Double> dest) {
		double[] data1 = ArrayUtils.toPrimitive(orig.toArray(new Double[0]));
		double[] data2 = ArrayUtils.toPrimitive(dest.toArray(new Double[0]));
		RealMatrix data = new Array2DRowRealMatrix(data1.length, 2);
		data.setColumn(0, data1);
		data.setColumn(1, data2);		
		PearsonsCorrelation corr = new PearsonsCorrelation(data);
		Result result = new Result();
		result.corr = corr.getCorrelationMatrix().getData()[0][1];
		result.pvalue = corr.getCorrelationPValues().getData()[0][1];
		return result;
	}

	private static Map<String, List<Double>> load(String path) throws IOException {
		Map<String, List<Double>> map = new HashMap<>();
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			br.readLine();	// skip header
			while( (line = br.readLine()) != null ) {
				String[] fields = line.split("\t");
				List<Double> data = new ArrayList<>(fields.length-2);
				for( int i = 2; i < fields.length; i++ )
					data.add(Double.parseDouble(fields[i]));
				map.put(fields[0], data);
			}
		}
		return map;
	}
}
