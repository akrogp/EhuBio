package es.ehubio.proteomics.pipeline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import es.ehubio.proteomics.AmbiguityGroup;
import es.ehubio.proteomics.AmbiguityItem;
import es.ehubio.proteomics.Gene;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.Spectrum;
import es.ehubio.proteomics.Transcript;
import es.ehubio.proteomics.psi.mzid11.CVParamType;
import es.ehubio.proteomics.psi.mzid11.UserParamType;

public class Filter {
	//private final static Logger logger = Logger.getLogger(Filter.class.getName());	
	private Score psmScoreThreshold;
	private ScoreType onlyBestPsmPerPrecursor;
	private ScoreType onlyBestPsmPerPeptide;
	private boolean passThreshold = false;
	private int rankTreshold = 0;
	private Double ppmThreshold;
	
	private Score peptideScoreThreshold;
	private int minPeptideLength = 0;
	private int maxPeptideLength = 0;
	private Boolean filterDecoyPeptides;
	private boolean uniquePeptides = false;
	private int minPeptideReplicates = 0;
	private boolean filterSpecialAminoacids = false;
	
	private Score proteinScoreThreshold;
	private int minProteinReplicates = 0;	
	private Score groupScoreThreshold;	
	private Score transcriptScoreThreshold;
	private Score geneScoreThreshold;
		
	private final MsMsData data;	
	
	
	public Filter( MsMsData data ) {
		this.data = data;
	}
	
	public MsMsData getData() {
		return data;
	}
	
	public Score getPsmScoreThreshold() {
		return psmScoreThreshold;
	}
	
	public void setPsmScoreThreshold( Score psmScoreThreshold ) {
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
	
	public Score getTranscriptScoreThreshold() {
		return transcriptScoreThreshold;
	}

	public void setTranscriptScoreThreshold(Score transcriptScoreThreshold) {
		this.transcriptScoreThreshold = transcriptScoreThreshold;
	}

	public Score getGeneScoreThreshold() {
		return geneScoreThreshold;
	}

	public void setGeneScoreThreshold(Score geneScoreThreshold) {
		this.geneScoreThreshold = geneScoreThreshold;
	}
	
	public boolean isUniquePeptides() {
		return uniquePeptides;
	}

	public void setUniquePeptides(boolean uniquePeptides) {
		this.uniquePeptides = uniquePeptides;
	}
	
	public boolean isFilterSpecialAminoacids() {
		return filterSpecialAminoacids;
	}

	public void setFilterSpecialAminoacids(boolean filterSpecialAminoacids) {
		this.filterSpecialAminoacids = filterSpecialAminoacids;
	}
	
	public int getMinPeptideLength() {
		return minPeptideLength;
	}
	
	public void setMinPeptideLength(int minPeptideLength) {
		this.minPeptideLength = minPeptideLength;
	}
	
	public int getMaxPeptideLength() {
		return maxPeptideLength;
	}
	
	public void setMaxPeptideLength(int maxPeptideLength) {
		this.maxPeptideLength = maxPeptideLength;
	}

	public boolean isFilterDecoyPeptides() {
		return filterDecoyPeptides == null ? false : filterDecoyPeptides;
	}

	public void setFilterDecoyPeptides(boolean filterDecoyPeptides) {
		this.filterDecoyPeptides = filterDecoyPeptides;
	}

	public boolean isPassThreshold() {
		return passThreshold;
	}

	public void setPassThreshold(boolean mzidPassThreshold) {
		this.passThreshold = mzidPassThreshold;
	}
	
	public int getRankTreshold() {
		return rankTreshold;
	}

	public void setRankTreshold(int rankTreshold) {
		this.rankTreshold = rankTreshold;
	}
	
	public Double getPpmThreshold() {
		return ppmThreshold;
	}

	public void setPpmThreshold(Double ppmThreshold) {
		this.ppmThreshold = ppmThreshold;
	}
	
	public boolean isOnlyBestPsmPerPrecursor() {
		return onlyBestPsmPerPrecursor != null;
	}

	public void setOnlyBestPsmPerPrecursor(ScoreType scoreType) {
		this.onlyBestPsmPerPrecursor = scoreType;
	}
	
	public boolean isOnlyBestPsmPerPeptide() {
		return onlyBestPsmPerPeptide != null;
	}

	public void setOnlyBestPsmPerPeptide(ScoreType onlyBestPsmPerPeptide) {
		this.onlyBestPsmPerPeptide = onlyBestPsmPerPeptide;
	}	
	
	public int getMinPeptideReplicates() {
		return minPeptideReplicates;
	}

	public void setMinPeptideReplicates(int minPeptideReplicates) {
		this.minPeptideReplicates = minPeptideReplicates;
	}

	public int getMinProteinReplicates() {
		return minProteinReplicates;
	}

	public void setMinProteinReplicates(int minProteinReplicates) {
		this.minProteinReplicates = minProteinReplicates;
	}
	
	public void run() {
		filterGroups();
		filterGenes();
		filterTranscripts();
		filterProteins();
		filterPeptides();		
		filterPsms();
		
		Set<Spectrum> spectra = new HashSet<>();
		for( Spectrum spectrum : data.getSpectra() )
			if( !spectrum.getPsms().isEmpty() )
				spectra.add(spectrum);
		data.loadFromSpectra(spectra);
		
		updateMetaData();
	}
	
	private void filterPsms() {				
		for( Psm psm : data.getPsms() ) {
			if( psm.getPeptide() == null ) {
				unlinkPsm(psm);
				continue;
			}
			if( getRankTreshold() > 0 && (psm.getRank() == null || psm.getRank() > getRankTreshold()) ) {
				unlinkPsm(psm);
				continue;
			}
			if( getPpmThreshold() != null && (psm.getMassPpm() == null || psm.getMassPpm() > getPpmThreshold()) ) {
				unlinkPsm(psm);
				continue;
			}
			if( isPassThreshold() && !psm.isPassThreshold() ) {
				unlinkPsm(psm);
				continue;
			}
			if( getPsmScoreThreshold() == null )
				continue;
			Score score = psm.getScoreByType(getPsmScoreThreshold().getType());
			if( score == null || getPsmScoreThreshold().compare(score.getValue()) > 0 )
				unlinkPsm(psm);
		}
		
		if( isOnlyBestPsmPerPeptide() )
			filterPsmsByPeptide();		
		else if( isOnlyBestPsmPerPrecursor() )
			filterPsmsByPrecursor();
	}

	private void filterPsmsByPeptide() {
		for( Peptide peptide : data.getPeptides() ) {
			Psm best = peptide.getBestPsm(onlyBestPsmPerPeptide);
			for( Psm psm : peptide.getPsms().toArray(new Psm[0]) )
				if( psm != best )
					unlinkPsm(psm);
		}
	}

	private void filterPsmsByPrecursor() {
		Map<Integer,Double> bestScores = new HashMap<>();
		Map<Integer,Psm> bestPsms = new HashMap<>();
		Double prev, cur;
		for( Peptide peptide : data.getPeptides() ) {
			bestScores.clear();
			bestPsms.clear();
			for( Psm psm : peptide.getPsms() ) {
				Score score = psm.getScoreByType(onlyBestPsmPerPrecursor);
				if( score == null )
					continue;
				prev = bestScores.get(psm.getCharge());
				cur = score.getValue();
				if( prev == null || onlyBestPsmPerPrecursor.compare(cur,prev) > 0 ) {
					bestScores.put(psm.getCharge(), cur);
					bestPsms.put(psm.getCharge(), psm);
				}
			}
			for( Psm psm : peptide.getPsms().toArray(new Psm[0]) )
				if( !psm.equals(bestPsms.get(psm.getCharge())) )
					unlinkPsm(psm);
		}
	}

	private void filterPeptides() {
		for( Peptide peptide : data.getPeptides() ) {
			if( peptide.getPsms().isEmpty() ) {
				unlinkPeptide(peptide);
				continue;
			}
			if( peptide.getProteins().isEmpty() ) {
				unlinkPeptide(peptide);
				continue;
			}
			if( peptide.getSequence().length() < getMinPeptideLength() ) {
				unlinkPeptide(peptide);
				continue;
			}
			if( getMaxPeptideLength() > 0 && peptide.getSequence().length() > getMaxPeptideLength() ) {
				unlinkPeptide(peptide);
				continue;
			}
			if( isFilterDecoyPeptides() && Boolean.TRUE.equals(peptide.getDecoy()) ) {
				unlinkPeptide(peptide);
				continue;
			}
			if( isPassThreshold() && !peptide.isPassThreshold() ) {
				unlinkPeptide(peptide);
				continue;
			}
			if( isUniquePeptides() && peptide.getProteins().size() != 1 ) {
				unlinkPeptide(peptide);
				continue;
			}
			if( getMinPeptideReplicates() > 1 && peptide.getReplicates().size() < getMinPeptideReplicates() ) {
				unlinkPeptide(peptide);
				continue;
			}
			if( peptide.getSequence().toLowerCase().matches(".*[bjzx].*") ) {
				unlinkPeptide(peptide);
				continue;
			}
			if( getPeptideScoreThreshold() == null )
				continue;
			Score score = peptide.getScoreByType(getPeptideScoreThreshold().getType());
			if( score == null || getPeptideScoreThreshold().compare(score.getValue()) > 0 )
				unlinkPeptide(peptide);
		}				
	}

	private void filterProteins() {
		for( Protein protein : data.getProteins() ) {
			if( protein.getPeptides().isEmpty() ) {
				unlinkProtein(protein);
				continue;
			}
			if( isPassThreshold() && !protein.isPassThreshold() ) {
				unlinkProtein(protein);
				continue;
			}
			if( getMinProteinReplicates() > 1 && protein.getReplicates().size() < getMinProteinReplicates() ) {
				unlinkProtein(protein);
				continue;
			}
			if( getProteinScoreThreshold() == null )
				continue;
			Score score = protein.getScoreByType(getProteinScoreThreshold().getType());
			if( score == null || getProteinScoreThreshold().compare(score.getValue()) > 0 )
				unlinkProtein(protein);
		}
	}

	private void filterGroups() {		
		for( AmbiguityGroup group : data.getGroups() ) {
			if( group.getItems().isEmpty() ) {
				unlinkGroup(group);
				continue;
			}
			if( isPassThreshold() && !group.isPassThreshold() ) {
				unlinkGroup(group);
				continue;
			}
			if( getGroupScoreThreshold() == null )
				continue;
			Score score = group.getScoreByType(getGroupScoreThreshold().getType());
			//if( score == null || getGroupScoreThreshold().compare(score.getValue()) > 0 )
			if( score != null && getGroupScoreThreshold().compare(score.getValue()) > 0 )
				unlinkGroup(group);
		}
	}
	
	private void filterTranscripts() {
		for( Transcript transcript : data.getTranscripts() ) {
			if( transcript.getProteins().isEmpty() ) {
				unlinkTranscript(transcript);
				continue;
			}
			if( isPassThreshold() && !transcript.isPassThreshold() ) {
				unlinkTranscript(transcript);
				continue;
			}
			if( getTranscriptScoreThreshold() == null )
				continue;
			Score score = transcript.getScoreByType(getTranscriptScoreThreshold().getType());
			if( score == null || getTranscriptScoreThreshold().compare(score.getValue()) > 0 )
				unlinkTranscript(transcript);
		}
	}
	
	private void filterGenes() {
		for( Gene gene : data.getGenes() ) {
			if( gene.getTranscripts().isEmpty() ) {
				unlinkGene(gene);
				continue;
			}
			if( isPassThreshold() && !gene.isPassThreshold() ) {
				unlinkGene(gene);
				continue;
			}
			if( getGeneScoreThreshold() == null )
				continue;
			Score score = gene.getScoreByType(getGeneScoreThreshold().getType());
			if( score == null || getGeneScoreThreshold().compare(score.getValue()) > 0 )
				unlinkGene(gene);
		}
	}

	private void updateMetaData() {
		updatePsmMetaData();
		updatePeptideMetaData();
		updateProteinMetaData();
		updateGroupMetaData();

		CVParamType cvParam = new CVParamType();
		cvParam.setAccession("MS:1001194");
		cvParam.setCvRef("PSI-MS");
		cvParam.setName("quality estimation with decoy database");
		cvParam.setValue(""+(filterDecoyPeptides!=null));
		data.setAnalysisParam(cvParam);
	}

	private void updatePsmMetaData() {
		UserParamType userParam = null;
		
		if( getPsmScoreThreshold() != null ) {
			userParam = new UserParamType();
			userParam.setName("PAnalyzer:PSM score type");
			userParam.setValue(getPsmScoreThreshold().getName());
			data.setAnalysisParam(userParam);
			userParam = new UserParamType();
			userParam.setName("PAnalyzer:PSM score threshold");
			userParam.setValue(getPsmScoreThreshold().getValue()+"");
			data.setAnalysisParam(userParam);
		}
		if( getRankTreshold() > 0 ) {
			userParam = new UserParamType();
			userParam.setName("PAnalyzer:PSM rank threshold");
			userParam.setValue(getRankTreshold()+"");
			data.setAnalysisParam(userParam);
		}
		if( getPpmThreshold() != null ) {
			userParam = new UserParamType();
			userParam.setName("PAnalyzer:PSM ppm threshold");
			userParam.setValue(getPpmThreshold()+"");
			data.setAnalysisParam(userParam);
		}
		if( isOnlyBestPsmPerPrecursor() ) {
			userParam = new UserParamType();
			userParam.setName("PAnalyzer:Only best psm per precursor");
			userParam.setValue(""+isOnlyBestPsmPerPrecursor());
			data.setAnalysisParam(userParam);
		}
		
		userParam = new UserParamType();
		userParam.setName("PAnalyzer:Using original file PSM threshold");
		userParam.setValue(isPassThreshold()+"");
		data.setAnalysisParam(userParam);
	}
	
	private void updatePeptideMetaData() {
		UserParamType userParam = null;
		
		if( getMinPeptideLength() > 0 ) {
			userParam = new UserParamType();
			userParam.setName("PAnalyzer:Minimum peptide length");
			userParam.setValue(""+getMinPeptideLength());
			data.setAnalysisParam(userParam);
		}
		if( getPeptideScoreThreshold() != null ) {
			userParam = new UserParamType();
			userParam.setName("PAnalyzer:Peptide score type");
			userParam.setValue(getPeptideScoreThreshold().getName());
			data.setAnalysisParam(userParam);
			userParam = new UserParamType();
			userParam.setName("PAnalyzer:Peptide score threshold");
			userParam.setValue(getPeptideScoreThreshold().getValue()+"");
			data.setAnalysisParam(userParam);
		}
		if( filterDecoyPeptides != null ) {
			userParam = new UserParamType();
			userParam.setName("PAnalyzer:Decoys removed");
			userParam.setValue(isFilterDecoyPeptides()+"");
			data.setAnalysisParam(userParam);
		}
	}
	
	private void updateProteinMetaData() {
		UserParamType userParam = null;
		
		if( getProteinScoreThreshold() != null ) {
			userParam = new UserParamType();
			userParam.setName("PAnalyzer:Protein score type");
			userParam.setValue(getProteinScoreThreshold().getName());
			data.setAnalysisParam(userParam);
			userParam = new UserParamType();
			userParam.setName("PAnalyzer:Protein score threshold");
			userParam.setValue(getProteinScoreThreshold().getValue()+"");
			data.setAnalysisParam(userParam);
		}
	}
	
	private void updateGroupMetaData() {
		UserParamType userParam = null;
		
		if( getGroupScoreThreshold() != null ) {
			userParam = new UserParamType();
			userParam.setName("PAnalyzer:Protein group score type");
			userParam.setValue(getGroupScoreThreshold().getName());
			data.setAnalysisParam(userParam);
			userParam = new UserParamType();
			userParam.setName("PAnalyzer:Protein group score threshold");
			userParam.setValue(getGroupScoreThreshold().getValue()+"");
			data.setAnalysisParam(userParam);
		}
	}

	private static void unlinkGroup( AmbiguityGroup group ) {
		group.setPassThreshold(false);
		for( AmbiguityItem item : group.getItems().toArray(new AmbiguityItem[0]) )
			//if( item.getGroup() == group ) {
				if( item instanceof Protein )
					unlinkProtein((Protein)item);
				else if( item instanceof Transcript )
					unlinkTranscript((Transcript)item);
				else if( item instanceof Gene )
					unlinkGene((Gene)item);
				else
					throw new UnsupportedOperationException();
			/*}
			else
				throw new AssertionError();*/
	}
	
	private static void unlinkItemGroup(AmbiguityItem item) {
		if( item.getGroup() == null )
			return;
		item.getGroup().getItems().remove(item);
		item.linkGroup(null);
	}

	private static void unlinkProtein(Protein protein) {
		protein.setPassThreshold(false);
		for( Peptide peptide : protein.getPeptides().toArray(new Peptide[0]) ) {
			peptide.getProteins().remove(protein);
			if( peptide.getProteins().isEmpty() )
				unlinkPeptide(peptide);
		}
		for( Transcript transcript : protein.getTranscripts().toArray(new Transcript[0]) ) {
			transcript.getProteins().remove(protein);
			if( transcript.getProteins().isEmpty() )
				unlinkTranscript(transcript);
		}
		unlinkItemGroup(protein);
	}
	
	private static void unlinkTranscript(Transcript transcript) {
		transcript.setPassThreshold(false);
		for( Protein protein : transcript.getProteins().toArray(new Protein[0]) ) {
			protein.getTranscripts().remove(transcript);
			if( protein.getTranscripts().isEmpty() )
				unlinkProtein(protein);
		}
		Gene gene = transcript.getGene(); 
		if( gene != null ) {
			transcript.linkGene(null);
			gene.getTranscripts().remove(transcript);
			if( gene.getTranscripts().isEmpty() )
				unlinkGene(gene);
		}
		unlinkItemGroup(transcript);
	}

	private static void unlinkGene(Gene gene) {
		gene.setPassThreshold(false);
		for( Transcript transcript : gene.getTranscripts().toArray(new Transcript[0]) )
			unlinkTranscript(transcript);
		unlinkItemGroup(gene);
	}
	
	private static void unlinkPeptide( Peptide peptide ) {
		peptide.setPassThreshold(false);
		for( Psm psm : peptide.getPsms().toArray(new Psm[0]) )
			unlinkPsm(psm);		
		
		for( Protein protein : peptide.getProteins().toArray(new Protein[0]) ) {
			protein.getPeptides().remove(peptide);
			if( protein.getPeptides().isEmpty() )
				unlinkProtein(protein);
		}		
	}	

	private static void unlinkPsm( Psm psm ) {
		psm.setPassThreshold(false);
		Spectrum spectrum = psm.getSpectrum(); 
		if( spectrum != null ) {
			psm.linkSpectrum(null);
			spectrum.getPsms().remove(psm);
		}
		Peptide peptide = psm.getPeptide(); 
		if( peptide != null ) {
			psm.linkPeptide(null);
			peptide.getPsms().remove(psm);
			if( peptide.getPsms().isEmpty() )
				unlinkPeptide(peptide);
		}
	}	
}