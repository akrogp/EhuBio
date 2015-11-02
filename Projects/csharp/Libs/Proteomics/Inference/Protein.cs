// $Id: Protein.cs 78 2013-09-20 17:21:43Z gorka.prieto@gmail.com $
// 
// Protein.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      Protein.cs
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
using System.Collections.Generic;

namespace EhuBio.Proteomics.Inference {

/// <summary>
/// Class for managing protein inference
/// </summary>
public class Protein {
	/// <summary>
	/// Type of protein evidence
	/// </summary>
	public enum EvidenceType { Conclusive, Group, NonConclusive, Indistinguishable, Filtered };
	
	/// <summary>
	/// Constructor
	/// </summary>
	/// <param name="ID">
	/// A <see cref="System.Int32"/> to uniquely identify the protein
	/// </param>
	/// <param name="Entry">
	/// A <see cref="System.String"/> with the protein entry name
	/// </param>
	/// <param name="Accession">
	/// A <see cref="System.String"/> with the protein accession number
	/// </param>
	/// <param name="Desc">
	/// A <see cref="System.String"/> with a description of the protein (optional)
	/// </param>
	/// <param name="Seq">
	/// A <see cref="System.String"/> with the protein sequence (optional)
	/// </param>
	public Protein( int ID, string Entry, string Accession, string Desc, string Seq ) {
		this.ID = ID;
		this.Entry = Entry;
		this.Accession = Accession;
		this.Desc = Desc;
		this.Sequence = Seq;
		Peptides = new List<Peptide>();
		Subset = new List<Protein>();
		Evidence = Protein.EvidenceType.NonConclusive;
	}
	
	/// <summary>
	/// Protein ID
	/// </summary>
	public override string ToString() {
		return ID.ToString();
	}
	
	/// <summary>
	/// Checks for the presence of the given peptide in the peptide list of the protein
	/// </summary>
	public bool HasPeptide( Peptide fo ) {
		return Peptides.Contains( fo );
	}
	
	/// <summary>
	/// Entry or accession (if entry is empty)
	/// </summary>
	public string Name {
		get { return (Entry == null || Entry == "") ? Accession : Entry; }
	}
	
	/// <summary>
	/// Protein ID
	/// </summary>
	public int ID;
	
	/// <summary>
	/// Protein entry name
	/// </summary>
	public string Entry;
	
	/// <summary>
	/// Protein accession number
	/// </summary>
	public string Accession;
	
	/// <summary>
	/// Gets the entry name or accession if entry name is not available.
	/// </summary>
	public string EntryEx {
		get {
			if( Entry == null || Entry.Length == 0 )
				return Accession;
			return Entry;
		}
	}
	
	/// <summary>
	/// Protein description
	/// </summary>
	public string Desc;
	
	/// <summary>
	/// DBSequence ID used by mzIdentML
	/// </summary>
	public string DBRef;
	
	/// <summary>
	/// Protein sequence
	/// </summary>
	public string Sequence;
	
	/// <summary>
	/// Returns the sequence parsed in columns.
	/// </summary>
	public string ParseSeq( int cols ) {
		if( Sequence == null || Sequence.Length == 0 )
			return "";
		string str = "";
		int col = 10;
		int pos = 0;
		int len = 0;
		int c = cols;
		bool bFirst = true;
		int digits = ((int)Math.Log10(Sequence.Length))+1;
		
		while( pos < Sequence.Length ) {
			if( c >= cols ) {
				if( !bFirst )
					str += "\n";
				else
					bFirst = false;
				str += String.Format( "{0,"+digits+"}", pos+1 );
				c=0;
			}
			len = (pos+col) < Sequence.Length ? col : (Sequence.Length-pos);
			str += " " + Sequence.Substring( pos, len );
			pos += len;
			c++;
		}
		
		return str;
	}
	
	/// <summary>
	/// List of identified peptides present in the protein
	/// </summary>
	public List<Peptide> Peptides;
	
	/// <summary>
	/// List of proteins in the group
	/// </summary>
	public List<Protein> Subset;
	
	/// <summary>
	/// Protein evidence
	/// </summary>
	public EvidenceType Evidence;
	
	/// <summary>
	/// Group to which the protein belongs
	/// </summary>
	public Protein Group = null;
}

} // namespace EhuBio.Proteomics.Inference