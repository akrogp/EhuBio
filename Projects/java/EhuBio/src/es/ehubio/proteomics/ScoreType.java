package es.ehubio.proteomics;

import java.util.HashMap;
import java.util.Map;

public enum ScoreType {	
	MASCOT_EVALUE("MS:1001172","mascot:expectation value","The Mascot result 'expectation value",false),
	MASCOT_SCORE("MS:1001171","mascot:score","The Mascot result 'Score'",true),
	SEQUEST_XCORR("MS:1001155","SEQUEST:xcorr","The SEQUEST result 'XCorr'",true),
	XTANDEM_EVALUE("MS:1001330","X!Tandem:expect","The X!Tandem expectation value",false),
	XTANDEM_HYPERSCORE("MS:1001331","X!Tandem:hyperscore","The X!Tandem hyperscore",true),	
	PSM_P_VALUE("MS:1002352","PSM-level p-value","Estimation of the p-value for peptide spectrum matches",false),
	PSM_LOCAL_FDR("MS:1002351","PSM-level local FDR","Estimation of the local false discovery rate of peptide spectrum matches",false),
	PSM_Q_VALUE("MS:1002354","PSM-level q-value","Estimation of the q-value for peptide spectrum matches",false),
	PSM_FDR_SCORE("MS:1002355","PSM-level FDRScore","FDRScore for peptide spectrum matches",false),
	LPS_SCORE(null,"PSM-level LPS score","Cologarithm of PSM-level p-value",true),
	PSM_PLGS_SCORE(null,"PSM-level PLGS score","PSM-level PLGS score",true),
	PSM_PLGS_COLOR(null,"PSM-level PLGS color number","PSM-level PLGS color number: red(1), yellow(2), green(3)",true),
	OTHER_LARGER(null,"other:larger","Other, larger values are better",true),
	OTHER_SMALLER(null,"other:smaller","Other, smaller values are better",false),
	PROPHET_PROBABILITY(null,"PeptideProphet:probability","PeptideProphet probability score",true),
	PEPTIDE_P_VALUE(null,"peptide-level p-value","Estimation of the p-value for peptides",false),
	PEPTIDE_LOCAL_FDR("MS:1002359","distinct peptide-level local FDR","Estimation of the local false discovery rate for distinct peptides once redundant identifications of the same peptide have been removed (id est multiple PSMs have been collapsed to one entry)",false),
	PEPTIDE_Q_VALUE(null,"peptide-level q-value","Estimation of the q-value for peptides",false),
	PEPTIDE_FDR_SCORE("MS:1002360","distinct peptide-level FDRScore","FDRScore for distinct peptides once redundant identifications of the same peptide have been removed (id est multiple PSMs have been collapsed to one entry)",false),
	LPP_SCORE(null,"peptide-level LPP score","Sum of PSM-level spHPP scores",true),
	PEPTIDE_MSF_CONFIDENCE(null,"msf peptide confidence level number","Peptide confidence level in MSF: low(1), middle(2), high(3)",true),
	PROTEIN_P_VALUE("MS:1001871","protein-level p-value","Estimation of the p-value for proteins",false),
	PROTEIN_LOCAL_FDR("MS:1002364","protein-level local FDR","Estimation of the local false discovery rate of proteins",false),
	PROTEIN_Q_VALUE("MS:1001869","protein-level q-value","Estimation of the q-value for proteins",false),
	PROTEIN_FDR_SCORE(null,"protein-level FDRScore","FDRScore for proteins",false),
	LPQ_SCORE(null,"protein-level LPQ score","Normalized sum of peptide-level spHPP scores",true),
	NQ_EVALUE(null,"protein-level expected Nq value","Expected number of non-normalized randon matching peptides",false),
	NQ_OVALUE(null,"protein-level observed Nq value","Observed number of non-normalized randon matching peptides",false),
	MQ_EVALUE(null,"protein-level expected Mq value","Expected number of normalized randon matching peptides",false),
	MQ_OVALUE(null,"protein-level observed Mq value","Observed number of normalized randon matching peptides",false),
	LPQCORR_SCORE(null,"protein-level LPQcorr score","LPQ score corrected according to Mq value",true),
	PROTEIN_PLGS_SCORE(null,"protein-level PLGS score","Protein-level PLGS score",true),
	GROUP_P_VALUE("MS:1002371","protein group-level p-value","Estimation of the p-value for protein groups",false),
	GROUP_LOCAL_FDR("MS:1002370","protein group-level local FDR","Estimation of the local false discovery rate of protein groups", false),
	GROUP_Q_VALUE("MS:1002373","protein group-level q-value","Estimation of the q-value for protein groups",false),
	GROUP_FDR_SCORE(null,"group-level FDRScore","FDRScore for protein groups",false),
	LPG_SCORE(null,"group-level LPG score","Sum of LPQ scores of the proteins in the group",true),
	MG_EVALUE(null,"group-level expected Mg value","Sum of expected Mq values of the proteins in the group",false),
	MG_OVALUE(null,"group-level observed Mg value","Sum of observed Mq values of the proteins in the group",false),
	LPGCORR_SCORE(null,"group-level LPGcorr score","LPG score corrected according to Mq value",true),
	LOCAL_FDR(null,"local FDR","Estimation of the local false discovery rate",false),
	Q_VALUE(null,"q-value","Estimation of the q-value",false),
	FDR_SCORE(null,"FDRScore","FDRScore",false),
	LP_SCORE(null,"LP score","Cologarithm of p-value",true),
	LPCORR_SCORE(null,"LPCorr score","LP score corrected",true),
	ID_COUNT(null,"ID count","Number of times identified",true),
	N_DVALUE(null,"N(db)","Number of non-normalized randon matching peptides calculated from DB",false),
	N_EVALUE(null,"N(exp)","Expected number of non-normalized randon matching peptides",false),
	N_OVALUE(null,"N(obs)","Observed number of non-normalized randon matching peptides",false),
	M_DVALUE(null,"M(db)","Number of normalized randon matching peptides calculated from DB",false),
	M_EVALUE(null,"M(exp)","Expected number of normalized randon matching peptides",false),
	M_OVALUE(null,"M(obs)","Observed number of normalized randon matching peptides",false),
	EVIDENCE(null,"Evidence","PAnalyzer evidence type",false),
	DEGENERACY(null,"Degeneracy","PAnalyzer degeneracy type",false);
	
	private final String accession;
	private final String name;
	private final String description;
	private final boolean largerBetter;
	
	private final static Map<String,ScoreType> mapAccession = new HashMap<>();	
	private final static Map<String,ScoreType> mapName = new HashMap<>();
	static {
		for( ScoreType type : ScoreType.values() ) {
			if( type.getAccession() != null )
				mapAccession.put(type.getAccession(), type);
			if( type.getName() != null )
				mapName.put(type.getName(), type);
		}
	}
	
	private ScoreType( String accession, String name, String description, boolean largerBetter ) {		
		this.accession = accession;
		this.name = name;
		this.description = description;
		this.largerBetter = largerBetter;
	}
	
	public String getAccession() {
		return accession;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

	public boolean isLargerBetter() {
		return largerBetter;
	}
	
	public int compare( double v1, double v2 ) {
		if( v1 == v2 )
			return 0;
		if( isLargerBetter() )
			v2 = v1-v2;
		else
			v2 = v2-v1;
		return (int)Math.signum(v2);
	}
	
	public static ScoreType getByAccession( String accession ) {
		return mapAccession.get(accession);
	}
	
	public static ScoreType getByName( String name ) {
		return mapName.get(name);
	}
	
	public static ScoreType getByName( String name, ScoreType defaultType ) {
		ScoreType type = getByName(name);
		return type != null ? type : defaultType;
	}
	
	@Override
	public String toString() {
		//return getName();
		return getDescription();
	}
}