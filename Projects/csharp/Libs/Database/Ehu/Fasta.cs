// $Id$
// 
// Fasta.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      Fasta.cs
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

namespace EhuBio.Database.Ehu {

public class Variant : IComparable<Variant> {
	public string id;
	public ulong pos;
	public char orig;
	public char mut;
	
	public Variant() {
	}
	
	public Variant( string str ) {
		char[] sep = new char[]{'>'};
		string[] fields = str.Split(sep);
		id = fields[0];
		orig = fields[1][1];
		mut = fields[1][3];
		pos = ulong.Parse(fields[1].Substring(6,fields[1].Length-7))-1;
	}
	
	public override bool Equals( object obj ) {
		Variant v = (Variant)obj;
		//return v.id == id && v.pos == pos && v.orig == orig && v.mut == mut;
		return v.pos == pos && v.orig == orig && v.mut == mut;
	}
	
	public override int GetHashCode() {
		return (int)pos;
	}
	
	public void Dump() {
		Console.WriteLine( id + "> " + orig + "/" + mut + " (" + (pos+1) + ")" );
	}
	
	public int CompareTo( Variant other ) {
		return pos.CompareTo(other.pos);
	}
}

public class Fasta {
	public enum Type { Protein, Nucleotide };
	
	//public Fasta() : this(Type.Protein, "", "") {		
	//}
	
	public Fasta( Type type, string header, string sequence ) {
		mType = type;
		char[] spaces = { ' ', '\t', '\r', '\n' };
		mHeader = header.Trim( spaces );
		mSequence = sequence.Trim( spaces );
		mVariants = new List<Variant>();
		//Validate();
	}
	
	public void Dump() {
		Dump ( false );
	}
	
	public void Dump( bool variants ) {
		Console.WriteLine( '>' + mHeader );
		int lines = mSequence.Length / 80;
		int i = 0;
		for( ; i < lines; i++ )
			Console.WriteLine( mSequence.Substring(i*80, 80) );
		Console.WriteLine( mSequence.Substring(i*80) );
		if( variants )
			if( mVariants.Count == 0 )
				Console.WriteLine( "Variants not considered" );
			else
				foreach( Variant v in mVariants )
					v.Dump();
	}
	
	public void Validate() {
		throw new NotImplementedException();
		/*if( mType == Fasta.Type.Protein ) {
		} else {
		}*/
	}
	
	public string ID {
		get {
			return mHeader.Split(new char[]{' ','\t'})[0];
		}
	}
	
	public string mHeader;
	public string mSequence;
	public Type mType;
	public List<Variant> mVariants;
}

}		// namespace EhuBio.Database.Ehu