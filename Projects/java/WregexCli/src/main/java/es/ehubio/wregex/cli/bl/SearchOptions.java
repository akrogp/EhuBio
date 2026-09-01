package es.ehubio.wregex.cli.bl;

public class SearchOptions {
	private final String regex;
	private final String pssmPath;
	private final String motif;
	private final String inputPath;
	private final String outputPath;
	private final double threshold;
	private final boolean filterEqual;
	private final boolean grouping;
	private final String format;

	public SearchOptions(String regex, String pssmPath, String motif, String inputPath, String outputPath,
			double threshold, boolean filterEqual, boolean grouping, String format) {
		this.regex = regex;
		this.pssmPath = pssmPath;
		this.motif = motif;
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.threshold = threshold;
		this.filterEqual = filterEqual;
		this.grouping = grouping;
		this.format = format;
	}

	public String getRegex() {
		return regex;
	}

	public String getPssmPath() {
		return pssmPath;
	}

	public String getMotif() {
		return motif;
	}

	public String getInputPath() {
		return inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public double getThreshold() {
		return threshold;
	}

	public boolean isFilterEqual() {
		return filterEqual;
	}

	public boolean isGrouping() {
		return grouping;
	}

	public String getFormat() {
		return format;
	}
}
