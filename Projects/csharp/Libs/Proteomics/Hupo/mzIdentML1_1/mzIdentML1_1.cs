// $Id$
// 
// mzidFile1_1.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      mzidFile1_1.cs
//  
// Copyright (c) 2012 Gorka Prieto
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
using EhuBio.Proteomics.Hupo.mzIdentML1_1;

namespace EhuBio.Proteomics.Hupo.mzIdentML {

/// <summary>
/// Class mzidFile1_1:
/// - Validates mzIdentML 1.1.0 files using internal XSD
/// - Reads/writes C# objects to a mzIdentML file using deserialization/serialization
/// </summary>
public class mzidFile1_1 {
	/// <summary>
	/// Initializes a new instance of the <see cref="EhuBio.Proteomics.Hupo.mzIdentML.mzidFile1_1"/> class.
	/// </summary>
	public mzidFile1_1() {
		Default();
	}
	
	private void Default() {
		ListSW = new List<AnalysisSoftwareType>();
		ListOrganizations = new List<OrganizationType>();
		ListPeople = new List<PersonType>();
		ListProteins = new List<DBSequenceType>();
		ListPeptides = new List<PeptideType>();
	}
	
	/// <summary>
	/// Validates the mzIdentML file using the embedded schema
	/// </summary>
	public static void Validate( string xml ) {
		XmlReaderSettings settings = new XmlReaderSettings();
		/*foreach( string s in Assembly.GetExecutingAssembly().GetManifestResourceNames() )
			Console.WriteLine( s );*/
		XmlSchema xsd = XmlSchema.Read(
			Assembly.GetExecutingAssembly().GetManifestResourceStream("Hupo.mzIdentML1_1.mzIdentML1.1.0.xsd"), XsdValidationHandler );
		settings.Schemas.Add( xsd );
		settings.ValidationType = ValidationType.Schema;
		XmlReader rd = XmlReader.Create( xml, settings );
		try {
			while( rd.Read() );
		} catch( Exception e ) {
			throw new ApplicationException( "XML not valid: " + e.Message );
		}
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
		XmlSerializer serializer = new XmlSerializer(typeof(MzIdentMLType));
		TextReader reader = new StreamReader(xml);
		Data = (MzIdentMLType)serializer.Deserialize(reader);
		reader.Close();
		//Data = MzIdentMLType.LoadFromFile( xml );
		
		// Parse data
		if( Data.AnalysisSoftwareList != null )		
			ListSW = new List<AnalysisSoftwareType>( Data.AnalysisSoftwareList );
		if( Data.AuditCollection != null ) {
			ListOrganizations.Clear();
			ListPeople.Clear();
			foreach( IdentifiableType contact in Data.AuditCollection )
				if( contact is OrganizationType )
					ListOrganizations.Add( contact as OrganizationType );
				else if( contact is PersonType )
					ListPeople.Add( contact as PersonType );
		}
		if( Data.SequenceCollection == null || Data.SequenceCollection.DBSequence == null
			|| Data.SequenceCollection.Peptide == null || Data.SequenceCollection.PeptideEvidence == null )
			throw new ApplicationException( "mzIdentML file without identification sequences" );
		ListProteins = new List<DBSequenceType>( Data.SequenceCollection.DBSequence );
		ListPeptides = new List<PeptideType>( Data.SequenceCollection.Peptide );
		ListEvidences = new List<PeptideEvidenceType>( Data.SequenceCollection.PeptideEvidence );

		System.GC.Collect();
	}
	
	/// <summary>
	/// Writes current data to a mzid file
	/// </summary>
	/// <param name="xml">
	/// A <see cref="System.String"/> with the output file name
	/// </param>
	public void Save( string xml ) {
		Data.creationDate = DateTime.UtcNow;
	
		// AnalysisSoftwareList
        Data.AnalysisSoftwareList = ListSW.ToArray();
        
        // AuditCollection
        List<AbstractContactType> audit = new List<AbstractContactType>();
        audit.AddRange( ListOrganizations );
        audit.AddRange( ListPeople );
        Data.AuditCollection = audit.ToArray();
        
        // SequenceCollection
        SequenceCollectionType seq = new SequenceCollectionType();
        seq.DBSequence = ListProteins.ToArray();
        seq.Peptide = ListPeptides.ToArray();
        seq.PeptideEvidence = ListEvidences.ToArray();       
        Data.SequenceCollection = seq;
        
        // Serialization
        XmlSerializer serializer =  new XmlSerializer(typeof(MzIdentMLType));
        TextWriter writer = new StreamWriter(xml);
        serializer.Serialize(writer, Data);
        writer.Close();
        //Data.SaveToFile( xml );
        
        System.GC.Collect();
	}
	
	/// <summary>
	/// Search sequence online in the corresponding DB
	/// </summary>
	public void RetrieveSequences() {
		WSDBFetchServerService db = new WSDBFetchServerService();
		foreach( DBSequenceType p in ListProteins ) {
			if( p.Seq!= null )
				continue;
			if( p.searchDatabase_ref.ToUpper() != "SDB_SWISSPROT" )
				throw new ApplicationException( "DB " + p.searchDatabase_ref + " not supported" );
			Console.WriteLine( "Retrieving " + p.accession + " ..." );
			string fasta = db.fetchData("UNIPROT:"+p.accession, "fasta", "raw");
			fasta = fasta.Remove(0,fasta.IndexOf('\n'));
			string seq = "";
			foreach( string str in fasta.Split('\n') )
				seq += str;
			p.Seq = seq;//seq.ToUpper();
		}
	}
	
	/// <summary>
	/// Helper method for including an additional analysis software to the list
	/// </summary>
	public void AddAnalysisSoftware(
		string id, string name, string version, string uri, string cv, string accession, string customizations ) {
		
		AnalysisSoftwareType sw = new AnalysisSoftwareType();
		sw.id = id;
		sw.name = name;
		sw.version = version;
		sw.uri = uri;
		if( cv != null && accession != null ) {
			CVParamType p = new CVParamType();
			p.cvRef = cv;
			p.accession = accession;
			p.name = name;
			sw.SoftwareName.Item = p;
		}
		if( customizations != null )
			sw.Customizations = customizations;
		ListSW.Add( sw );
	}
	
	/// <summary>
	/// Finds the specified CV in cvparams.
	/// </summary>
	/// <param name='acc'>
	/// Accession number of the desired CV.
	/// </param>
	/// <param name='cvparams'>
	/// Search array of CV terms.
	/// </param>
	public static CVParamType FindCV( string acc, AbstractParamType[] cvparams ) {
		if( cvparams == null )
			return null;
		foreach( AbstractParamType p in cvparams )
			if( p is CVParamType ) {
				CVParamType cv = p as CVParamType;
				if( cv.accession == acc )
					return cv;
			}
		return null;
	}

	/// <summary>
	/// Finds the specified CV in cvparams.
	/// </summary>
	/*public static CVParamType FindCV( string acc, AbstractParamType p ) {
		if( p == null )
			return null;
		if( p is CVParamType ) {
			CVParamType cv = p as CVParamType;
			if( cv.accession == acc )
				return cv;
		}
		return null;
	}*/
	
	private static void XsdValidationHandler( object sender, ValidationEventArgs e ) {
		throw new ApplicationException( "XSD not valid: " + e.Message );
	}
	
	/// <summary>
	/// Serializable object with all the mzIdentML file data
	/// </summary>
	public MzIdentMLType Data;
	
	/// <summary>
	/// The organization list.
	/// </summary>
	public List<OrganizationType> ListOrganizations;
	
	/// <summary>
	/// The person list.
	/// </summary>
	public List<PersonType> ListPeople;
	
	/// <summary>
	/// Analysis software list
	/// </summary>
	public List<AnalysisSoftwareType> ListSW;
		
	/// <summary>
	/// Protein list
	/// </summary>
	public List<DBSequenceType> ListProteins;
	
	/// <summary>
	/// Peptide list
	/// </summary>
	public List<PeptideType> ListPeptides;
	
	/// <summary>
	/// Peptide evidence list	
	/// </summary>
	public List<PeptideEvidenceType> ListEvidences;
}

}	// namespace EhuBio.Proteomics.Hupo.mzIdentML