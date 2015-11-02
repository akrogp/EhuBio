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
using EhuBio.Proteomics.Hupo.mzIdentML1_0;
using EhuBio.Proteomics.Hupo.mzIdentML;

namespace EhuBio.Proteomics.Inference {

/// <summary>
/// Infers proteins from peptide identifications
/// </summary>
public class mzId1_0 : Mapper {
	/// <summary>
	/// Constructor
	/// </summary>
	public mzId1_0(Mapper.Software sw) : base(sw) {
		m_Type = Mapper.SourceType.mzIdentML100;
	}
	
	/// <summary>
	/// Gets the name of the parser.
	/// </summary>
	/// <value>
	/// The name of the parser.
	/// </value>
	public override string ParserName {
		get {
			return "PSI-PI mzIdentML (v1.0.0)";
		}
	}
			
	/// <summary>
	/// Loads a mzIdentML file
	/// </summary>
	override protected void Load( string mzid ) {
		m_mzid = new mzidFile1_0();
		m_mzid.Load( mzid );
		
		// Proteins
		SortedList<string,string> SortedAccession = new SortedList<string, string>();
		foreach( PSIPIanalysissearchDBSequenceType element in m_mzid.ListProteins ) {
			if( SortedAccession.ContainsKey(element.id) )
				continue;
			string acc = element.accession;
			SortedAccession.Add( element.id, acc );
			if( m_SortedProteins.ContainsKey(acc) )
				continue;
			FuGECommonOntologycvParamType cv;
			cv = FuGECommonOntologycvParamType.Find( "MS:1001352", element.cvParam );
			string entry = cv == null ? "" : cv.value;
			cv = FuGECommonOntologycvParamType.Find( "MS:1001088", element.cvParam );
			string desc = cv == null ? "" : cv.value;
			string seq = element.seq;//.ToUpper();
			Protein p = new Protein(m_pid++, entry, acc, desc, seq);
			p.DBRef = element.id;
			Proteins.Add( p );
			m_SortedProteins.Add( acc, p );
		}
		
		// Peptides
		SortedList<string,Peptide> SortedPeptides = new SortedList<string, Peptide>();
		int id = 1;
		foreach( PSIPIpolypeptidePeptideType element in m_mzid.ListPeptides ) {
			string seq = element.peptideSequence;//.ToUpper();
			Peptide f = new Peptide(id++, seq);
			f.Confidence = Peptide.ConfidenceType.PassThreshold; // It will be filtered later if neccessary
			SortedPeptides.Add( element.id, f );
			f.Runs.Add( m_Run );
			if( element.Modification != null )
				foreach( PSIPIpolypeptideModificationType mod in element.Modification ) {
					PTM ptm = new PTM();
					ptm.Pos = mod.locationSpecified ? mod.location : -1;
					if( mod.residues != null )
						foreach( string residue in mod.residues )
							ptm.Residues += residue;
					foreach( FuGECommonOntologycvParamType param in mod.cvParam )
						if( param.cvRef.Equals("UNIMOD") )
							ptm.Name = param.name;
					f.AddPTM( ptm );
				}
			Peptides.Add( f );
		}
		
		// Relations
		if( m_mzid.Data.DataCollection.AnalysisData.SpectrumIdentificationList.Length != 1 )
			throw new ApplicationException( "Multiple spectrum identification lists not supported" );
		foreach( PSIPIanalysissearchSpectrumIdentificationResultType idres in
			m_mzid.Data.DataCollection.AnalysisData.SpectrumIdentificationList[0].SpectrumIdentificationResult )
			foreach( PSIPIanalysissearchSpectrumIdentificationItemType item in idres.SpectrumIdentificationItem ) {
				if( !item.passThreshold )
					continue;
				Peptide f = SortedPeptides[item.Peptide_ref];
				if( item.PeptideEvidence == null )
					continue;
				f.Confidence = Peptide.ConfidenceType.PassThreshold;
				foreach( PSIPIanalysisprocessPeptideEvidenceType relation in item.PeptideEvidence ) {
					Protein p = m_SortedProteins[SortedAccession[relation.DBSequence_Ref]];
					if( f.Proteins.Contains(p) )
						continue;
					f.Names.Add( relation.DBSequence_Ref, relation.id );
					p.Peptides.Add( f );
					f.Proteins.Add( p );
				}
			}
	}
	
	/// <summary>
	/// Also saves as mzid.
	/// </summary>
	public override void Save( string fpath ) {
		base.Save( fpath );
		SaveMzid( Path.ChangeExtension(fpath,".mzid"), null, null, null, null );
	}

	/// <summary>
	/// Save results to a mzIdentML file
	/// </summary>
	public void SaveMzid(
		string mzid,
		string org_id, string org_name,
		string owner_name, string owner_email ) {
		// Previous file is required for including MS data
		if( m_mzid == null || m_InputFiles.Count > 1 )
			return;
        
        #region Organization
		FuGECommonAuditOrganizationType org = new FuGECommonAuditOrganizationType();
		org.id = "UPV/EHU";
		org.name = "University of the Basque Country";
		foreach( FuGECommonAuditOrganizationType o in m_mzid.ListOrganizations )
			if( o.id == org.id ) {
				m_mzid.ListOrganizations.Remove( o );
				break;
			}
		m_mzid.ListOrganizations.Add( org );
		#endregion
		
		#region Software author
		FuGECommonAuditPersonType person = new FuGECommonAuditPersonType();
		person.id = "PAnalyzer_Author";
		person.firstName = "Gorka";
		person.lastName = "Prieto";
		person.email = "gorka.prieto@ehu.es";
		FuGECommonAuditPersonTypeAffiliations aff = new FuGECommonAuditPersonTypeAffiliations();
		aff.Organization_ref = org.id;		
		person.affiliations = new FuGECommonAuditPersonTypeAffiliations[]{aff};
		foreach( FuGECommonAuditPersonType p in m_mzid.ListPeople )
			if( p.id == person.id ) {
				m_mzid.ListPeople.Remove( p );
				break;
			}
		m_mzid.ListPeople.Add( person );
		#endregion

		#region Analysis software
		PSIPIanalysissearchAnalysisSoftwareType sw = new PSIPIanalysissearchAnalysisSoftwareType();
		sw.id = m_Software.Name;
		sw.name = m_Software.ToString();
		sw.URI = m_Software.Url;
		sw.version = m_Software.Version;
		ParamType swname = new ParamType();
		FuGECommonOntologycvParamType item = new FuGECommonOntologycvParamType();
		item.name = "PAnalyzer";
		item.cvRef = "PSI-MS";
		item.accession = "MS:1002076";		
		swname.Item = item;
		sw.SoftwareName = swname;
		FuGECommonAuditContactRoleType contact = new FuGECommonAuditContactRoleType();
		contact.Contact_ref = person.id;
		FuGECommonAuditContactRoleTypeRole role = new FuGECommonAuditContactRoleTypeRole();
		FuGECommonOntologycvParamType contacttype = new FuGECommonOntologycvParamType();
		contacttype.accession = "MS:1001271";
		contacttype.cvRef = "PSI-MS";
		contacttype.name = "researcher";
		role.cvParam = contacttype;
		contact.role = role;
		sw.ContactRole = contact;
		sw.Customizations = m_Software.Customizations;
		foreach( PSIPIanalysissearchAnalysisSoftwareType s in m_mzid.ListSW )
			if( s.id == m_Software.Name ) {
				m_mzid.ListSW.Remove( sw );
				break;
			}
		m_mzid.ListSW.Add( sw );
		#endregion
		
		#region Protein detection protocol
		m_mzid.Data.AnalysisCollection.ProteinDetection.ProteinDetectionList_ref = "PDL_PAnalyzer";
		m_mzid.Data.AnalysisCollection.ProteinDetection.ProteinDetectionProtocol_ref = "PDP_PAnalyzer";
		m_mzid.Data.AnalysisProtocolCollection.ProteinDetectionProtocol.AnalysisSoftware_ref = sw.id;
		m_mzid.Data.AnalysisProtocolCollection.ProteinDetectionProtocol.id = "PDP_PAnalyzer";
		#endregion
        
        #region Protein detection list
        List<PSIPIanalysisprocessProteinAmbiguityGroupType> listGroup =
        	new List<PSIPIanalysisprocessProteinAmbiguityGroupType>();
        int hit = 1;
        foreach( Protein p in Proteins ) {
        	if( p.Evidence == Protein.EvidenceType.Filtered )
        		continue;
        	PSIPIanalysisprocessProteinAmbiguityGroupType grp = new PSIPIanalysisprocessProteinAmbiguityGroupType();
        	grp.id = "PAG_hit_" + (hit++);
        	int num = (p.Subset.Count == 0 ? 1 : p.Subset.Count);
        	grp.ProteinDetectionHypothesis = new PSIPIanalysisprocessProteinDetectionHypothesisType[num];
        	if( p.Subset.Count == 0 )
        		grp.ProteinDetectionHypothesis[0] = BuildHypothesis( p, p.Evidence );
        	else {
        		int i = 0;
        		foreach( Protein p2 in p.Subset )
        			grp.ProteinDetectionHypothesis[i++] = BuildHypothesis( p2, p.Evidence );
        	}        	
        	listGroup.Add( grp );
        }
        PSIPIanalysisprocessProteinDetectionListType analysis = new PSIPIanalysisprocessProteinDetectionListType();
        analysis.id = "PDL_PAnalyzer";
       	analysis.ProteinAmbiguityGroup = listGroup.ToArray();
        m_mzid.Data.DataCollection.AnalysisData.ProteinDetectionList = analysis;
        #endregion
        
        m_mzid.Save( mzid );        
        Notify( "Saved to " + mzid );
	}
	
	/// <summary>
	/// Builds a PDH for the current protein
	/// </summary>
	private PSIPIanalysisprocessProteinDetectionHypothesisType BuildHypothesis( Protein p, Protein.EvidenceType ev ) {
		PSIPIanalysisprocessProteinDetectionHypothesisType h = new PSIPIanalysisprocessProteinDetectionHypothesisType();
		h.id = "PDH_" + p.Accession;
		h.DBSequence_ref = p.DBRef;
		if( p.Evidence == Protein.EvidenceType.NonConclusive || p.Evidence == Protein.EvidenceType.Filtered )
			h.passThreshold = false;
		else
			h.passThreshold = true;
		List<PeptideHypothesisType> listPeptides = new List<PeptideHypothesisType>();
		foreach( Peptide f in p.Peptides ) {
			PeptideHypothesisType peptide = new PeptideHypothesisType();
			peptide.PeptideEvidence_Ref = f.Names[p.DBRef];
			listPeptides.Add( peptide );
		}
		if( listPeptides.Count > 0 )
			h.PeptideHypothesis = listPeptides.ToArray();
		h.cvParam = new FuGECommonOntologycvParamType[1];
        h.cvParam[0] = new FuGECommonOntologycvParamType(
        		"Protein Inference Confidence Category",
        		"MS:1001600", "PSI-MS" );
        h.cvParam[0].value = ParseConfidence( ev );
		return h;
	}
		
	private int m_pid;
	private mzidFile1_0 m_mzid;
}

} // namespace EhuBio.Proteomics.Inference