package es.ehubio.dubase.pl.beans;

import java.util.stream.Collectors;

import es.ehubio.dubase.dl.CsvExporter;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.input.ScoreType;
import es.ehubio.dubase.pl.views.Formats;
import es.ehubio.io.CsvUtils;

public class SearchBean {
	private final Evidence entity;
	private String experiment;
	private String enzyme;
	private String genes;
	private String proteins;
	private String cell;
	private String descriptions;
	private String foldChangeFmt;
	private Double foldChange;
	private String pValueFmt;
	private Double pValue;
	private Integer totalPepts;
	private Integer uniqPepts;
	private String weightFmt;
	private Double weight;
	private Double coverage;
	private String coverageFmt;
	private String glygly;	
	
	public SearchBean(Evidence ev) {
		entity = ev;		
		setExperiment(ev.getExperimentBean().getFmtId());
		setEnzyme(ev.getExperimentBean().getEnzymeBean().getGene());
		setGenes(ev.getAmbiguities().stream()
				//.map(a->String.format("<a href='https://www.uniprot.org/uniprot/%s' target='_blank'>%s</a>",a.getProteinBean().getAccession(),a.getProteinBean().getGeneBean().getName()))
				.map(a->String.format("<a href='https://www.uniprot.org/uniprot/?query=gene%%3A%s+organism%%3A%d&sort=score' target='_blank'>%s</a>",a.getProteinBean().getGeneBean().getName(),ev.getExperimentBean().getCellBean().getTaxonBean().getId(),a.getProteinBean().getGeneBean().getName()))
				.distinct()
				.collect(Collectors.joining("<br/>")));
		setProteins(ev.getProteins().stream()
				.map(p->String.format("<a href='https://www.uniprot.org/uniprot/%s' target='_blank'>%s</a>",p,p))
				.collect(Collectors.joining("<br/>")));
		setCell(ev.getExperimentBean().getCellBean().getName());
		if( ev.getDescriptions().stream().anyMatch(d -> d!= null) )
			setDescriptions(CsvUtils.getCsv("<br/>", ev.getDescriptions().toArray()));
		setGlygly(CsvExporter.buildModString(ev,"<br/>",", "));
		if( ev.getExperimentBean().getMethodBean().isProteomics() ) {
			setFoldChange(Math.log(ev.getScore(ScoreType.FOLD_CHANGE))/Math.log(2));
			setpValue(ev.getScore(ScoreType.P_VALUE));
			setTotalPepts(ev.getScore(ScoreType.TOTAL_PEPTS));
			setUniqPepts(ev.getScore(ScoreType.UNIQ_PEPTS));
			setWeight(ev.getScore(ScoreType.MOL_WEIGHT));
			setCoverage(ev.getScore(ScoreType.SEQ_COVERAGE));			
		}
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
	public void setCell(String cell) {
		this.cell = cell;
	}
	public String getCell() {
		return cell;
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
	public Integer getTotalPepts() {
		return totalPepts;
	}
	public void setTotalPepts(Integer totalPepts) {
		this.totalPepts = totalPepts;
	}
	public void setTotalPepts(Double totalPepts) {
		this.totalPepts = totalPepts == null ? null : totalPepts.intValue();
	}
	public Integer getUniqPepts() {
		return uniqPepts;
	}
	public void setUniqPepts(Integer uniqPepts) {
		this.uniqPepts = uniqPepts;
	}
	public void setUniqPepts(Double uniqPepts) {
		this.uniqPepts = uniqPepts == null ? null : uniqPepts.intValue();
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
	public Double getFoldChange() {
		return foldChange;
	}
	public void setFoldChange(Double foldChange) {
		this.foldChange = foldChange;
		foldChangeFmt = Formats.logChange(foldChange);
	}
	public Double getpValue() {
		return pValue;
	}
	public void setpValue(Double pValue) {
		this.pValue = pValue;
		pValueFmt = Formats.exp10(pValue);
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
		weightFmt = weight == null ? null : Formats.decimal3(weight);
	}

	public Evidence getEntity() {
		return entity;
	}

	public String getProteins() {
		return proteins;
	}

	public void setProteins(String proteins) {
		this.proteins = proteins;
	}

	public Double getCoverage() {
		return coverage;
	}

	public void setCoverage(Double coverage) {
		this.coverage = coverage;
		coverageFmt = coverage == null ? null : Formats.percent(coverage);
	}
	
	public String getCoverageFmt() {
		return coverageFmt;
	}

	public String getType() {
		return entity.getExperimentBean().getMethodBean().getTypeFmt();
	}

	public boolean isProteomics() {
		return entity.getExperimentBean().getMethodBean().isProteomics();
	}
	
	public boolean isUbiquitomics() {
		return entity.getExperimentBean().getMethodBean().isUbiquitomics();
	}
}
