package es.ehubio.dubase.pl.beans;

import java.util.Locale;
import java.util.stream.Collectors;

import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.input.ScoreType;
import es.ehubio.dubase.pl.Colors;
import es.ehubio.io.CsvUtils;

public class SearchBean {
	private final Evidence entity;
	private String experiment;
	private String enzyme;
	private String genes;
	private String descriptions;
	private String foldChangeFmt;
	private double foldChange;
	private String pValueFmt;
	private double pValue;
	private int totalPepts;
	private int uniqPepts;
	private String weightFmt;
	private double weight;
	private String glygly;
	
	public SearchBean(Evidence ev) {
		entity = ev;		
		setExperiment(String.format("EXP%05d", ev.getExperimentBean().getId()));
		setEnzyme(ev.getExperimentBean().getEnzymeBean().getGene());
		setGenes(CsvUtils.getCsv("<br/>", ev.getGenes().toArray()));
		setDescriptions(CsvUtils.getCsv("<br/>", ev.getDescriptions().toArray()));
		setFoldChange(ev.getScore(ScoreType.FOLD_CHANGE));		
		double pValue = ev.getScore(ScoreType.P_VALUE);
		setpValue(Math.pow(10, -pValue));
		setTotalPepts(ev.getScore(ScoreType.TOTAL_PEPTS).intValue());
		setUniqPepts(ev.getScore(ScoreType.UNIQ_PEPTS).intValue());
		setWeight(ev.getScore(ScoreType.MOL_WEIGHT));
		setGlygly(ev.getModifications().stream()
			.map(m->String.valueOf(m.getPosition()))
			.collect(Collectors.joining(";"))
		);
	}
	
	public String getExperiment() {
		return experiment;
	}
	public void setExperiment(String experiment) {
		this.experiment = experiment;
	}
	public String getEnzyme() {
		return enzyme;
	}
	public void setEnzyme(String enzyme) {
		this.enzyme = enzyme;
	}
	public String getGenes() {
		return genes;
	}
	public void setGenes(String genes) {
		this.genes = genes;
	}
	public String getDescriptions() {
		return descriptions;
	}
	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}
	public String getFoldChangeFmt() {
		return foldChangeFmt;
	}
	public String getpValueFmt() {
		return pValueFmt;
	}
	public int getTotalPepts() {
		return totalPepts;
	}
	public void setTotalPepts(int totalPepts) {
		this.totalPepts = totalPepts;
	}
	public int getUniqPepts() {
		return uniqPepts;
	}
	public void setUniqPepts(int uniqPepts) {
		this.uniqPepts = uniqPepts;
	}
	public String getWeightFmt() {
		return weightFmt;
	}
	public String getGlygly() {
		return glygly;
	}
	public void setGlygly(String glygly) {
		this.glygly = glygly;
	}
	public double getFoldChange() {
		return foldChange;
	}
	public void setFoldChange(double foldChange) {
		this.foldChange = foldChange;
		foldChangeFmt = String.format(Locale.ENGLISH,
			"<font color='%s'>%.2f</font>",
			foldChange >= 0 ? Colors.UP_REGULATED : Colors.DOWN_REGULATED,
			foldChange);
	}
	public double getpValue() {
		return pValue;
	}
	public void setpValue(double pValue) {
		this.pValue = pValue;
		pValueFmt = String.format(Locale.ENGLISH, "%4.1e", pValue);
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
		weightFmt = String.format(Locale.ENGLISH, "%.3f", weight);
	}

	public Evidence getEntity() {
		return entity;
	}
}
