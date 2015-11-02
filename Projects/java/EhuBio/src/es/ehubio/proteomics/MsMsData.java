package es.ehubio.proteomics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.proteomics.psi.mzid11.AbstractParamType;
import es.ehubio.proteomics.psi.mzid11.AnalysisSoftwareType;
import es.ehubio.proteomics.psi.mzid11.BibliographicReferenceType;
import es.ehubio.proteomics.psi.mzid11.OrganizationType;
import es.ehubio.proteomics.psi.mzid11.ParamListType;
import es.ehubio.proteomics.psi.mzid11.PersonType;
import es.ehubio.proteomics.psi.mzid11.UserParamType;

/**
 * Mutable class for storing and processing data associated with
 * a MS/MS proteomics experiment.
 * 
 * @author gorka
 *
 */
public class MsMsData {
	public enum GroupingLevel {
		PROTEIN("protein"),
		TRANSCRIPT("transcript"),
		GENE("gene");
		private GroupingLevel( String name ) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		private final String name;
	}	
	
	public Set<Spectrum> getSpectra() {
		return spectra;
	}
	
	public int getSpectraCount() {
		return spectra.size();
	}
	
	public Set<Psm> getPsms() {
		return psms;
	}
	
	public int getPsmCount() {
		return psms.size();
	}
	
	public Set<Psm> getDecoyPsms() {
		return getDecoys(psms, true);
	}
	
	public int getDecoyPsmCount() {
		return getDecoyCount(psms, true);
	}
	
	public Set<Psm> getTargetPsms() {
		return getDecoys(psms, false);
	}
	
	public int getTargetPsmCount() {
		return getDecoyCount(psms, false);
	}
		
	public Set<Peptide> getPeptides() {
		return peptides;
	}
	
	@SuppressWarnings("unchecked")
	public Set<AmbiguityPart> getAmbiguityParts() {
		return (Set<AmbiguityPart>)(Set<? extends AmbiguityPart>)getPeptides();
	}
	
	public int getPeptideCount() {
		return peptides.size();
	}
	
	public Set<Peptide> getDecoyPeptides() {
		return getDecoys(peptides, true);
	}
	
	public int getDecoyPeptideCount() {
		return getDecoyCount(peptides, true);
	}
	
	public Set<Peptide> getTargetPeptides() {
		return getDecoys(peptides, false);
	}
	
	public int getTargetPeptideCount() {
		return getDecoyCount(peptides, false);
	}
	
	public Set<Protein> getProteins() {
		return proteins;
	}
	
	public int getProteinCount() {
		return proteins.size();
	}
	
	public Set<Protein> getDecoyProteins() {
		return getDecoys(proteins, true);
	}
	
	public int getDecoyProteinCount() {
		return getDecoyCount(proteins, true);
	}
	
	public Set<Protein> getTargetProteins() {
		return getDecoys(proteins, false);
	}
	
	public int getTargetProteinCount() {
		return getDecoyCount(proteins, false);
	}
	
	public Set<AmbiguityGroup> getGroups() {
		return groups;
	}
	
	public int getGroupCount() {
		return groups.size();
	}
	
	public Set<AmbiguityGroup> getDecoyGroups() {
		return getDecoys(groups, true);
	}
	
	public int getDecoyGroupCount() {
		return getDecoyCount(groups, true);
	}
	
	public Set<AmbiguityGroup> getTargetGroups() {
		return getDecoys(groups, false);
	}
	
	public int getDecoyTargetCount() {
		return getDecoyCount(groups, false);
	}
	
	private static <T extends DecoyBase> Set<T> getDecoys( Set<T> set, boolean decoy ) {
		Set<T> result = new HashSet<>();
		for( T item : set )
			if( item.isDecoy() == decoy )
				result.add(item);
		return result;
	}
	
	private static <T extends DecoyBase> int getDecoyCount( Set<T> set, boolean decoy ) {
		int count=0;
		for( T item : set )
			if( item.isDecoy() == decoy )
				count++;
		return count;
	}
	
	public MsMsData getDecoy() {
		return getDecoy(true);
	}
	
	public MsMsData getTarget() {
		return getDecoy(false);
	}
	
	private MsMsData getDecoy( boolean decoy ) {
		Set<Peptide> peptides = decoy ? getDecoyPeptides() : getTargetPeptides();
		MsMsData data = new MsMsData();
		data.loadFromPeptides(peptides);
		return data;
	}
	
	public void loadFromSpectra( Collection<Spectrum> spectra ) {
		psms.clear();
		peptides.clear();
		proteins.clear();
		transcripts.clear();
		genes.clear();
		groups.clear();
		if( this.spectra != spectra ) {
			this.spectra.clear();
			this.spectra.addAll(spectra);
		}
		for( Spectrum spectrum : spectra )			
			for( Psm psm : spectrum.getPsms() ) {				
				psms.add(psm);
				if( psm.getPeptide() == null )
					continue;
				peptides.add(psm.getPeptide());
				for( Protein protein : psm.getPeptide().getProteins() ) {
					proteins.add(protein);
					for( Transcript transcript : protein.getTranscripts() ) {
						transcripts.add(transcript);
						if( transcript.getGene() != null )
							genes.add(transcript.getGene());
					}
				}
			}
		for( AmbiguityItem item : getAmbiguityItems() )
			if( item.getGroup() != null )
				groups.add(item.getGroup());
	}
	
	private void loadFromPeptides( Set<Peptide> peptides ) {
		spectra.clear();
		psms.clear();
		this.peptides.clear();
		proteins.clear();
		groups.clear();
		for( Peptide peptide : peptides ) {
			Spectrum spectrum = new Spectrum();
			Psm psm = new Psm();
			psm.linkPeptide(peptide);
			psm.linkSpectrum(spectrum);
			spectra.add(spectrum);
		}
		loadFromSpectra(spectra);
	}
	
	public MsMsData markDecoys( String decoyRegex ) throws DecoyException {
		if( decoyRegex == null || decoyRegex.isEmpty() )
			return this;
		Pattern pattern = Pattern.compile(decoyRegex);
		int count = 0;
		for( Protein protein : getProteins() ) {
			Matcher matcher = pattern.matcher(protein.getAccession());
			protein.setDecoy(matcher.find());
			if( Boolean.TRUE.equals(protein.getDecoy()) )
				count++;
		}
		UserParamType param = new UserParamType();
		param.setName("PAnalyzer:Decoy regex");
		param.setValue(decoyRegex);
		setAnalysisParam(param);
		if( count == 0 )
			throw new DecoyException(String.format("No decoys found using %s regex", decoyRegex));
		return this;
	}
	
	public MsMsData markTarget() {
		for( Peptide peptide : peptides )
			peptide.setDecoy(false);
		return this;
	}
	
	public MsMsData markDecoy() {
		for( Peptide peptide : peptides )
			peptide.setDecoy(true);
		return this;
	}
	
	public long getRedundantPeptidesCount() {
		long count = 0;
		for( Protein protein : proteins )
			count += protein.getPeptides().size();
		return count;
	}
		
	public void clear() {
		spectra.clear();
		psms.clear();
		peptides.clear();
		proteins.clear();
		groups.clear();
		clearMetaData();
	}
	
	public void clearMetaData() {
		organization = null;
		author = null;
		software = null;
		publication = null;
		analysisParams = null;
		thresholds = null;
	}
	
	public OrganizationType getOrganization() {
		return organization;
	}

	public void setOrganization(OrganizationType organization) {
		this.organization = organization;
	}

	public PersonType getAuthor() {
		return author;
	}

	public void setAuthor(PersonType author) {
		this.author = author;
	}

	public AnalysisSoftwareType getSoftware() {
		return software;
	}

	public void setSoftware(AnalysisSoftwareType software) {
		this.software = software;
	}

	public BibliographicReferenceType getPublication() {
		return publication;
	}

	public void setPublication(BibliographicReferenceType publication) {
		this.publication = publication;
	}
	
	public void setAnalysisParam(AbstractParamType param) {
		if( analysisParams == null )
			analysisParams = new ParamListType();
		setParam(analysisParams.getCvParamsAndUserParams(), param);
	}
	
	public ParamListType getAnalysisParams() {
		return analysisParams;
	}
	
	public void setThreshold(AbstractParamType param) {
		if( thresholds == null )
			thresholds = new ParamListType();
		setParam(thresholds.getCvParamsAndUserParams(), param);
	}
	
	public ParamListType getThresholds() {
		return thresholds;
	}
	
	@Override
	public String toString() {
		return toString(getGroups().size(), getGenes().size(), getTranscripts().size(), getProteins().size(), getPeptides().size(), getPsms().size(), getSpectra().size());
	}
	
	public String toTargetString() {
		return toString(true);
	}
	
	public String toDecoyString() {
		return toString(false);
	}
	
	private String toString( boolean target ) {
		int spectra = 0;
		for( Spectrum spectrum : getSpectra() )
			for( Psm psm : spectrum.getPsms() )
				if( psm.isTarget() == target ) {
					spectra++;
					break;
				}
		boolean decoy = !target;
		return toString(
			getDecoyCount(groups, decoy),
			getDecoyCount(genes, decoy),
			getDecoyCount(transcripts, decoy),
			getDecoyCount(proteins, decoy),
			getDecoyCount(peptides, decoy),
			getDecoyCount(psms, decoy),
			spectra);
	}
	
	private String toString( int groups, int genes, int transcripts, int proteins, int peptides, int psms, int spectra ) {
		StringBuilder sb = new StringBuilder();
		if( groups != 0 )
			sb.append(String.format("%d %s groups, ", groups, ambiguityLevel.getName()));
		if( genes != 0 )
			sb.append(String.format("%d genes, ", genes));
		if( transcripts != 0 )
			sb.append(String.format("%d transcripts, ", transcripts));
		sb.append(String.format("%d proteins, %d peptides, %d psms, %d spectra", proteins, peptides, psms, spectra));
		return sb.toString();
	}
	
	private void setParam(List<AbstractParamType> list, AbstractParamType param) {
		AbstractParamType remove = null;
		for( AbstractParamType item : list )
			if( item.getName().equals(param.getName()) ) {
				remove = item;
				break;
			}
		if( remove != null )
			list.remove(remove);
		list.add(param);
	}
	
	/**
	 * Merge MS/MS data from searches using the same DB (ej. fractions). After the
	 * merging, data2 will be broken and should not be used. Groups will be cleared
	 * and should be re-built next if desired.
	 * 
	 * @param data2
	 */
	public void merge( MsMsData data2 ) {
		clearMetaData();
		groups.clear();
		mergeSpectra(data2.getSpectra());
		psms.addAll(data2.getPsms());
		mergePeptides(data2.getPeptides());
		mergeProteins(data2.getProteins());
		data2.clear();
		mergeTitle(data2);		
	}
	
	private void mergeTitle(MsMsData data2) {
		if( getTitle() == null && data2.getTitle() == null )
			return;
		if( getTitle() == null )
			setTitle(data2.getTitle());
		else if( data2.getTitle() != null ) {
			StringBuilder title = new StringBuilder();
			int len = Math.min(getTitle().length(), data2.getTitle().length());
			for( int i = 0; i < len; i++ )
				if( getTitle().charAt(i) == data2.getTitle().charAt(i) )
					title.append(getTitle().charAt(i));
				else
					break;
			setTitle(title.toString().replaceAll("[^a-zA-Z0-9]+$", ""));
		}
	}

	public void mergeFromPeptide( MsMsData data2 ) {
		clearMetaData();
		groups.clear();
		spectra.addAll(data2.getSpectra());
		psms.addAll(data2.getPsms());
		mergePeptides(data2.getPeptides());
		mergeProteins(data2.getProteins());
		data2.clear();
		mergeTitle(data2);
	}
	
	public void updateRanks( final ScoreType type ) {
		for( Spectrum spectrum : spectra ) {
			List<Psm> list = new ArrayList<>(spectrum.getPsms());
			Collections.sort(list,new Comparator<Psm>() {
				@Override
				public int compare(Psm o1, Psm o2) {
					return o2.getScoreByType(type).compare(o1.getScoreByType(type).getValue());
				}
			});
			int rank = 1;
			Double lastScore = null;
			double newScore;
			for( Psm psm : list ) {
				newScore = psm.getScoreByType(type).getValue();
				if( lastScore != null && newScore != lastScore )
					rank++;
				lastScore = newScore;
				psm.setRank(rank);
			}
		}
	}
	
	private void mergeSpectra(Set<Spectrum> spectra2) {
		Map<String,Spectrum> map = new HashMap<>();
		for( Spectrum spectrum : spectra )
			map.put(spectrum.getUniqueString(), spectrum);
		for( Spectrum spectrum2 : spectra2 ) {
			Spectrum spectrum = map.get(spectrum2.getUniqueString());
			if( spectrum != null )				
				for( Psm psm2 : spectrum2.getPsms() )
					psm2.linkSpectrum(spectrum);
			else
				spectra.add(spectrum2);
		}
	}
	
	private void mergePeptides(Set<Peptide> peptides2) {
		Map<String,Peptide> map = new HashMap<>();
		for( Peptide peptide : peptides ) {
			map.put(peptide.getUniqueString(), peptide);
			peptide.clearScores();
		}
		for( Peptide peptide2 : peptides2 ) {
			Peptide peptide = map.get(peptide2.getUniqueString());
			if( peptide != null ) {
				for( Psm psm2 : peptide2.getPsms() )
					psm2.linkPeptide(peptide);
				for( Protein protein2 : peptide2.getProteins() ) {
					protein2.getPeptides().remove(peptide2);
					protein2.linkPeptide(peptide);
				}
			} else {
				peptides.add(peptide2);
				peptide2.clearScores();
			}
		}
	}
	
	private void mergeProteins(Set<Protein> proteins2) {
		Map<String,Protein> map = new HashMap<>();
		for( Protein protein : proteins ) {
			map.put(protein.getUniqueString(), protein);
			protein.clearScores();
		}
		for( Protein protein2 : proteins2 ) {
			Protein protein = map.get(protein2.getUniqueString());
			if( protein != null )
				for( Peptide peptide2 : protein2.getPeptides() ) {
					peptide2.getProteins().remove(protein2);
					protein.linkPeptide(peptide2);
				}
			else {
				proteins.add(protein2);
				protein2.clearScores();
			}
		}
	}
		
	public void checkIntegrity() throws AssertionError {
		for( Peptide peptide : getPeptides() ) {
			if( peptide.getProteins().size() == 0 )
				throw new AssertionError(String.format("Peptide %s not mapped no any protein", peptide.getSequence()));
			for( Protein protein : peptide.getProteins() )
				if( !protein.getPeptides().contains(peptide) )
					throw new AssertionError(String.format("Peptide %s not present in protein %s", peptide.getSequence(), protein.getAccession()));
		}
	}
	
	public MsMsData updateProteinInformation( String fastaPath ) throws IOException, InvalidSequenceException {
		if( fastaPath == null )
			return this;
		List<Fasta> list = Fasta.readEntries(fastaPath, SequenceType.PROTEIN);
		Map<String,Fasta> map = new HashMap<>();
		for( Fasta fasta : list )
			map.put(fasta.getAccession(), fasta);
		for( Protein protein : getProteins() )
			protein.setFasta(map.get(protein.getAccession()));
		return this;
	}
	
	public void mergeDuplicatedPeptides() {
		Map<String, Peptide> map = new HashMap<>();
		
		for( Peptide peptide : peptides ) {
			Peptide prev = map.get(peptide.getUniqueString());
			if( prev == null ) {
				map.put(peptide.getUniqueString(), peptide);
				continue;
			}
			for( Psm psm : peptide.getPsms() )
				psm.linkPeptide(prev);
			for( Protein protein : peptide.getProteins() ) {
				protein.getPeptides().remove(peptide);
				protein.linkPeptide(prev);
			}
		}
		
		peptides.clear();
		peptides.addAll(map.values());
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public GroupingLevel getAmbiguityLevel() {
		return ambiguityLevel;
	}

	public void setAmbiguityLevel(GroupingLevel ambiguityLevel) {
		for( AmbiguityItem item : getAmbiguityItems() )
			item.linkGroup(null);
		groups.clear();
		this.ambiguityLevel = ambiguityLevel;
	}

	public Set<Transcript> getTranscripts() {
		return transcripts;
	}

	public Set<Gene> getGenes() {
		return genes;
	}
	
	public Set<? extends AmbiguityItem> getAmbiguityItems() {
		switch( ambiguityLevel ) {
			case GENE:
				return genes;
			case PROTEIN:
				return proteins;
			case TRANSCRIPT:
				return transcripts;		
		}
		return null;
	}
	
	public void updateAmbiguities( Score pepThreshold ) {
		groups.clear();
		for( AmbiguityItem item : getAmbiguityItems() )
			item.getAmbiguityParts().clear();
		for( AmbiguityPart part : getAmbiguityParts() )
			part.getAmbiguityItems().clear();
		switch( ambiguityLevel ) {				
			case PROTEIN:
				for( Peptide peptide : peptides )
					if( usePeptide(peptide, pepThreshold) )
						for( Protein protein : peptide.getProteins() )
							protein.linkAmbiguityPart(peptide);
				break;
			case TRANSCRIPT:
				for( Transcript transcript : transcripts )
					updateAmbiguities(transcript.getProteins(), transcript, pepThreshold);
				break;
			case GENE:
				for( Gene gene : getGenes() )
					for( Transcript transcript : gene.getTranscripts() )
						updateAmbiguities(transcript.getProteins(), gene, pepThreshold);
				break;
		}
	}
	
	private boolean usePeptide(Peptide peptide, Score pepThreshold) {
		/*if( peptide.getSequence().equalsIgnoreCase("TNEVIFKK") )
			System.out.println();*/
		if( pepThreshold == null )
			return true;
		Score score = peptide.getScoreByType(pepThreshold.getType());
		if( score == null )
			return false;
		return score.compare(pepThreshold.getValue()) > 0;
	}
	
	private void updateAmbiguities(Set<Protein> proteins, AmbiguityItem item, Score pepThreshold) {
		for( Protein protein : proteins )
			for( Peptide peptide : protein.getPeptides() )
				if( usePeptide(peptide, pepThreshold) )
					item.linkAmbiguityPart(peptide);
	}

	private Set<Spectrum> spectra = new HashSet<>();
	private Set<Psm> psms = new HashSet<>();
	private Set<Peptide> peptides = new HashSet<>();
	private Set<Protein> proteins = new HashSet<>();
	private Set<Transcript> transcripts = new HashSet<>();
	private Set<Gene> genes = new HashSet<>();
	private Set<AmbiguityGroup> groups = new HashSet<>();
	private GroupingLevel ambiguityLevel = GroupingLevel.PROTEIN;
	
	private String title;
	private OrganizationType organization;
	private PersonType author;
	private AnalysisSoftwareType software;
	private BibliographicReferenceType publication;
	private ParamListType analysisParams;
	private ParamListType thresholds;
}