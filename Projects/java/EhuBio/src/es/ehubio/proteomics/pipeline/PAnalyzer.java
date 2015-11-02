package es.ehubio.proteomics.pipeline;

import java.util.HashSet;
import java.util.Set;

import es.ehubio.proteomics.AmbiguityGroup;
import es.ehubio.proteomics.AmbiguityItem;
import es.ehubio.proteomics.AmbiguityPart;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.psi.mzid11.AffiliationType;
import es.ehubio.proteomics.psi.mzid11.AnalysisSoftwareType;
import es.ehubio.proteomics.psi.mzid11.BibliographicReferenceType;
import es.ehubio.proteomics.psi.mzid11.CVParamType;
import es.ehubio.proteomics.psi.mzid11.ContactRoleType;
import es.ehubio.proteomics.psi.mzid11.OrganizationType;
import es.ehubio.proteomics.psi.mzid11.ParamType;
import es.ehubio.proteomics.psi.mzid11.PersonType;
import es.ehubio.proteomics.psi.mzid11.RoleType;

/**
 * Class for managing inference ambiguities in MS/MS proteomics.
 * 
 * @author gorka
 *
 */
public class PAnalyzer {
	public static class Counts {								
		public Counts(
			MsMsData.GroupingLevel level,
			int conclusive, int indistinguishable, int indistinguishableGroups, int ambiguous, int ambiguousGroups, int nonConclusive, int nonClassified,
			int unique, int discriminating, int nonDiscriminating, int notUsed, int psms ) {
			this.level = level;
			this.conclusive = conclusive;			
			this.ambiguous = ambiguous;
			this.ambiguousGroups = ambiguousGroups;
			this.indistinguishable = indistinguishable;
			this.indistinguishableGroups = indistinguishableGroups;
			this.nonConclusive = nonConclusive;
			this.nonClassified = nonClassified;
			maximum = conclusive+nonConclusive+nonClassified+indistinguishable+ambiguous;
			minimum = conclusive+indistinguishableGroups+ambiguousGroups;
			groups = minimum+nonConclusive+nonClassified;
			str = String.format("%s count=%s (%s) -> %s conclusive, %s indistinguishable groups (%s), %s ambiguous groups (%s), non-conclusive (%s), non-classified (%s)",
				level.getName(), minimum, maximum, conclusive, indistinguishableGroups, indistinguishable, ambiguousGroups, ambiguous, nonConclusive, nonClassified);
			this.unique = unique;
			this.discriminating = discriminating;
			this.nonDiscriminating = nonDiscriminating;
			this.notUsed = notUsed;
			this.parts = unique+discriminating+nonDiscriminating+notUsed;
			this.psms = psms;
		}
		public int getConclusive() {
			return conclusive;
		}
		public int getNonConclusive() {
			return nonConclusive;
		}
		public int getNonClassified() {
			return nonClassified;
		}
		public int getAmbiguous() {
			return ambiguous;
		}
		public int getAmbiguousGroups() {
			return ambiguousGroups;
		}
		public int getIndistinguishable() {
			return indistinguishable;
		}
		public int getIndistinguishableGroups() {
			return indistinguishableGroups;
		}
		public int getMaximum() {
			return maximum;
		}
		public int getMinimum() {
			return minimum;
		}
		public int getGroups() {
			return groups;
		}
		public int getUnique() {
			return unique;
		}
		public int getDiscriminating() {
			return discriminating;
		}
		public int getNonDiscriminating() {
			return nonDiscriminating;
		}
		public int getNotUsed() {
			return notUsed;
		}
		public int getAmbiguityParts() {
			return parts;
		}
		public int getPsms() {
			return psms;
		}
		@Override
		public String toString() {
			return str;
		}
		@Override
		public boolean equals(Object obj) {
			if( obj == null || !Counts.class.isInstance(obj) )
				return false;
			Counts other = (Counts)obj;
			return conclusive == other.conclusive && nonConclusive == other.nonConclusive
				&& ambiguous == other.ambiguous && ambiguousGroups == other.ambiguousGroups
				&& indistinguishable == other.indistinguishable && indistinguishableGroups == other.indistinguishableGroups;
		}
		@Override
		public int hashCode() {
			return minimum;
		}
		public MsMsData.GroupingLevel getLevel() {
			return level;
		}		
		private final MsMsData.GroupingLevel level;
		private final int conclusive;
		private final int nonConclusive;
		private final int nonClassified;
		private final int ambiguous;
		private final int ambiguousGroups;
		private final int indistinguishable;
		private final int indistinguishableGroups;
		private final int maximum;
		private final int minimum;
		private final int groups;
		private final int unique;
		private final int discriminating;
		private final int nonDiscriminating;
		private final int notUsed;
		private final int parts;
		private final int psms;
		private final String str;
	}	
	
	public PAnalyzer( MsMsData data ) {
		this.data = data;
	}
	
	/**
	 * Executes PAnalyzer algorithm.
	 * @see <a href="http://www.biomedcentral.com/1471-2105/13/288">original paper</a>
	 */
	public void run() {
		Score pepThreshold = new Score(ScoreType.PEPTIDE_Q_VALUE, 0.01);
		run(pepThreshold);
	}
			
	public void run(Score pepThreshold) {
		//data.getGroups().clear();
		data.updateAmbiguities(pepThreshold);
		resetConfidences();
		classifyAmbiguityParts();
		classifyItems();
		updateMetadata();
	}
	
	private Counts getCounts(Boolean target) {
		int conclusive = 0;
		int indistinguishable = 0;
		int indistinguishableGroups = 0;
		int ambiguous = 0;
		int ambiguousGroups = 0;
		int nonConclusive = 0;
		int nonClassified = 0;
		for( AmbiguityGroup group : data.getGroups() )
			if( addToCount(target, group) )
				if( group.getConfidence() == null )
					nonClassified++;
				else switch( group.getConfidence() ) {
					case CONCLUSIVE: conclusive++; break;
					case NON_CONCLUSIVE: nonConclusive++; break;					
					case INDISTINGUISABLE_GROUP: indistinguishableGroups++; indistinguishable += group.size(); break;
					case AMBIGUOUS_GROUP: ambiguousGroups++; ambiguous += group.size(); break;
				}
		int unique = 0;
		int discriminating = 0;
		int nonDiscriminating = 0;
		int notUsed = 0;
		for( AmbiguityPart part : data.getAmbiguityParts() )
			if( addToCount(target, part) )
				if( part.getConfidence() == null )
					notUsed++;
				else switch( part.getConfidence() ) {
					case UNIQUE: unique++; break;
					case DISCRIMINATING: discriminating++; break;
					case NON_DISCRIMINATING: nonDiscriminating++; break;
				}
		int psms = 0;
		for( Psm psm : data.getPsms() )
			if( addToCount(target, psm) )
				psms++;
		return new Counts(data.getAmbiguityLevel(), conclusive, indistinguishable, indistinguishableGroups, ambiguous, ambiguousGroups, nonConclusive, nonClassified,
			unique, discriminating, nonDiscriminating, notUsed, psms);
	}
	
	private boolean addToCount( Boolean target, Decoyable item ) {
		return target == null ||
			(target.equals(Boolean.FALSE) && Boolean.TRUE.equals((item.getDecoy()))) ||
			(target.equals(Boolean.TRUE) && !Boolean.TRUE.equals((item.getDecoy())));
	}

	public Counts getCounts() {
		return getCounts(null);
	}
	
	public Counts getTargetCounts() {
		return getCounts(Boolean.TRUE);
	}
	
	public Counts getDecoyCounts() {
		return getCounts(Boolean.FALSE);
	}
	
	private void resetConfidences() {
		for( AmbiguityPart part : data.getAmbiguityParts() )
			part.setConfidence(null);
		for( AmbiguityItem item : data.getAmbiguityItems() )
			item.setConfidence(null);
	}

	private void classifyAmbiguityParts() {
		// 1. Locate unique parts
		for( AmbiguityPart part : data.getAmbiguityParts() ) {
			if( part.getAmbiguityItems().isEmpty() )
				part.setConfidence(null);
			else if( part.getAmbiguityItems().size() == 1 ) {
				part.setConfidence(AmbiguityPart.Confidence.UNIQUE);
				part.getAmbiguityItems().iterator().next().setConfidence(AmbiguityItem.Confidence.CONCLUSIVE);
			} else
				part.setConfidence(AmbiguityPart.Confidence.DISCRIMINATING);
		}
		
		// 2. Locate non-discriminating parts (first round)
		for( AmbiguityItem item : data.getAmbiguityItems() )
			if( item.getConfidence() == AmbiguityItem.Confidence.CONCLUSIVE )
				for( AmbiguityPart part : item.getAmbiguityParts() )
					if( part.getConfidence() != AmbiguityPart.Confidence.UNIQUE )
						part.setConfidence(AmbiguityPart.Confidence.NON_DISCRIMINATING);
		
		// 3. Locate non-discriminating parts (second round)
		for( AmbiguityPart part : data.getAmbiguityParts() ) {
			if( part.getConfidence() != AmbiguityPart.Confidence.DISCRIMINATING )
				continue;			
			if( part.getAmbiguityItems().isEmpty() )
				System.out.println(part.getUniqueString());
			for( AmbiguityPart part2 : part.getAmbiguityItems().iterator().next().getAmbiguityParts() ) {
				if( part2.getConfidence() != AmbiguityPart.Confidence.DISCRIMINATING )
					continue;
				if( part2.getAmbiguityItems().size() <= part.getAmbiguityItems().size() )
					continue;
				boolean shared = true;
				for( AmbiguityItem item : part.getAmbiguityItems() )
					if( !item.getAmbiguityParts().contains(part2) ) {
						shared = false;
						break;
					}
				if( shared )
					part2.setConfidence(AmbiguityPart.Confidence.NON_DISCRIMINATING);
			}
		}
	}
	
	private void classifyItems() {
		// 1. Locate non-conclusive items
		for( AmbiguityItem item : data.getAmbiguityItems() ) {
			item.linkGroup(null);
			if( item.getConfidence() == AmbiguityItem.Confidence.CONCLUSIVE )
				continue;
			if( item.getAmbiguityParts().isEmpty() ) {
				item.setConfidence(null);
				continue;
			}
			item.setConfidence(AmbiguityItem.Confidence.NON_CONCLUSIVE);
			for( AmbiguityPart part : item.getAmbiguityParts() )
				if( part.getConfidence() == AmbiguityPart.Confidence.DISCRIMINATING ) {
					item.setConfidence(AmbiguityItem.Confidence.AMBIGUOUS_GROUP);
					break;
				}			
		}
		
		// 2. Group items
		data.getGroups().clear();
		for( AmbiguityItem item : data.getAmbiguityItems() ) {
			if( item.getGroup() != null )
				continue;
			AmbiguityGroup group = new AmbiguityGroup();
			data.getGroups().add(group);
			buildGroup(group, item);
		}
		
		// 3. Indistinguishable
		for( AmbiguityGroup group : data.getGroups() )
			if( group.size() >= 2 )
				if( isIndistinguishable(group) )
					for( AmbiguityItem item : group.getItems() )
						item.setConfidence(AmbiguityItem.Confidence.INDISTINGUISABLE_GROUP);
	}
	
	private void buildGroup( AmbiguityGroup group, AmbiguityItem item ) {
		if( group.getItems().contains(item) )
			return;
		group.linkItem(item);
		for( AmbiguityPart part : item.getAmbiguityParts() ) {
			if( part.getConfidence() != AmbiguityPart.Confidence.DISCRIMINATING )
				continue;
			for( AmbiguityItem item2 : part.getAmbiguityItems() )
				buildGroup(group, item2);
		}
	}
	
	private boolean isIndistinguishable( AmbiguityGroup group ) {
		boolean indistinguishable = true;
		Set<AmbiguityPart> discrimitating = new HashSet<>();
		for( AmbiguityItem item : group.getItems() )
			for( AmbiguityPart part : item.getAmbiguityParts() )
				if( part.getConfidence() == AmbiguityPart.Confidence.DISCRIMINATING )
					discrimitating.add(part);			
		for( AmbiguityItem item : group.getItems() )
			if( !item.getAmbiguityParts().containsAll(discrimitating) ) {
				indistinguishable = false;
				break;
			}
		discrimitating.clear();
		return indistinguishable;
	}

	public static String getVersion() {
		return VERSION;
	}

	public static String getName() {
		return NAME;
	}
	
	public static String getFullName() {
		return String.format("%s (v%s)", getName(), getVersion());
	}

	public static String getUrl() {
		return URL;
	}

	public static String getCustomizations() {
		return CUSTOMIZATIONS;
	}
	
	private void updateMetadata() {
		// Organization
		OrganizationType organization = new OrganizationType();
		organization = new OrganizationType();
		organization.setId("UPV/EHU");
		organization.setName("University of the Basque Country (UPV/EHU)");
		data.setOrganization(organization);
		
		// Author
		PersonType author = new PersonType();
		author = new PersonType();
		author.setId("PAnalyzer_Author");
		author.setFirstName("Gorka");
		author.setLastName("Prieto");
		CVParamType email = new CVParamType();
		email.setAccession("MS:1000589");
		email.setName("contact email");
		email.setCvRef("PSI-MS");
		email.setValue("gorka.prieto@ehu.es");
		author.getCvParamsAndUserParams().add(email);
		AffiliationType affiliation = new AffiliationType();
		affiliation.setOrganizationRef(organization.getId());
		author.getAffiliations().add(affiliation);
		data.setAuthor(author);
		
		// Software
		AnalysisSoftwareType software = new AnalysisSoftwareType();
		software.setId(getName());
		software.setName(getName());
		software.setVersion(getVersion());
		software.setUri(getUrl());
		software.setCustomizations(getCustomizations());
		CVParamType cv = new CVParamType();
		cv.setAccession("MS:1002076");
		cv.setName("PAnalyzer");
		cv.setCvRef("PSI-MS");
		ParamType param = new ParamType();
		param.setCvParam(cv);
		software.setSoftwareName(param);
		RoleType role = new RoleType();
		cv = new CVParamType();
		cv.setAccession("MS:1001271");
		cv.setName("researcher");
		cv.setCvRef("PSI-MS");
		role.setCvParam(cv);
		ContactRoleType contact = new ContactRoleType();
		contact.setContactRef(author.getId());
		contact.setRole(role);
		software.setContactRole(contact);
		data.setSoftware(software);
		
		// Reference
		BibliographicReferenceType paper= new BibliographicReferenceType();
		paper.setTitle("PAnalyzer: A software tool for protein inference in shotgun proteomics");
		paper.setName(paper.getTitle());
		paper.setAuthors("Gorka Prieto, Kerman Aloria, Nerea Osinalde, Asier Fullaondo, Jesus M. Arizmendi and Rune Matthiesen");
		paper.setDoi("10.1186/1471-2105-13-288");
		paper.setId(paper.getDoi());
		paper.setVolume("13");
		paper.setIssue("288");
		paper.setYear(2012);
		paper.setPublication("BMC Bioinformatics");
		paper.setPublisher("BioMed Central Ltd.");
		data.setPublication(paper);
	}
	
	private final MsMsData data;
	private static final String VERSION = "2.0a1";
	private static final String NAME = "PAnalyzer";
	private static final String URL = "https://code.google.com/p/ehu-bio/wiki/PAnalyzer";
	private static final String CUSTOMIZATIONS = "No customizations";
}