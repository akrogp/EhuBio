package es.ehubio.crosstool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class CrossTool {
	public static void main(String[] args) throws IOException {
		String pathGene1 = "/home/gorka/Bio/Workspace/CancerTool/gorka/XPO1.txt";
		String pathGene2 = "/home/gorka/Bio/Workspace/CancerTool/gorka/AURKA.txt";
		
		double[] data1 = load(pathGene1);
		double[] data2 = load(pathGene2);
		RealMatrix data = new Array2DRowRealMatrix(data1.length, 2);
		data.setColumn(0, data1);
		data.setColumn(1, data2);
		
		PearsonsCorrelation corr = new PearsonsCorrelation(data);
		System.out.println("corr: " + corr.getCorrelationMatrix().getData()[0][1]);
		System.out.println("p-value: " + corr.getCorrelationPValues().getData()[0][1]);
	}

	private static double[] load(String path) throws IOException {
		Double[] data = Arrays.stream(Files.readAllLines(Paths.get(path)).get(0).split("\t"))
			.map(str->Double.parseDouble(str)).toArray(Double[]::new);
		return ArrayUtils.toPrimitive(data);
	}
}
