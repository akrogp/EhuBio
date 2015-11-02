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

namespace EhuBio.Proteomics.Inference {

/// <summary>
/// Infers proteins from PLGS peptide identifications
/// </summary>
public class Plgs : Mapper {
	/// <summary>
	/// Initializes a new instance of the <see cref="EhuBio.Proteomics.Inference.Plgs"/> class.
	/// </summary>
	public Plgs( Mapper.Software sw ) : base(sw) {
		m_Type = Mapper.SourceType.Plgs;
	}
	
	/// <summary>
	/// Gets the name of the parser.
	/// </summary>
	/// <value>
	/// The name of the parser.
	/// </value>
	public override string ParserName {
		get {
			return "Waters PLGS";
		}
	}

	/// <summary>
	/// Loader of Waters XML and TXT files
	/// </summary>
	protected override void Load( string xmlpath ) {
		string logpath = xmlpath.Contains("workflow.xml") ? xmlpath.Replace( "workflow.xml", "Log.txt" ) : null;
		if( logpath != null && !File.Exists(logpath) )
			logpath = null;
		PlgsThreshold = logpath == null ? Peptide.ConfidenceType.NoThreshold : Peptide.ConfidenceType.Yellow;
		if( PlgsThreshold != Peptide.ConfidenceType.NoThreshold ) {
			LoadThresholds( logpath );
			Notify( "Loaded peptide score thresholds from '" + System.IO.Path.GetFileName(logpath) + "'" );
			Notify( "\t* Red-Yellow threshold: " + Peptide.YellowTh );
			Notify( "\t* Yellow-Green threshold: " + Peptide.GreenTh );
		}
		LoadData( xmlpath );
	}

	private void LoadData( string xmlpath ) {
		SortedList<int,string> SortedAccession = new SortedList<int, string>();
	
		XmlDocument doc = new XmlDocument();
		doc.Load( xmlpath );
		
		// Load proteins
		XmlNodeList proteins = doc.GetElementsByTagName( "PROTEIN" );
		foreach( XmlElement element in proteins ) {
			int id = int.Parse(element.GetAttribute("ID"));
			if( SortedAccession.ContainsKey(id) )
				continue;
			string acc = element.GetElementsByTagName("ACCESSION")[0].InnerText;
			SortedAccession.Add( id, acc );
			if( m_SortedProteins.ContainsKey(acc) )
				continue;
			string entry = element.GetElementsByTagName("ENTRY")[0].InnerText;
			string desc = element.GetElementsByTagName("DESCRIPTION")[0].InnerText.Replace('+',' ');
			string seq = element.GetElementsByTagName("SEQUENCE")[0].InnerText.ToUpper();
			Protein p = new Protein(m_pid++, entry, acc, desc, seq);
			Proteins.Add( p );
			m_SortedProteins.Add( acc, p );
		}
		
		// Load peptides
		SortedList<int,Peptide> SortedPeptides = new SortedList<int, Peptide>();
		XmlNodeList peptides = doc.GetElementsByTagName( "PEPTIDE" );
		foreach( XmlElement element in peptides ) {
			int id = int.Parse(element.GetAttribute("ID"));
			int pid = int.Parse(element.GetAttribute("PROT_ID"));
			int mid = 0;
			if( PlgsThreshold != Peptide.ConfidenceType.NoThreshold )
				mid = int.Parse(element.GetAttribute("QUERY_MASS_ID"));
			string seq = element.GetAttribute("SEQUENCE").ToUpper();
			Peptide f = new Peptide(id, seq);
			XmlNodeList mods = element.GetElementsByTagName( "MATCH_MODIFIER" );
			f.Runs.Add( m_Run );
			foreach( XmlElement mod in mods ) {
				PTM ptm = new PTM();
				string[] strs = mod.GetAttribute("NAME").Split(new char[]{'+'});
				ptm.Name = strs[0];
				string str = mod.GetAttribute("POS");
				ptm.Pos = str.Length == 0 ? -1 : int.Parse(str);
				if( strs.Length > 1 )
					ptm.Residues = strs[1];
				f.AddPTM( ptm );
			}
			Protein p = null;
			try {
				p = m_SortedProteins[SortedAccession[pid]];
			} catch {
				Notify( "Peptide '" + id + "' references unknown protein '" + pid + "'" );
			}
			if( p != null ) {
				p.Peptides.Add( f );
				f.Proteins.Add( p );
				if( !p.Sequence.Contains(f.Sequence) )
					throw new ApplicationException( "Inconsistent sequence data" );
			}
			Peptides.Add( f );
			if( PlgsThreshold != Peptide.ConfidenceType.NoThreshold )
				SortedPeptides.Add(mid,f);
		}
		if( PlgsThreshold == Peptide.ConfidenceType.NoThreshold )
			return;
		
		// Scores
		XmlNodeList scores = doc.GetElementsByTagName( "MASS_MATCH" );
		foreach( XmlElement element in scores ) {
			int id = int.Parse(element.GetAttribute("ID"));
			double score = double.Parse(element.GetAttribute("SCORE"), m_Format);
			SortedPeptides[id].Score = score;
		}
	}

	/// <summary>
	/// Loads peptide score thresholds from Waters TXT file
	/// </summary>
	private void LoadThresholds( string fpath ) {
		TextReader r = new StreamReader( fpath );
		string t = r.ReadLine();
		while( t != null ) {
			if( t.Contains("Red-Yellow") ) {
				string[] f;
				f = t.Split(new string[]{"Red-Yellow Threshold is ", "Red-Yellow Threshold = "}, StringSplitOptions.None);
				f = f[1].Split(new char[]{' '});
				Peptide.YellowTh = double.Parse(f[0], m_Format);
			}
			if( t.Contains("Yellow-Green") ) {
				string[] f;
				f = t.Split(new string[]{"Yellow-Green Threshold is ", "Yellow-Green Threshold = "}, StringSplitOptions.None);
				f = f[1].Split(new char[]{' '});
				Peptide.GreenTh = double.Parse(f[0], m_Format);
			}
			t = r.ReadLine();
		}
		r.Close();
	}
	
	private int m_pid;
}

} // namespace EhuBio.Proteomics.Inference