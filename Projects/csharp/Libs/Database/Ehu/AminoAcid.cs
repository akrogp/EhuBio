// $Id: AminoAcid.cs 78 2013-09-20 17:21:43Z gorka.prieto@gmail.com $
// 
// AminoAcid.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      AminoAcid.cs
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

public class AminoAcid {
	public readonly string Name;
	public readonly char Letter;
	public readonly string Abbrev;
	public readonly double Mass;
	public readonly double pI;
	public readonly double pK1;
	public readonly double pK2;
	public readonly double pKa;
	public readonly bool Acidic;
	public readonly bool Basic;
	public readonly bool Hydrophobic;
	public readonly bool Polar;
	public readonly short Charge;
	public readonly bool Positive;
	public readonly bool Negative;
	public readonly bool Small;
	public readonly bool Tiny;
	public readonly bool Aromatic;
	public readonly bool Aliphatic;
	
	public AminoAcid(
		string Name, char Letter, string Abbrev, double Mass,
		double pI, double pK1, double pK2, double pKa,
		bool Hydrophobic, bool Polar, short Charge,
		bool Small, bool Tiny, bool Aromatic, bool Aliphatic ) {
		this.Name = Name;
		this.Letter = Letter;
		this.Abbrev = Abbrev;
		this.Mass = Mass;
		this.pI = pI;
		Acidic = pI < 7.0;
		Basic = pI > 7.0;
		this.pK1 = pK1;
		this.pK2 = pK2;
		this.pKa = pKa;
		this.Hydrophobic = Hydrophobic;
		this.Polar = Polar;
		this.Charge = Charge;
		Positive = Charge > 0;
		Negative = Charge < 0;
		this.Small = Small;
		this.Tiny = Tiny;
		this.Aromatic = Aromatic;
		this.Aliphatic = Aliphatic;
	}
	
	public static AminoAcid Get( char ch ) {
		if( mLUT == null )
			Initialize();
		return mLUT[char.ToUpper(ch) - 'A'];
	}
	
	public static AminoAcid Get( string abbrev ) {
		if( mLUT == null )
			Initialize();
		return mMap[abbrev];
	}
	
	public static void Initialize() {
		if( mLUT != null )
			return;

		Standard.Add(Alanine);
		Standard.Add(Cysteine);
		Standard.Add(Aspartic);
		Standard.Add(Glutamic);
		Standard.Add(Phenylalanine);
		Standard.Add(Glycine);
		Standard.Add(Histidine);
		Standard.Add(Isoleucine);
		Standard.Add(Lysine);
		Standard.Add(Leucine);
		Standard.Add(Methionine);
		Standard.Add(Asparagine);
		Standard.Add(Pyrrolysine);
		Standard.Add(Proline);
		Standard.Add(Glutamine);
		Standard.Add(Arginine);
		Standard.Add(Serine);
		Standard.Add(Threonine);
		Standard.Add(Selenocysteine);
		Standard.Add(Valine);
		Standard.Add(Tryptophan);
		Standard.Add(Tyrosine);
		
		int n = 'z'-'a';
		mLUT = new AminoAcid[n];
		foreach( AminoAcid aa in Standard ) {
			mLUT[aa.Letter - 'A'] = aa;
			mMap.Add( aa.Abbrev, aa );
		}
	}
	
	public override string ToString() {
		return Letter.ToString();
	}
	
	public static readonly AminoAcid Alanine = new AminoAcid("Alanine",'A',"Ala",89.09404,6.01,2.35,9.87,-1.00,true,false,0,true,true,false,false);
	public static readonly AminoAcid Cysteine = new AminoAcid("Cysteine",'C',"Cys",121.15404,5.05,1.92,10.70,8.18,false,false,0,true,false,false,false);
	public static readonly AminoAcid Aspartic = new AminoAcid("Aspartic acid",'D',"Asp",133.10384,2.85,1.99,9.90,3.90,false,true,-1,true,false,false,false);
	public static readonly AminoAcid Glutamic = new AminoAcid("Glutamic acid",'E',"Glu",147.13074,3.15,2.10,9.47,4.07,false,true,-1,false,false,false,false);
	public static readonly AminoAcid Phenylalanine = new AminoAcid("Phenylalanine",'F',"Phe",165.19184,5.49,2.20,9.31,-1.00,true,false,0,false,false,true,false);
	public static readonly AminoAcid Glycine = new AminoAcid("Glycine",'G',"Gly",75.06714,6.06,2.35,9.78,-1.00,true,false,0,true,true,false,false);
	public static readonly AminoAcid Histidine = new AminoAcid("Histidine",'H',"His",155.15634,7.60,1.80,9.33,6.04,false,true,1,false,false,true,false);
	public static readonly AminoAcid Isoleucine = new AminoAcid("Isoleucine",'I',"Ile",131.17464,6.05,2.32,9.76,-1.00,true,false,0,false,false,false,true);
	public static readonly AminoAcid Lysine = new AminoAcid("Lysine",'K',"Lys",146.18934,9.60,2.16,9.06,10.54,false,true,1,false,false,false,false);
	public static readonly AminoAcid Leucine = new AminoAcid("Leucine",'L',"Leu",131.17464,6.01,2.33,9.74,-1.00,true,false,0,false,false,false,true);
	public static readonly AminoAcid Methionine = new AminoAcid("Methionine",'M',"Met",149.20784,5.74,2.13,9.28,-1.00,true,false,0,false,false,false,false);
	public static readonly AminoAcid Asparagine = new AminoAcid("Asparagine",'N',"Asn",132.11904,5.41,2.14,8.72,-1.00,false,true,0,true,false,false,false);
	public static readonly AminoAcid Pyrrolysine = new AminoAcid("Pyrrolysine",'O',"Pyl",-1.00000,-1.00,-1.00,-1.00,-1.00,false,false,0,false,false,false,false);
	public static readonly AminoAcid Proline = new AminoAcid("Proline",'P',"Pro",115.13194,6.30,1.95,10.64,-1.00,true,false,0,true,false,false,false);
	public static readonly AminoAcid Glutamine = new AminoAcid("Glutamine",'Q',"Gln",146.14594,5.65,2.17,9.13,-1.00,false,true,0,false,false,false,false);
	public static readonly AminoAcid Arginine = new AminoAcid("Arginine",'R',"Arg",174.20274,10.76,1.82,8.99,12.48,false,true,1,false,false,false,false);
	public static readonly AminoAcid Serine = new AminoAcid("Serine",'S',"Ser",105.09344,5.68,2.19,9.21,-1.00,false,true,0,true,true,false,false);
	public static readonly AminoAcid Threonine = new AminoAcid("Threonine",'T',"Thr",119.12034,5.60,2.09,9.10,-1.00,false,true,0,true,false,false,false);
	public static readonly AminoAcid Selenocysteine = new AminoAcid("Selenocysteine",'U',"Sec",168.05300,-1.00,-1.00,-1.00,5.73,true,false,0,true,false,false,false);
	public static readonly AminoAcid Valine = new AminoAcid("Valine",'V',"Val",117.14784,6.00,2.39,9.74,-1.00,true,false,0,true,false,false,true);
	public static readonly AminoAcid Tryptophan = new AminoAcid("Tryptophan",'W',"Trp",204.22844,5.89,2.46,9.41,-1.00,true,false,0,false,false,true,false);
	public static readonly AminoAcid Tyrosine = new AminoAcid("Tyrosine",'Y',"Tyr",181.19124,5.64,2.20,9.21,10.46,false,true,0,false,false,true,false);
	
	public static readonly List<AminoAcid> Standard = new List<AminoAcid>();
		
	private static AminoAcid[] mLUT;
	private static SortedList<string,AminoAcid> mMap = new SortedList<string, AminoAcid>();
}

}	// namespace EhuBio.Database.Ehu