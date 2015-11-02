package es.ehubio.db.cosmic;

// See -> http://cancer.sanger.ac.uk/cancergenome/projects/cosmic/download
public class Entry {
	public String getGeneName() {
		return geneName;
	}
	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	public String getGeneCdsLength() {
		return geneCdsLength;
	}
	public void setGeneCdsLength(String geneCdsLength) {
		this.geneCdsLength = geneCdsLength;
	}
	public String getHgncId() {
		return hgncId;
	}
	public void setHgncId(String hgncId) {
		this.hgncId = hgncId;
	}
	public String getSampleName() {
		return sampleName;
	}
	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}
	public String getIdSample() {
		return idSample;
	}
	public void setIdSample(String idSample) {
		this.idSample = idSample;
	}
	public String getIdTumour() {
		return idTumour;
	}
	public void setIdTumour(String idTumour) {
		this.idTumour = idTumour;
	}
	public String getPrimarySite() {
		return primarySite;
	}
	public void setPrimarySite(String primarySite) {
		this.primarySite = primarySite;
	}
	public String getSiteSubtype1() {
		return siteSubtype1;
	}
	public void setSiteSubtype1(String siteSubtype1) {
		this.siteSubtype1 = siteSubtype1;
	}
	public String getPrimaryHistology() {
		return primaryHistology;
	}
	public void setPrimaryHistology(String primaryHistology) {
		this.primaryHistology = primaryHistology;
	}
	public String getHistologySubtype1() {
		return histologySubtype1;
	}
	public void setHistologySubtype1(String histologySubtype1) {
		this.histologySubtype1 = histologySubtype1;
	}
	public String getGws() {
		return gws;
	}
	public void setGws(String gws) {
		this.gws = gws;
	}
	public String getMutationId() {
		return mutationId;
	}
	public void setMutationId(String mutationId) {
		this.mutationId = mutationId;
	}
	public String getMutationCds() {
		return mutationCds;
	}
	public void setMutationCds(String mutationCds) {
		this.mutationCds = mutationCds;
	}
	public String getMutationAa() {
		return mutationAa;
	}
	public void setMutationAa(String mutationAa) {
		this.mutationAa = mutationAa;
	}
	public String getMutationDescription() {
		return mutationDescription;
	}
	public void setMutationDescription(String mutationDescription) {
		this.mutationDescription = mutationDescription;
	}
	public String getMutationZygosity() {
		return mutationZygosity;
	}
	public void setMutationZygosity(String mutationZygosity) {
		this.mutationZygosity = mutationZygosity;
	}
	public String getMutationNcbi36GenomePosition() {
		return mutationNcbi36GenomePosition;
	}
	public void setMutationNcbi36GenomePosition(String mutationNcbi36GenomePosition) {
		this.mutationNcbi36GenomePosition = mutationNcbi36GenomePosition;
	}
	public String getMutationGrch37GenomePosition() {
		return mutationGrch37GenomePosition;
	}
	public void setMutationGrch37GenomePosition(String mutationGrch37GenomePosition) {
		this.mutationGrch37GenomePosition = mutationGrch37GenomePosition;
	}
	public String getMutationGrch37Strand() {
		return mutationGrch37Strand;
	}
	public void setMutationGrch37Strand(String mutationGrch37Strand) {
		this.mutationGrch37Strand = mutationGrch37Strand;
	}
	public String getSnp() {
		return snp;
	}
	public void setSnp(String snp) {
		this.snp = snp;
	}
	public String getFathmmPrediction() {
		return fathmmPrediction;
	}
	public void setFathmmPrediction(String fathmmPrediction) {
		this.fathmmPrediction = fathmmPrediction;
	}
	public String getMutationSomaticStatus() {
		return mutationSomaticStatus;
	}
	public void setMutationSomaticStatus(String mutationSomaticStatus) {
		this.mutationSomaticStatus = mutationSomaticStatus;
	}
	public String getPubmedId() {
		return pubmedId;
	}
	public void setPubmedId(String pubmedId) {
		this.pubmedId = pubmedId;
	}
	public String getIdStudy() {
		return idStudy;
	}
	public void setIdStudy(String idStudy) {
		this.idStudy = idStudy;
	}
	public String getSampleSource() {
		return sampleSource;
	}
	public void setSampleSource(String sampleSource) {
		this.sampleSource = sampleSource;
	}
	public String getTumourOrigin() {
		return tumourOrigin;
	}
	public void setTumourOrigin(String tumourOrigin) {
		this.tumourOrigin = tumourOrigin;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	private String geneName;
	private String accession;
	private String geneCdsLength;
	private String hgncId;
	private String sampleName;
	private String idSample;
	private String idTumour;
	private String primarySite;
	private String siteSubtype1;
	private String primaryHistology;
	private String histologySubtype1;
	private String gws;
	private String mutationId;	
	private String mutationCds;
	private String mutationAa;
	private String mutationDescription;
	private String mutationZygosity;
	private String mutationNcbi36GenomePosition;
	private String mutationGrch37GenomePosition;
	private String mutationGrch37Strand;
	private String snp;
	private String fathmmPrediction;
	private String mutationSomaticStatus;
	private String pubmedId;
	private String idStudy;
	private String sampleSource;
	private String tumourOrigin;
	private String age;
	private String comments;
}
