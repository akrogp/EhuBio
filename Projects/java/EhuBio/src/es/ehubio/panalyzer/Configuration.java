package es.ehubio.panalyzer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

@XmlType(propOrder={
	"description", "psmScore",
	"psmRankThreshold","bestPsmPerPrecursor","bestPsmPerPeptide", "psmFdr", "psmScoreThreshold",
	"minPeptideLength","uniquePeptides","peptideFdr","peptideScoreThreshold","minPeptideReplicates",
	"proteinFdr","proteinScoreThreshold","minProteinReplicates","groupFdr","groupScoreThreshold",
	"decoyRegex","replicates","useFragmentIons","filterDecoys","output"})
@XmlRootElement
public class Configuration implements Serializable {		
	private static final long serialVersionUID = 1L;
	
	public void initializeFilter() {
		setPsmRankThreshold(1);		
		setBestPsmPerPrecursor(false);
		setBestPsmPerPeptide(true);
		setPsmFdr(null);
		setPsmScore(null);
		setMinPeptideLength(7);
		setUniquePeptides(null);
		setPeptideFdr(null);
		setMinPeptideReplicates(null);
		setProteinFdr(null);
		setMinProteinReplicates(null);
		setGroupFdr(0.01);
		setFilterDecoys(false);
	}
	
	@XmlAttribute
	public String getVersion() {
		return version;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public ScoreType getPsmScore() {
		return psmScore;
	}
	
	public void setPsmScore(ScoreType psmScore) {
		this.psmScore = psmScore;
	}
	
	public String getDecoyRegex() {
		return decoyRegex;
	}
	
	public void setDecoyRegex(String decoyRegex) {
		this.decoyRegex = decoyRegex;
	}	
	
	public Integer getPsmRankThreshold() {
		return psmRankThreshold;
	}

	public void setPsmRankThreshold(Integer psmRankThreshold) {
		this.psmRankThreshold = psmRankThreshold;
	}

	public Boolean getBestPsmPerPrecursor() {
		return bestPsmPerPrecursor;
	}

	public void setBestPsmPerPrecursor(Boolean bestPsmPerPrecursor) {
		this.bestPsmPerPrecursor = bestPsmPerPrecursor;
	}
	
	public Boolean getBestPsmPerPeptide() {
		return bestPsmPerPeptide;
	}

	public void setBestPsmPerPeptide(Boolean bestPsmPerPeptide) {
		this.bestPsmPerPeptide = bestPsmPerPeptide;
	}
	
	public Double getPsmFdr() {
		return psmFdr;
	}
	
	public void setPsmFdr(Double psmFdr) {
		this.psmFdr = psmFdr;
	}
	
	public Integer getMinPeptideLength() {
		return minPeptideLength;
	}

	public void setMinPeptideLength(Integer minPeptideLength) {
		this.minPeptideLength = minPeptideLength;
	}
	
	public Double getProteinFdr() {
		return proteinFdr;
	}

	public void setProteinFdr(Double proteinFdr) {
		this.proteinFdr = proteinFdr;
	}
	
	public Double getPeptideFdr() {
		return peptideFdr;
	}
	
	public void setPeptideFdr(Double peptideFdr) {
		this.peptideFdr = peptideFdr;
	}
	
	public Double getGroupFdr() {
		return groupFdr;
	}
	
	public void setGroupFdr(Double groupFdr) {
		this.groupFdr = groupFdr;
	}			
	
	@XmlElement(name="replicate")
	public List<Replicate> getReplicates() {
		if( replicates == null )
			replicates = new ArrayList<>();
		return replicates;
	}
	
	public void setReplicates(List<Replicate> replicates) {
		this.replicates = replicates;
	}
	
	public String getOutput() {
		return output;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}
	
	public Boolean getFilterDecoys() {
		return filterDecoys;
	}

	public void setFilterDecoys(Boolean filterDecoys) {
		this.filterDecoys = filterDecoys;
	}
	
	public Integer getMinPeptideReplicates() {
		return minPeptideReplicates;
	}

	public void setMinPeptideReplicates(Integer minPeptideReplicates) {
		this.minPeptideReplicates = minPeptideReplicates;
	}

	public Integer getMinProteinReplicates() {
		return minProteinReplicates;
	}

	public void setMinProteinReplicates(Integer minProteinReplicates) {
		this.minProteinReplicates = minProteinReplicates;
	}	

	public Boolean getUniquePeptides() {
		return uniquePeptides;
	}

	public void setUniquePeptides(Boolean uniquePeptides) {
		this.uniquePeptides = uniquePeptides;
	}

	public Boolean getUseFragmentIons() {
		return useFragmentIons;
	}

	public void setUseFragmentIons(Boolean useFragmentIons) {
		this.useFragmentIons = useFragmentIons;
	}
	
	public Score getPsmScoreThreshold() {
		return psmScoreThreshold;
	}

	public void setPsmScoreThreshold(Score psmScoreThreshold) {
		this.psmScoreThreshold = psmScoreThreshold;
	}

	public Score getPeptideScoreThreshold() {
		return peptideScoreThreshold;
	}

	public void setPeptideScoreThreshold(Score peptideScoreThreshold) {
		this.peptideScoreThreshold = peptideScoreThreshold;
	}

	public Score getProteinScoreThreshold() {
		return proteinScoreThreshold;
	}

	public void setProteinScoreThreshold(Score proteinScoreThreshold) {
		this.proteinScoreThreshold = proteinScoreThreshold;
	}

	public Score getGroupScoreThreshold() {
		return groupScoreThreshold;
	}

	public void setGroupScoreThreshold(Score groupScoreThreshold) {
		this.groupScoreThreshold = groupScoreThreshold;
	}

	public static class Replicate {
		private String name;
		private Set<String> fractions;

		@XmlAttribute
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@XmlElement(name="fraction")
		public Set<String> getFractions() {
			if( fractions == null )
				fractions = new HashSet<>();
			return fractions;
		}

		public void setFractions(Set<String> fractions) {
			this.fractions = fractions;
		}		
	}

	private final static String version = "1.0";
	private String description;
	private ScoreType psmScore;
	private String decoyRegex;
	
	private Double psmFdr;
	private Integer psmRankThreshold;
	private Boolean bestPsmPerPrecursor;
	private Boolean bestPsmPerPeptide;
	private Score psmScoreThreshold;
	
	private Integer minPeptideLength;
	private Boolean uniquePeptides;
	private Double peptideFdr;
	private Score peptideScoreThreshold;
	private Integer minPeptideReplicates;
	
	private Double proteinFdr;	
	private Score proteinScoreThreshold;
	private Integer minProteinReplicates;
	
	private Double groupFdr;
	private Score groupScoreThreshold;
	
	private List<Replicate> replicates;
	private Boolean useFragmentIons;
	private String output;
	private Boolean filterDecoys;
}