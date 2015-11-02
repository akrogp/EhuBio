// $Id: mzIdentML.cs 3 2011-03-10 19:32:55Z gorka.prieto@gmail.com $
// 
// mzIdentML.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      Class for reading and writing mzIdentML files
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
using System.Xml;
using System.Xml.Schema;
using System.Xml.Serialization;
using System.Collections.Generic;
using System.Reflection;
using EhuBio.Database.Ebi;
using EhuBio.Proteomics.Hupo.mzIdentML1_0;

namespace EhuBio.Proteomics.Hupo.mzIdentML {

/// <summary>
/// Class mzidFile1_0:
/// - Reads/writes C# objects to a mzIdentML file using deserialization/serialization
/// </summary>
public class mzidFile1_0 {
	/// <summary>
	/// Default constructor
	/// </summary>
	public mzidFile1_0() {
		Default();
	}
	
	private void Default() {
		Data = new PSIPIMainmzIdentMLType();
		ListOntology = new List<FuGECommonOntologycvType>();
		ListSW = new List<PSIPIanalysissearchAnalysisSoftwareType>();
		ListPeople = new List<FuGECommonAuditPersonType>();
		ListOrganizations = new List<FuGECommonAuditOrganizationType>();
		ListProteins = new List<PSIPIanalysissearchDBSequenceType>();
		ListPeptides = new List<PSIPIpolypeptidePeptideType>();
	}
	
	/// <summary>
	/// TODO: Validates the mzIdentML file using the embedded schemas
	/// </summary>
	public static void Validate( string xml ) {
		throw new NotImplementedException( "mzIdentML1.0.0 validation is not implemented" );
	}
	
	/// <summary>
	/// Writes current data to a mzid file
	/// </summary>
	/// <param name="xml">
	/// A <see cref="System.String"/> with the output file name
	/// </param>
	public void Save( string xml ) {
		Data.creationDate = DateTime.UtcNow;
	
		// CommonOntology
		Data.cvList = ListOntology.ToArray();
	
		// AnalysisSoftwareList
        Data.AnalysisSoftwareList = ListSW.ToArray();
        
        // Provider
        Data.Provider = Provider;
        
        // AuditCollection
        Data.AuditCollection = new FuGECollectionAuditCollectionType();
        Data.AuditCollection.Person = ListPeople.ToArray();
        Data.AuditCollection.Organization = ListOrganizations.ToArray();
        
        // SequenceCollection
        SequenceCollectionType seq = new SequenceCollectionType();
        seq.DBSequence = ListProteins.ToArray();
        seq.Peptide = ListPeptides.ToArray();
        Data.SequenceCollection = seq;
        
        // Serialization
        XmlSerializer serializer =  new XmlSerializer(typeof(PSIPIMainmzIdentMLType));
        TextWriter writer = new StreamWriter(xml);
        serializer.Serialize(writer, Data);
        writer.Close();
        
        System.GC.Collect();
	}
	
	/// <summary>
	/// Reads data from a mzid file
	/// </summary>
	/// <param name="xml">
	/// A <see cref="System.String"/> with the input file name
	/// </param>
	public void Load( string xml ) {
		Default();
		
		// Deserialization
		XmlSerializer serializer = new XmlSerializer(typeof(PSIPIMainmzIdentMLType));
		TextReader reader = new StreamReader(xml);
		Data = (PSIPIMainmzIdentMLType)serializer.Deserialize(reader);
		reader.Close();
		
		// Parse data
		if( Data.cvList != null )
			ListOntology = new List<FuGECommonOntologycvType>( Data.cvList );
		if( Data.AnalysisSoftwareList != null )
			ListSW = new List<PSIPIanalysissearchAnalysisSoftwareType>( Data.AnalysisSoftwareList );
		if( Data.Provider != null )
			Provider = Data.Provider;
		if( Data.AuditCollection != null && Data.AuditCollection.Person != null )
			ListPeople = new List<FuGECommonAuditPersonType>( Data.AuditCollection.Person );
		if( Data.AuditCollection != null && Data.AuditCollection.Organization != null )
			ListOrganizations = new List<FuGECommonAuditOrganizationType>( Data.AuditCollection.Organization );
		if( Data.SequenceCollection == null || Data.SequenceCollection.DBSequence == null
			|| Data.SequenceCollection.Peptide == null )
			throw new ApplicationException( "mzIdentML file without identification sequences" );
		ListProteins = new List<PSIPIanalysissearchDBSequenceType>( Data.SequenceCollection.DBSequence );
		ListPeptides = new List<PSIPIpolypeptidePeptideType>( Data.SequenceCollection.Peptide );
		
		System.GC.Collect();
	}
	
	/// <summary>
	/// Search sequence online in the corresponding DB
	/// </summary>
	public void RetrieveSequences() {
		WSDBFetchServerService db = new WSDBFetchServerService();
		foreach( PSIPIanalysissearchDBSequenceType p in ListProteins ) {
			if( p.seq != null )
				continue;
			if( p.SearchDatabase_ref.ToUpper() != "SDB_SWISSPROT" )
				throw new ApplicationException( "DB " + p.SearchDatabase_ref + " not supported" );
			Console.WriteLine( "Retrieving " + p.accession + " ..." );
			string fasta = db.fetchData("UNIPROT:"+p.accession, "fasta", "raw");
			fasta = fasta.Remove(0,fasta.IndexOf('\n'));
			string seq = "";
			foreach( string str in fasta.Split('\n') )
				seq += str;
			p.seq = seq;//seq.ToUpper();
		}
	}
	
	/// <summary>
	/// Serializable object with all the mzIdentML file data
	/// </summary>
	public PSIPIMainmzIdentMLType Data;
	
	/// <summary>
	/// Common ontology list
	/// </summary>
	public List<FuGECommonOntologycvType> ListOntology;
	
	/// <summary>
	/// Analysis software list
	/// </summary>
	public List<PSIPIanalysissearchAnalysisSoftwareType> ListSW;
	
	/// <summary>
	/// Analysis provider info
	/// </summary>
	public FuGECollectionProviderType Provider;
	
	/// <summary>
	/// Audit people list
	/// </summary>
	public List<FuGECommonAuditPersonType> ListPeople;
	
	/// <summary>
	/// Audit organizations list
	/// </summary>
	public List<FuGECommonAuditOrganizationType> ListOrganizations;
	
	/// <summary>
	/// Protein list
	/// </summary>
	public List<PSIPIanalysissearchDBSequenceType> ListProteins;
	
	/// <summary>
	/// Peptide list
	/// </summary>
	public List<PSIPIpolypeptidePeptideType> ListPeptides;
	
	/// <summary>
	/// Helper method for including additional ontologies to the list
	/// </summary>
	public void AddOntology(
		string id, string fullname, string version, string uri ) {
		FuGECommonOntologycvType cv = new FuGECommonOntologycvType( id, fullname, version, uri );
		ListOntology.Add( cv );
	}
	
	/// <summary>
	/// Helper method for including an additional analysis software to the list
	/// </summary>
	public void AddAnalysisSoftware(
		string id, string name, string version, string uri, string org_id,
		string contact_accession, string contact_name, string contact_cvRef,
		string customizations ) {
		
		PSIPIanalysissearchAnalysisSoftwareType sw = new PSIPIanalysissearchAnalysisSoftwareType(
			id, name, version, uri, org_id,
			contact_accession, contact_name, contact_cvRef,
			customizations );
		ListSW.Add( sw );
	}
	
	/// <summary>
	/// Helper method for setting the analysis provider
	/// </summary>
	public void SetProvider(
		string id, string contact_ref, string accession, string name, string cvRef ) {
		Provider = new FuGECollectionProviderType( id, contact_ref, accession, name, cvRef );
	}
	
	/// <summary>
	/// Helper method for including an additional person in the audit list
	/// </summary>
	public void AddPerson(
		string id, string name, string email, string org_ref ) {
		FuGECommonAuditPersonType person =
			new FuGECommonAuditPersonType( id, name, email, org_ref );
		ListPeople.Add( person );
	}
	
	/// <summary>
	/// Helper method for including an additional organization in the audit list
	/// </summary>
	public void AddOrganization(
		string id, string name, string address, string phone, string email ) {
		FuGECommonAuditOrganizationType org =
			new FuGECommonAuditOrganizationType( id, name, address, phone, email );
		ListOrganizations.Add( org );
	}
	
	/// <summary>
	/// Helper method for including an additional organization in the audit list
	/// </summary>
	public void AddOrganization( string id, string name ) {
		FuGECommonAuditOrganizationType org =
			new FuGECommonAuditOrganizationType( id, name );
		ListOrganizations.Add( org );
	}
	
	/// <summary>
	/// Adds another DB sequence to the collection
	/// </summary>
	public void AddProtein( string id, string db_ref, string accession, string seq ) {
		PSIPIanalysissearchDBSequenceType prot = new PSIPIanalysissearchDBSequenceType(
			id, seq.Length, db_ref, accession, seq );
		ListProteins.Add( prot );
	}
	
	/// <summary>
	/// Adds another peptide to the collection
	/// </summary>
	public void AddPeptide( string id, string seq ) {
		PSIPIpolypeptidePeptideType p = new PSIPIpolypeptidePeptideType( id, seq );
		ListPeptides.Add( p );
	}
	
	private static void XmlValidationHandler( object sender, ValidationEventArgs e ) {
		throw new ApplicationException( e.Message );
	}
}

}	// namespace EhuBio.Proteomics.Hupo.mzIdentML

#region Base nodes

namespace EhuBio.Proteomics.Hupo.mzIdentML1_0 {

/// <summary>
/// Common Ontology
/// </summary>
public partial class FuGECommonOntologycvType {
	/// <summary>
	/// Default constructor
	/// </summary>
	public FuGECommonOntologycvType() {
	}
	
	/// <summary>
	/// Simple constructor
	/// </summary>
	public FuGECommonOntologycvType(
		string id, string fullname, string version, string uri ) {
		
		this.id = id;
		this.fullName = fullname;
		this.version = version;
		this.URI = uri;
	}
}

/// <summary>
/// Analysis Software
/// </summary>
public partial class PSIPIanalysissearchAnalysisSoftwareType {
	/// <summary>
	/// Default constructor
	/// </summary>
	public PSIPIanalysissearchAnalysisSoftwareType() {
	}
	
	/// <summary>
	/// Simple constructor
	/// </summary>
	public PSIPIanalysissearchAnalysisSoftwareType(
		string id, string name, string version, string uri, string org_id,
		string contact_accession, string contact_name, string contact_cvRef,
		string customizations ) {
		
		// FuGECommonIdentifiableType
		this.id = id;
		this.name = name;
		
		// FuGECommonProtocolSoftwareType
		FuGECommonAuditContactRoleType contact_role = new FuGECommonAuditContactRoleType();
		FuGECommonAuditContactRoleTypeRole role = new FuGECommonAuditContactRoleTypeRole();
		FuGECommonOntologycvParamType cvParam =
			new FuGECommonOntologycvParamType(contact_name, contact_accession, contact_cvRef);
		role.cvParam = cvParam;
		contact_role.role = role;
		contact_role.Contact_ref = org_id;
		ContactRole = contact_role;
		this.version = version;
		
		// PSIPIanalysissearchAnalysisSoftwareType
		this.Customizations = customizations;
		this.URI = uri;
	}
}

/// <summary>
/// Analysis Provider
/// </summary>
public partial class FuGECollectionProviderType {
	/// <summary>
	/// Default constructor
	/// </summary>
	public FuGECollectionProviderType() {
	}

	/// <summary>
	/// Simple constructor
	/// </summary>
	public FuGECollectionProviderType(
		string id, string contact_ref, string accession, string name, string cvRef ) {
		
		// FuGECommonIdentifiableType
		this.id = id;
		//this.name = name;
		
		// FuGECommonAuditContactRoleType
		FuGECommonAuditContactRoleType contact_role = new FuGECommonAuditContactRoleType();
		FuGECommonAuditContactRoleTypeRole role = new FuGECommonAuditContactRoleTypeRole();
		FuGECommonOntologycvParamType cvParam = new FuGECommonOntologycvParamType(name, accession, cvRef);
		role.cvParam = cvParam;
		contact_role.role = role;
		contact_role.Contact_ref = contact_ref;
		ContactRole = contact_role;
		
		//Software_ref = ;
	}
}

/// <summary>
/// Audit Person
/// </summary>
public partial class FuGECommonAuditPersonType {
	/// <summary>
	/// Default constructor
	/// </summary>
	public FuGECommonAuditPersonType() {
	}

	/// <summary>
	/// Simple constructor
	/// </summary>
	public FuGECommonAuditPersonType(
		string id, string firstName, string lastName, string email, string org_ref ) {

		// FuGECommonIdentifiableType
		this.id = id;
		//this.name = "";
	
		// FuGECommonAuditContactType
		//this.address = "";
		//this.phone = "";
		this.email = email;
		//this.fax = "";
		//this.tollFreePhone = "";
	
		FuGECommonAuditPersonTypeAffiliations[] a = new FuGECommonAuditPersonTypeAffiliations[1];
		a[0] = new FuGECommonAuditPersonTypeAffiliations( org_ref );
		affiliations = a;
		this.lastName = lastName;
		this.firstName = firstName;
		//this.midInitials = "";
	}
	
	/// <summary>
	/// Simple constructor
	/// </summary>
	public FuGECommonAuditPersonType(
		string id, string name, string email, string org_ref ) {

		// FuGECommonIdentifiableType
		this.id = id;
		this.name = name;
	
		// FuGECommonAuditContactType
		//this.address = "";
		//this.phone = "";
		this.email = email;
		//this.fax = "";
		//this.tollFreePhone = "";
	
		FuGECommonAuditPersonTypeAffiliations[] a = new FuGECommonAuditPersonTypeAffiliations[1];
		a[0] = new FuGECommonAuditPersonTypeAffiliations( org_ref );
		affiliations = a;
		//this.lastName = lastName;
		//this.firstName = firstName;
		//this.midInitials = "";
	}
}

/// <summary>
/// Audit Organization
/// </summary>
public partial class FuGECommonAuditOrganizationType {
	/// <summary>
	/// Default constructor
	/// </summary>
	public FuGECommonAuditOrganizationType() {
	}

	/// <summary>
	/// Simple constructor
	/// </summary>
	public FuGECommonAuditOrganizationType (
		string id, string name, string address, string phone, string email ) {
	
		// FuGECommonIdentifiableType
		this.id = id;
		this.name = name;
	
		// FuGECommonAuditContactType
		this.address = address;
		this.phone = phone;
		this.email = email;
		//fax =;
		//tollFreePhone =;
	}
	
	/// <summary>
	/// Simple constructor
	/// </summary>
	public FuGECommonAuditOrganizationType ( string id, string name ) {
	
		// FuGECommonIdentifiableType
		this.id = id;
		this.name = name;
	
		// FuGECommonAuditContactType
		//this.address = address;
		//this.phone = phone;
		//this.email = email;
		//fax =;
		//tollFreePhone =;
	}
}

#endregion

#region Internal nodes

public partial class FuGECommonOntologyParamType {
	/// <summary>
	/// Default constructor
	/// </summary>
	public FuGECommonOntologyParamType() {
	}

	/// <summary>
	/// Simple constructor
	/// </summary>
	public FuGECommonOntologyParamType(
		string name, string value,
		string unitAccession, string unitName, string unitCvRef ) {
		this.name = name;
		this.value = value;
		this.unitAccession = unitAccession;
		this.unitName = unitName;
		this.unitCvRef = unitCvRef;
	}

	/// <summary>
	/// Simple constructor
	/// </summary>
	public FuGECommonOntologyParamType( string name ) {
		this.name = name;
	}
}

public partial class FuGECommonOntologycvParamType {
	/// <summary>
	/// Default constructor
	/// </summary>
	public FuGECommonOntologycvParamType() {
	}

	/// <summary>
	/// Simple constructor
	/// </summary>
	public FuGECommonOntologycvParamType(string name, string accession, string cvRef) : base(name) {
		this.cvRef = cvRef;
		this.accession = accession;
	}
	
	/// <summary>
	/// Finds a given cvParam from an array
	/// </summary>
	/// <param name="acc">
	/// A <see cref="System.String"/> with the accession of the cvParam to find
	/// </param>
	/// <param name="cvparams">
	/// A <see cref="FuGECommonOntologycvParamType[]"/> with all the cvParams
	/// </param>
	/// <returns>
	/// A <see cref="FuGECommonOntologycvParamType"/> with the desired cvParam or null if not found
	/// </returns>
	public static FuGECommonOntologycvParamType Find( string acc, FuGECommonOntologycvParamType[] cvparams ) {
		if( cvparams == null )
			return null;
		foreach( FuGECommonOntologycvParamType cv in cvparams )
			if( cv.accession == acc )
				return cv;
		return null;
	}
}

public partial class FuGECommonAuditPersonTypeAffiliations {
	/// <summary>
	/// Default constructor
	/// </summary>
	public FuGECommonAuditPersonTypeAffiliations() {
	}

	/// <summary>
	/// Simple constructor
	/// </summary>
	public FuGECommonAuditPersonTypeAffiliations( string org_ref ) {
		Organization_ref = org_ref;
	}
}

/// <summary>
/// Protein sequence
/// </summary>
public partial class PSIPIanalysissearchDBSequenceType {
	/// <summary>
	/// Default constructor
	/// </summary>
	public PSIPIanalysissearchDBSequenceType() {
	}

	/// <summary>
	/// Simple constructor
	/// </summary>
	public PSIPIanalysissearchDBSequenceType(
		string id, int length, string db_ref, string accession, string seq ) {
		this.id = id;
		this.length = length;
		this.SearchDatabase_ref = db_ref;
		this.accession = accession;
		this.seq = seq;
	}
}

/// <summary>
/// Peptide sequence
/// </summary>
public partial class PSIPIpolypeptidePeptideType {
	/// <summary>
	/// Default constructor
	/// </summary>
	public PSIPIpolypeptidePeptideType() {
	}

	/// <summary>
	/// Simple constructor
	/// </summary>
	public PSIPIpolypeptidePeptideType( string id, string seq ) {
		this.id = id;
		this.peptideSequence = seq;
	}
}

#endregion


}	// namespace EhuBio.Proteomics.Hupo.mzIdentML1_0