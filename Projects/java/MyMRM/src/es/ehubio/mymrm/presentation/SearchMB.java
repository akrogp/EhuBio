package es.ehubio.mymrm.presentation;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.mymrm.data.Fragment;
import es.ehubio.mymrm.data.Peptide;
import es.ehubio.mymrm.data.Score;
import es.ehubio.proteomics.Enzyme;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.pipeline.Digester;
import es.ehubio.proteomics.pipeline.PAnalyzer;

@ManagedBean
@SessionScoped
public class SearchMB implements Serializable {
	private static final long serialVersionUID = 1L;
	private String peptideSequence;
	private List<PeptideBean> seqPeptides;
	private PrecursorBean precursor;
	private String proteinAccession;
	private String fastaFile, prevFastaFile;
	private Enzyme enzyme, prevEnzyme;
	private Protein protein;
	private List<CandidateBean> candidates = new ArrayList<>();
	private Map<String, Protein> proteins = new HashMap<>();
	private int minPeptideLength = 7, prevPeptideLength = 0;
	
	public String getPeptideSequence() {
		return peptideSequence;
	}
	
	public void setPeptideSequence(String peptide) {
		this.peptideSequence = peptide;
	}
	
	public void searchPeptide( DatabaseMB db ) {
		seqPeptides = new ArrayList<>();
		for( Peptide peptide : db.search(peptideSequence) ) {
			PeptideBean bean = new PeptideBean();
			bean.setEntity(peptide);
			seqPeptides.add(bean);
		}
	}
	
	public String searchProteinPeptide( DatabaseMB db, String seq ) {
		peptideSequence = seq;
		searchPeptide(db);
		return "peptide";
	}
	
	public void searchProtein( DatabaseMB db ) {
		protein = null;
		candidates.clear();
		try {
			if( prevPeptideLength != minPeptideLength || !fastaFile.equals(prevFastaFile) || !enzyme.equals(prevEnzyme) ) {
				redigest();
				prevPeptideLength = minPeptideLength;
				prevFastaFile = fastaFile;
				prevEnzyme = enzyme;				
			}
			protein = proteins.get(proteinAccession.toUpperCase());
			if( protein != null ) {
				for( es.ehubio.proteomics.Peptide peptide : protein.getPeptides() ) {
					CandidateBean candidate = new CandidateBean();
					candidate.setPeptide(peptide);
					candidate.setAvailable(db.checkPeptideAvailable(peptide.getSequence())>0);
					candidates.add(candidate);
				}
				Collections.sort(candidates, new Comparator<CandidateBean>() {
					@Override
					public int compare(CandidateBean c1, CandidateBean c2) {
						if( c1.isAvailable() != c2.isAvailable() )
							return c1.isAvailable() ? -1 : 1;
						int res = c1.getPeptide().getConfidence().getOrder() - c2.getPeptide().getConfidence().getOrder(); 
						if( res != 0 )
							return res;
						res = c2.getPeptide().getSequence().length()-c1.getPeptide().getSequence().length();
						return res;
					}
				});
			}
		} catch (IOException | InvalidSequenceException e) {
			e.printStackTrace();
		}
	}
	
	private void redigest() throws IOException, InvalidSequenceException {
		proteins.clear();
		Set<es.ehubio.proteomics.Peptide> peptides = Digester.digestDatabase(
			new File(DatabaseMB.getFastaDir(),fastaFile).getAbsolutePath(),
			enzyme, minPeptideLength);
		MsMsData data = new MsMsData();
		data.loadFromPeptides(peptides);
		PAnalyzer pAnalyzer = new PAnalyzer(data);
		pAnalyzer.run();
		for( Protein protein : data.getProteins() )
			proteins.put(protein.getAccession().toUpperCase(), protein);
	}

	public List<PeptideBean> getSeqPeptides() {
		return seqPeptides;
	}

	public PrecursorBean getPrecursor() {
		return precursor;
	}
	
	public String showDetails( PrecursorBean bean, DatabaseMB db ) {
		this.precursor = bean;
		for( DetailsBean experiment : bean.getExperiments() ) {
			if( experiment.getFragments().isEmpty() ) {			
				for( Fragment fragment : db.getFragments(experiment.getPrecursor().getId()) ) {
					FragmentBean fragmentBean = new FragmentBean();
					fragmentBean.setEntity(fragment);
					experiment.getFragments().add(fragmentBean);
				}
				Collections.sort(experiment.getFragments(), new Comparator<FragmentBean>() {
					@Override
					public int compare(FragmentBean o1, FragmentBean o2) {
						if( o1.getEntity().getIntensity() != o2.getEntity().getIntensity() )
							return (int)Math.signum(o2.getEntity().getIntensity()-o1.getEntity().getIntensity());
						return (int)Math.signum(Math.abs(o1.getPpm())-Math.abs(o2.getPpm()));
					}
				});
			}
			if( experiment.getScores().isEmpty() ) {
				experiment.getScores().addAll(db.getScores(experiment.getEvidence().getId()));
				Collections.sort(experiment.getScores(), new Comparator<Score>() {
					@Override
					public int compare(Score o1, Score o2) {
						return o1.getScoreType().getName().compareTo(o2.getScoreType().getName());
					}
				});
			}
		}
		return "transitions";
	}

	public String getProteinAccession() {
		return proteinAccession;
	}

	public void setProteinAccession(String proteinAccession) {
		this.proteinAccession = proteinAccession;
	}

	public String getFastaFile() {
		return fastaFile;
	}

	public void setFastaFile(String fastaFile) {
		this.fastaFile = fastaFile;
	}

	public Protein getProtein() {
		return protein;
	}

	public List<CandidateBean> getCandidates() {
		return candidates;
	}
	
	public Enzyme[] getEnzymes() {
		return Enzyme.class.getEnumConstants();
	}

	public Enzyme getEnzyme() {
		return enzyme;
	}

	public void setEnzyme(Enzyme enzyme) {
		this.enzyme = enzyme;
	}

	public int getMinPeptideLength() {
		return minPeptideLength;
	}

	public void setMinPeptideLength(int minPeptideLength) {
		this.minPeptideLength = minPeptideLength;
	}
}
