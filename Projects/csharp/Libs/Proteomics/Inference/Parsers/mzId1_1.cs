// $Id: Mapper.cs 22 2012-03-06 19:50:52Z gorka.prieto@gmail.com $
// 
// Mapper.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      Mapper.cs
//  
// Copyright (c) 2011 Gorka Prieto
// 
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

using System;
using System.IO;
using System.Collections.Generic;
using System.Xml;
using EhuBio.Proteomics.Hupo.mzIdentML;
using EhuBio.Proteomics.Hupo.mzIdentML1_1;

namespace EhuBio.Proteomics.Inference {

/// <summary>
/// Infers proteins from peptide identifications
/// </summary>
public class mzId1_1 : Mapper {
	/// <summary>
	/// Constructor
	/// </summary>
	public mzId1_1(Mapper.Software sw) : base(sw) {
		m_Type = Mapper.SourceType.mzIdentML110;
	}
	
	/// <summary>
	/// Gets the name of the parser.
	/// </summary>
	/// <value>
	/// The name of the parser.
	/// </value>
	public override string ParserName {
		get {
			return "PSI-PI mzIdentML (v1.1.0)";
		}
	}
	
	protected override void Load( string mzid ) {
		m_mzid = new mzidFile1_1();
		m_mzid.Load( mzid );		
		LoadSeqScores();
		CheckOtherScores();
		SortedList<string,string> SortedAccession = LoadProteins();
		SortedList<string,Peptide> SortedPeptides = LoadPeptides();
		LoadRelations(SortedAccession, SortedPeptides);
	}
	
	private void LoadSeqScores() {
		m_GreenTh = new double[4];
		m_YellowTh = new double[4];
		ParamListType ParamList = m_mzid.Data.AnalysisProtocolCollection.SpectrumIdentificationProtocol[0].AdditionalSearchParams;
		if( ParamList == null )
			return;
		int ProteomeDiscovererSequestXcorr = 0;		
		foreach( AbstractParamType param in ParamList.Items ) {
			if( !(param is CVParamType) )
				continue;
			CVParamType cv = param as CVParamType;
			switch( cv.accession ) {
				case "MS:1001712":	// ProteomeDiscoverer:SEQUEST:FT High Confidence XCorr Charge1
					m_GreenTh[0] = double.Parse(cv.value, m_Format);
					ProteomeDiscovererSequestXcorr++;
					break;
				case "MS:1001713":	// ProteomeDiscoverer:SEQUEST:FT High Confidence XCorr Charge2
					m_GreenTh[1] = double.Parse(cv.value, m_Format);
					ProteomeDiscovererSequestXcorr++;
					break;
				case "MS:1001714":	// ProteomeDiscoverer:SEQUEST:FT High Confidence XCorr Charge3
					m_GreenTh[2] = double.Parse(cv.value, m_Format);
					ProteomeDiscovererSequestXcorr++;
					break;
				case "MS:1001715":	// ProteomeDiscoverer:SEQUEST:FT High Confidence XCorr Charge4
					m_GreenTh[3] = double.Parse(cv.value, m_Format);
					ProteomeDiscovererSequestXcorr++;
					break;
				case "MS:1001716":	// ProteomeDiscoverer:SEQUEST:FT Medium Confidence XCorr Charge1
					m_YellowTh[0] = double.Parse(cv.value, m_Format);
					ProteomeDiscovererSequestXcorr++;
					break;
				case "MS:1001717":	// ProteomeDiscoverer:SEQUEST:FT Medium Confidence XCorr Charge2
					m_YellowTh[1] = double.Parse(cv.value, m_Format);
					ProteomeDiscovererSequestXcorr++;
					break;
				case "MS:1001718":	// ProteomeDiscoverer:SEQUEST:FT Medium Confidence XCorr Charge3
					m_YellowTh[2] = double.Parse(cv.value, m_Format);
					ProteomeDiscovererSequestXcorr++;
					break;
				case "MS:1001719":	// ProteomeDiscoverer:SEQUEST:FT Medium Confidence XCorr Charge4				
					m_YellowTh[3] = double.Parse(cv.value, m_Format);
					ProteomeDiscovererSequestXcorr++;
					break;
			}
		}
		if( ProteomeDiscovererSequestXcorr >= 8 ) {
			string yellow = "\t* Red-Yellow thresholds:";
			string green = "\t* Yellow-Green thresholds:";
			for( int i = 0; i < 4; i++ ) {
				green += " " + m_GreenTh[i] + "(" + (i+1) + ")";
				yellow += " " + m_YellowTh[i] + "(" + (i+1) + ")";
			}
			Notify( "Using ProteomeDiscoverer/SEQUEST XCorr values:");
			Notify( yellow );
			Notify( green );
			SeqThreshold = Peptide.ConfidenceType.Yellow;
		}
	}

	private void CheckOtherScores() {
		AbstractParamType[] items = m_mzid.Data.DataCollection.AnalysisData.SpectrumIdentificationList[0].SpectrumIdentificationResult[0].SpectrumIdentificationItem[0].Items;
		foreach( AbstractParamType param in items ) {
			if( !(param is CVParamType) )
				continue;
			CVParamType cv = param as CVParamType;
			switch( cv.accession ) {
				case "MS:1001330":	// X!Tandem:expect
					XTandemAvailable = true;
					break;
				case "MS:1001172":	// mascot:expectation value
					MascotAvailable = true;
					break;
			}
		}
	}
	
	private SortedList<string,string> LoadProteins() {
		SortedList<string,string> SortedAccession = new SortedList<string, string>();
		foreach( DBSequenceType prot in m_mzid.ListProteins ) {
			if( SortedAccession.ContainsKey(prot.id) )	// Avoids duplicated entries in the same file
				continue;
			SortedAccession.Add( prot.id, prot.accession );
			if( m_SortedProteins.ContainsKey(prot.accession) ) // Avoids duplicated entries between different files
				continue;
			CVParamType cv = mzidFile1_1.FindCV("MS:1001352", prot.Items);
			string entry = cv == null ? "" : cv.value;
			cv = mzidFile1_1.FindCV("MS:1001088", prot.Items);
			string desc = cv == null ? "" : cv.value;
			Protein p = new Protein( m_pid++, entry, prot.accession, desc, prot.Seq );
			p.DBRef = prot.id;
			Proteins.Add( p );
			m_SortedProteins.Add( p.Accession, p );
		}
		return SortedAccession;
	}
	
	private SortedList<string,Peptide> LoadPeptides() {
		SortedList<string,Peptide> SortedPeptides = new SortedList<string, Peptide>();
		int id = 1;
		foreach( PeptideType pep in m_mzid.ListPeptides ) {			
			Peptide p = new Peptide( id++, pep.PeptideSequence );
			p.Confidence = Peptide.ConfidenceType.NoThreshold;
			SortedPeptides.Add( pep.id, p );
			p.Runs.Add( m_Run );
			if( pep.Modification != null )
				foreach( ModificationType mod in pep.Modification ) {
					PTM ptm = new PTM();
					ptm.Pos = mod.locationSpecified ? mod.location : -1;
					if( mod.residues != null )
						foreach( string residue in mod.residues )
							ptm.Residues += residue;
					foreach( CVParamType param in mod.cvParam )
						if( param.cvRef.Equals("UNIMOD") )
							ptm.Name = param.name;
					p.AddPTM( ptm );
				}
			p.DBRef = pep.id;
			Peptides.Add( p );
		}
		return SortedPeptides;
	}
	
	private void LoadRelations(
		SortedList<string,string> SortedAccession, SortedList<string,Peptide> SortedPeptides) {
		
		if( m_mzid.Data.DataCollection.AnalysisData.SpectrumIdentificationList.Length != 1 )
			throw new ApplicationException( "Multiple spectrum identification lists not supported" );

		SortedList<string,PeptideEvidenceType> SortedEvidences = new SortedList<string, PeptideEvidenceType>();
		foreach( PeptideEvidenceType evidence in m_mzid.Data.SequenceCollection.PeptideEvidence )
			SortedEvidences.Add( evidence.id, evidence );

		int SpectrumID = 1;
		int PsmID = 1;
		double score;
		string type;
		Peptide.ConfidenceType confidence;
		foreach( SpectrumIdentificationResultType idres in
			m_mzid.Data.DataCollection.AnalysisData.SpectrumIdentificationList[0].SpectrumIdentificationResult ) {
			Spectrum spectrum = new Spectrum();
			spectrum.ID = SpectrumID++;
			spectrum.File = idres.spectraData_ref;
			spectrum.SpectrumID = idres.spectrumID;
			spectrum.Psm = new List<PSM>();
			Spectra.Add(spectrum);
			foreach( SpectrumIdentificationItemType item in idres.SpectrumIdentificationItem ) {
				//Console.Out.WriteLine(item.id);
				GetPsmScore( item, out score, out type, out confidence );
				PSM psm = new PSM();
				psm.ID = PsmID++;
				psm.Charge = item.chargeState;
				psm.Mz = item.experimentalMassToCharge;
				psm.Rank = item.rank;
				psm.Score = score;
				psm.ScoreType = type;
				psm.Confidence = confidence;
				psm.passThreshold = item.passThreshold;
				psm.Spectrum = spectrum;
				spectrum.Psm.Add(psm);
				if( item.PeptideEvidenceRef == null )
					continue;
				foreach( PeptideEvidenceRefType evref in item.PeptideEvidenceRef ) {
					//Console.Out.WriteLine(evref.peptideEvidence_ref);
					PeptideEvidenceType evidence = SortedEvidences[evref.peptideEvidence_ref];
					Peptide pep = SortedPeptides[evidence.peptide_ref];
					if( pep.Sequence == null || pep.Sequence.Length == 0 ) { // ProCon 0.9.348 bug
						//Notify( "Skiped peptide with empty sequence: " + pep.DBRef );
						continue;
					}					
					pep.Decoy = evidence.isDecoy;
					psm.Peptide = pep;
					//pep.Psm.Add(psm);
					Protein prot = m_SortedProteins[SortedAccession[evidence.dBSequence_ref]];
					if( pep.Proteins.Contains(prot) )
						continue;
					prot.Peptides.Add( pep );
					pep.Proteins.Add( prot );
				}
			}
		}
	}
	
	/// <summary>
	/// Also saves as mzid.
	/// </summary>
	public override void Save( string fpath ) {
		base.Save( fpath );
		UpdateMzidThresholds();
		SaveMzid( Path.ChangeExtension(fpath,".mzid") );
	}
	
	private void UpdateMzidThresholds() {
		double score;
		string type;
		Peptide.ConfidenceType confidence;
		foreach( SpectrumIdentificationResultType idres in
			m_mzid.Data.DataCollection.AnalysisData.SpectrumIdentificationList[0].SpectrumIdentificationResult ) {
			foreach( SpectrumIdentificationItemType item in idres.SpectrumIdentificationItem ) {
				GetPsmScore( item, out score, out type, out confidence );
				item.passThreshold = CheckPsm(item.passThreshold,item.rank,confidence,score,type);
			}
		}
	}
	
	private void GetPsmScore(
		SpectrumIdentificationItemType item,
		out double score, out string type, out Peptide.ConfidenceType confidence ) {
		score = -1.0;
		type = "N/A";
		confidence = item.passThreshold ? Peptide.ConfidenceType.PassThreshold : Peptide.ConfidenceType.NoThreshold;
		if( item.Items == null )
			return;
		foreach( AbstractParamType param in item.Items ) {
			if( !(param is CVParamType) )
				continue;
			CVParamType cv = param as CVParamType;
			if( cv.accession == "MS:1001155" ) {				
				score = double.Parse(cv.value, m_Format);
				type = "ProteomeDiscoverer/SEQUEST Confidence XCorr";
				if( score >= m_GreenTh[item.chargeState-1] )
					confidence = Peptide.ConfidenceType.Green;
				else if( score >= m_YellowTh[item.chargeState-1] )
					confidence = Peptide.ConfidenceType.Yellow;
				else
					confidence = Peptide.ConfidenceType.Red;
				break;
			} else if( cv.accession == "MS:1001172" ) {
				score = double.Parse(cv.value, m_Format);
				type = "Mascot expectation value";
				break;
			} else if( cv.accession == "MS:1001330" ) {
				score = double.Parse( cv.value, m_Format );
				type = "X!Tandem expect";
				break;
			}
		}
	}
	
	/// <summary>
	/// Save results to a mzIdentML file
	/// </summary>
	public void SaveMzid( string fpath ) {
		if( m_mzid == null || m_InputFiles.Count > 1 )
			return;
		
		#region Organization
		OrganizationType org = new OrganizationType();
		org.id = "UPV/EHU";
		org.name = "University of the Basque Country";
		foreach( OrganizationType o in m_mzid.ListOrganizations )
			if( o.id == org.id ) {
				m_mzid.ListOrganizations.Remove( o );
				break;
			}
		CVParamType url = new CVParamType();
		url.accession = "MS:1000588";
		url.name = "contact URL";
		url.cvRef = "PSI-MS";
		url.value = "http://www.ehu.es";
		org.Item = url;
		m_mzid.ListOrganizations.Add( org );
		#endregion
		
		#region Software author
		PersonType person = new PersonType();
		person.id = "PAnalyzer_Author";
		person.firstName = "Gorka";
		person.lastName = "Prieto";
		CVParamType email = new CVParamType();
		email.accession = "MS:1000589";
		email.name = "contact email";
		email.cvRef = "PSI-MS";
		email.value = "gorka.prieto@ehu.es";
		//person.Items.Add(email);
		person.Item = email;
		AffiliationType aff = new AffiliationType();
		aff.organization_ref = org.id;		
		//person.Affiliation.Add(aff);
		person.Affiliation = new AffiliationType[]{aff};
		foreach( PersonType p in m_mzid.ListPeople )
			if( p.id == person.id ) {
				m_mzid.ListPeople.Remove( p );
				break;
			}
		m_mzid.ListPeople.Add( person );
		#endregion

		#region Analysis software
		AnalysisSoftwareType sw = new AnalysisSoftwareType();
		sw.id = m_Software.Name;
		sw.name = m_Software.ToString();
		sw.uri = m_Software.Url;
		sw.version = m_Software.Version;
		CVParamType swname = new CVParamType();
		swname.name = "PAnalyzer";
		swname.cvRef = "PSI-MS";
		swname.accession = "MS:1002076";		
		sw.SoftwareName = new ParamType();
		sw.SoftwareName.Item = swname;
		RoleType role = new RoleType();
		CVParamType contacttype = new CVParamType();
		contacttype.accession = "MS:1001271";
		contacttype.cvRef = "PSI-MS";
		contacttype.name = "researcher";
		role.cvParam = contacttype;		
		sw.ContactRole = new ContactRoleType();
		sw.ContactRole.contact_ref = person.id;
		sw.ContactRole.Role = role;
		sw.Customizations = m_Software.Customizations;
		AnalysisSoftwareType old = null;
		foreach( AnalysisSoftwareType s in m_mzid.ListSW )
			if( s.id == m_Software.Name ) {
				old = s;
				break;
			}
		if( old != null )
			m_mzid.ListSW.Remove(old);
		m_mzid.ListSW.Add( sw );
		#endregion
		
		#region Protein detection protocol
		if( m_mzid.Data.AnalysisCollection.ProteinDetection == null || m_mzid.Data.AnalysisProtocolCollection.ProteinDetectionProtocol == null )
			return;
		m_mzid.Data.AnalysisCollection.ProteinDetection.proteinDetectionList_ref = "PDL_PAnalyzer";
		m_mzid.Data.AnalysisCollection.ProteinDetection.proteinDetectionProtocol_ref = "PDP_PAnalyzer";
		m_mzid.Data.AnalysisProtocolCollection.ProteinDetectionProtocol.analysisSoftware_ref = sw.id;
		m_mzid.Data.AnalysisProtocolCollection.ProteinDetectionProtocol.id = "PDP_PAnalyzer";
		#endregion
		
		#region Protein detection list		
		m_mzid.Data.DataCollection.AnalysisData.ProteinDetectionList.id = "PDL_PAnalyzer";
		List<ProteinAmbiguityGroupType> groups = BuildProteinDetectionList();
		m_mzid.Data.DataCollection.AnalysisData.ProteinDetectionList.ProteinAmbiguityGroup = groups.ToArray();
		#endregion
		
		#region References		
		BibliographicReferenceType pa = new BibliographicReferenceType();
		pa.authors = "Gorka Prieto, Kerman Aloria, Nerea Osinalde, Asier Fullaondo, Jesus M. Arizmendi and Rune Matthiesen";
		pa.id = pa.doi = "10.1186/1471-2105-13-288";
		pa.issue = "288";
		pa.name = pa.title = "PAnalyzer: A software tool for protein inference in shotgun proteomics";
		pa.publication = "BMC Bioinformatics";
		pa.publisher = "BioMed Central Ltd.";
		pa.volume = "13";
		pa.year = 2012;
		List<BibliographicReferenceType> refs = new List<BibliographicReferenceType>();
		refs.Add( pa );
		if( m_mzid.Data.BibliographicReference != null )
			foreach( BibliographicReferenceType r in m_mzid.Data.BibliographicReference ) {
				if( r.doi != null && r.doi == pa.doi )
					continue;
				refs.Add( r );
			}
		m_mzid.Data.BibliographicReference = refs.ToArray();
		#endregion
		
		m_mzid.Save( fpath );
		Notify( "Saved to " + fpath );
	}
	
	
	// TODO: Support empty PDH in input mzid
	protected virtual List<ProteinAmbiguityGroupType> BuildProteinDetectionList() {
		int gid = 1;
		SortedList<string,ProteinDetectionHypothesisType> list = new SortedList<string, ProteinDetectionHypothesisType>();
		List<ProteinAmbiguityGroupType> groups = new List<ProteinAmbiguityGroupType>();
		
		foreach( ProteinAmbiguityGroupType grp in m_mzid.Data.DataCollection.AnalysisData.ProteinDetectionList.ProteinAmbiguityGroup )
			foreach( ProteinDetectionHypothesisType pdh in grp.ProteinDetectionHypothesis )
				list.Add( pdh.dBSequence_ref, pdh );
		
		foreach( Protein p in Proteins ) {
			ProteinAmbiguityGroupType g = new ProteinAmbiguityGroupType();
			CVParamType ev = new CVParamType();
			ev.accession = "MS:1001600";
			ev.cvRef = "PSI-MS";
			ev.name = "Protein Inference Confidence Category";
			switch( p.Evidence ) {
				case Protein.EvidenceType.Conclusive:
					ev.value = "conclusive"; break;
				case Protein.EvidenceType.Indistinguishable:
					ev.value = "indistinguishable"; break;
				case Protein.EvidenceType.Group:
					ev.value = "ambiguous group"; break;
				case Protein.EvidenceType.NonConclusive:
					ev.value = "non conclusive"; break;
				default:
					continue;
			}
			g.id = "PAG_" + gid; gid++;
			if( p.Subset.Count == 0 ) {
				//g.ProteinDetectionHypothesis.Add(list[p.DBRef]);
				g.ProteinDetectionHypothesis = new ProteinDetectionHypothesisType[]{list[p.DBRef]};
				g.Items = new CVParamType[]{ev};
			} else {
				List<ProteinDetectionHypothesisType> listpdh = new List<ProteinDetectionHypothesisType>();
				foreach( Protein p2 in p.Subset ) {
					ProteinDetectionHypothesisType pdh = list[p2.DBRef];
					pdh.Items = new CVParamType[]{ev};
					listpdh.Add( pdh );
				}
				g.ProteinDetectionHypothesis = listpdh.ToArray();
			}
			groups.Add( g );
		}
		
		return groups;
	}
	
	private int m_pid = 0;
	private double[] m_GreenTh;
	private double[] m_YellowTh;
	protected mzidFile1_1 m_mzid;
}

} // namespace EhuBio.Proteomics.Inference