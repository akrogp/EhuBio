// $Id: Main.cs 78 2013-09-20 17:21:43Z gorka.prieto@gmail.com $
// 
// Main.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      Main.cs
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
using System.IO.Compression;
using System.Collections.Generic;
using System.Threading;
using EhuBio.Database.Ehu;
using EhuBio.Database.Ebi;
using EhuBio.Database.Ncbi.eFetch.Snp;

namespace wregex {

class WregexConsole {
	public static int Main( string[] args ) {
		if( args.Length % 2 != 0 ) {
			DisplayUsage( "odd number of arguments" );
			return 1;
		}
		
		string RegexFile="";
		string PssmFile="";
		string DatabaseFile="";
		string VariantsFile="";
		string OutputDir=".";
		bool grouping = false;
		for( int i = 0; i < args.Length; i += 2 ) {
			if( args[i][0] != '-' || args[i].Length != 2 ) {
				DisplayUsage( "incorrect argument specifier" );
				return 1;
			}
			switch( args[i][1] ) {
				case 'o': OutputDir = args[i+1]; break;
				case 'd': DatabaseFile = args[i+1]; break;
				case 'r': RegexFile = args[i+1]; break;
				case 'p': PssmFile = args[i+1]; break;
				case 'v': VariantsFile = args[i+1]; break;
				case 'g': grouping = args[i+1][0] != '0'; break;
				default: DisplayUsage( "specifier '" + args[i] + "'not known" ); return 1;
			}
		}
		if( DatabaseFile.Length == 0 || RegexFile.Length == 0 ) {
			DisplayUsage( "missing mandatory parameters" );
			return 1;
		}
		if( !DatabaseFile.Contains(".fasta") && !DatabaseFile.Contains(".xml.gz") ) {
			DisplayUsage( "database file format not recognized" );
			return 1;
		}
		if( !DatabaseFile.Contains(".xml.gz") && VariantsFile.Length != 0 ) {
			DisplayUsage( "variants are only supported with UniProt XML input file" );
			return 1;
		}
		
		// Use '.' as decimal separator
        Thread.CurrentThread.CurrentCulture = new System.Globalization.CultureInfo( "en-US", false );
		
		WregexConsole app = new WregexConsole();
		app.Create( RegexFile, PssmFile, DatabaseFile, VariantsFile, OutputDir, grouping );
		//app.Dump();
		app.Run();
		
		return 0;
	}
	
	public static void DisplayUsage( string err ) {
		Console.WriteLine( "ERROR: " + err );
		Console.WriteLine( "\nUsage:" );
		Console.WriteLine( "\twregex -r <regex_file> [-p <pssm_file>] -d <file.fasta|file.xml.gz> [-v <variants.txt.gz>] [-g 0|1] [-o <output_dir>]" );
	}
	
	private void Create( string RegexFile, string PssmFile, string DatabaseFile, string VariantsFile, string OutputDir, bool grouping ) {
		string line;
				
		// regex
		UnixCfg rd = new UnixCfg( RegexFile );
		line = rd.ReadUnixLine();
		if( line == null )
			throw new ApplicationException( "Empty regex" );
		rd.Close();
		if( PssmFile.Length > 0 )
			mRegex = new WregexManager( line, new PSSM(PssmFile), grouping );
		else
			mRegex = new WregexManager( line, grouping );
		
		// Fasta
		mSeqs = new List<Fasta>();
		if( DatabaseFile.Contains(".fasta") )
			LoadFasta( DatabaseFile );
		else {
			SortedList<string,List<Variant>> list = null;
			if( VariantsFile.Length != 0 )
				list = LoadVariants( VariantsFile );
			LoadXml( DatabaseFile, list );
		}
		
		// Sort Variants
		foreach( Fasta seq in mSeqs )
			seq.mVariants.Sort();
		
		mDataId = Path.GetFileNameWithoutExtension( DatabaseFile );
		mOutputDir = OutputDir;
		Directory.CreateDirectory( OutputDir );
	}
	
	private SortedList<string,List<Variant>> LoadVariants( string path ) {		
		StreamReader rd = new StreamReader(new GZipStream(new FileStream(path,FileMode.Open), CompressionMode.Decompress));
		SortedList<string,List<Variant>> list = new SortedList<string,List<Variant>>();
		string line;
		char[] sep1 = new char[]{','};
		char[] sep2 = new char[]{'/'};
		string[] fields, fields2;
		Variant v;
		while( (line=rd.ReadLine()) != null ) {
			fields = line.Split(sep1);
			if( !fields[2].Contains("/") || fields[5].Length == 0 || fields[3].Length == 0 || fields[3] != fields[4] )
				continue;
			v = new Variant();
			v.id = fields[5];
			v.pos = ulong.Parse(fields[3])-1;
			fields2 = fields[2].Split(sep2);
			v.orig = fields2[0][0];
			v.mut = fields2[1][0];
			if( v.mut == '*' )
				continue;
			if( list.ContainsKey(v.id) )
				if( list[v.id].Contains(v) )
					continue;
				else
					list[v.id].Add(v);
			else {
				list[v.id] = new List<Variant>();
				list[v.id].Add( v );
			}
		}
		return list;
	}
	
	private void LoadFasta( string path ) {
		string line;
		//char[] sep = new char[]{'|',' ','\t'};
		char[] sep = new char[]{'|'};
		Variant v;
		UnixCfg rd = new UnixCfg( path );
		line = rd.ReadUnixLine();
		if( line == null || line[0] != '>' )
			throw new ApplicationException( "FASTA header not found" );		
		Fasta f = new Fasta(Fasta.Type.Protein, line.Split(sep)[0].Substring(1), "");
		do {
			line = rd.ReadUnixLine();
			if( line == null || line[0] == '>' ) {	// EOF or next element
				if( f.mSequence.Length == 0 )
					throw new ApplicationException( "FASTA sequence not found" );
				mSeqs.Add(f);
				if( line != null )
					f = new Fasta(Fasta.Type.Protein, line.Split(sep)[0].Substring(1), "");
			} else if( line.StartsWith("NP_") ) { // Variant
				v = new Variant(line);
				if( !f.mVariants.Contains(v) )
					f.mVariants.Add(v);
			}
			else // Sequence
				f.mSequence += line;
		} while( line != null );
		rd.Close();
	}
	
	private void LoadXml( string path, SortedList<string,List<Variant>> list ) {
		UniprotXml xml = new UniprotXml( path );
		EhuBio.Database.Ebi.Xml.entry e;
		Fasta f = null;
		bool skip = false;
		while( (e=xml.ReadEntry()) != null ) {
			if( e.sequence == null || e.sequence.Value == null || e.sequence.Value.Length == 0 )
				continue;				
			if( list != null ) {
				skip = true;
				foreach( EhuBio.Database.Ebi.Xml.featureType feature in e.feature )
					if( feature.type == EhuBio.Database.Ebi.Xml.featureTypeType.sequencevariant && feature.id != null )
						if( list.ContainsKey(feature.id) ) {
							if( skip == true ) {
								f = new Fasta( Fasta.Type.Protein, e.accession[0], e.sequence.Value );
								skip = false;
							}
							f.mVariants.AddRange( list[feature.id] );
						}
			} else
				f = new Fasta( Fasta.Type.Protein, e.accession[0], e.sequence.Value );
			if( skip )
				continue;
			f.Dump( true );			
			mSeqs.Add( f );
		}
		xml.Close();
	}	
	
	private void Dump() {
		Console.WriteLine( "regex: " + (mRegex == null ? "<empty>" : mRegex.ToString()) );
		foreach( Fasta seq in mSeqs )
			seq.Dump();
	}
	
	private void Run() {
		List<WregexResult> results = GetResults();
		if( results.Count == 0 ) {
			Console.WriteLine( "No matches found, please consider reviewing the regex" );
			return;
		}
		WriteAln( results );
		WriteCsv( results );
		ShowResults( results );
		ShowCandidates( results );
	}
	
	private List<WregexResult> GetResults() {
		List<WregexResult> results = new List<WregexResult>();
		List<WregexResult> tmp_results;
		
		Console.WriteLine( "Searching with '" + mRegex + "' ...\n" );
		foreach( Fasta seq in mSeqs ) {
			if( seq.mVariants.Count == 0 )
				tmp_results = mRegex.Search( seq.mSequence, seq.ID );
			else
				tmp_results = GetTotalVariantsResults(seq);
			if( tmp_results == null )
				continue;
			seq.Dump(true);
			foreach( WregexResult result in tmp_results ) {
				results.Add( result );
				Console.WriteLine( "* Match!! -> " + result.Match +
					" (" + (result.Index+1) + ".." + (result.Index+result.Length) + ") -> " +
					result.ToString() );
			}
			Console.WriteLine();
		}
		
		results.Sort(delegate(WregexResult r1, WregexResult r2) {
			if( r1.Score > r2.Score )
				return -1;
			if( r1.Score < r2.Score )
				return 1;
			return 0;
		});
		
		return results;
	}
		
	
	private List<List<Variant>> GetMutations( Fasta seq ) {
		return GetMutations( seq, 20000 );
	}
	
	private List<List<Variant>> GetMutations( Fasta seq, int max ) {
		List<List<Variant>> result = new List<List<Variant>>();
		int i1 = 0, i2 = 0;
		
		while( i1 < seq.mVariants.Count ) {
			do {
				i2++;
			} while( i2 < seq.mVariants.Count && (seq.mVariants[i2].pos-seq.mVariants[i2-1].pos) <= (ulong)max );
			result.AddRange(GetMutations(seq,i1,i2-1));
			i1 = i2;
		}
		
		return result;
	}
	
	private List<List<Variant>> GetMutations( Fasta seq, int i1, int i2 ) {
		List<List<Variant>> result = new List<List<Variant>>();
		int len = i2 - i1 + 1;
		if( len <= 0 )
			return result;
		
		List<Variant> comb;
		char[] array;				
		int combinations = 1 << len;
		bool dup;
		for( int i = 1; i < combinations; i++ ) {
			array = Convert.ToString(i,2).ToCharArray();
			comb = new List<Variant>();
			for( int j = 0; j < array.Length; j++ )
				if( array[array.Length-j-1] == '1' ) {
					dup = false;
					foreach( Variant v in comb )
						if( v.pos == seq.mVariants[i1+j].pos ) {
							dup = true;
							break;
						}
					if( !dup )
						comb.Add( seq.mVariants[i1+j] );
				}
			dup = false;
			foreach( List<Variant> list in result ) {
				if( list.Count != comb.Count )
					continue;
				dup = true;
				foreach( Variant v in comb )
					if( !list.Contains(v) ) {
						dup = false;
						break;
					}
				if( dup )
					break;
			}
			if( !dup )
				result.Add( comb );
		}
		
		return result;
	}
	
	private List<List<Variant>> GetMutations( Fasta seq, WregexResult r ) {
		int i1 = 0, i2;
		
		while( i1 < seq.mVariants.Count && (int)seq.mVariants[i1].pos < r.Index )
			i1++;
		i2 = i1;
		while( i2 < seq.mVariants.Count && (int)seq.mVariants[i2].pos < (r.Index+r.Length) )
			i2++;
		
		return GetMutations( seq, i1, i2-1 );
	}
	
	private List<WregexResult> GetVariantsResults( string id, string str, List<Variant> variants ) {
		return GetVariantsResults( id, str, 0, variants );
	}
	
	private List<WregexResult> GetVariantsResults( string id, string str, int offset, List<Variant> variants ) {
		char[] array = str.ToCharArray();
		foreach( Variant v in variants )
			if( (int)v.pos < (str.Length + offset) && (int)v.pos >= offset )
				array[(int)v.pos-offset] = v.mut;
		List<WregexResult> res = new List<WregexResult>();
		res.AddRange( mRegex.Search(new String(array),id,ResultType.Mutated) );
		return res;
	}
	
	private List<WregexResult> GetVariantsResults( string id, string str, List<List<Variant>> variants ) {
		return GetVariantsResults( id, str, 0, variants );
	}
	
	private List<WregexResult> GetVariantsResults( string id, string str, int offset, List<List<Variant>> variants ) {
		List<WregexResult> res = new List<WregexResult>();
		foreach( List<Variant> v in variants )
			res.AddRange( GetVariantsResults(id,str,offset,v) );
		return res;
	}
	
	private string GetID( List<Variant> m, WregexResult r ) {
		return GetID( m, r, false );
	}
	
	private string GetID( List<Variant> m, WregexResult r, bool original ) {
		string id = "";
		foreach( Variant v in m )
			if( (int)v.pos >= r.Index && (int)v.pos < (r.Index+r.Length) )
				if( original || r.Match[(int)v.pos-r.Index] == v.mut )
					id += "-" + v.orig + (v.pos+1).ToString() + v.mut;
		return id;
	}
	
	private List<WregexResult> GetTotalVariantsResults( Fasta seq ) {
		List<WregexResult> ret = new List<WregexResult>();
		WregexResult[] orig, mut;
		bool found;
		int i, j;
		List<List<Variant>> mutations;
		string id;
		
		// Original (without mutations)
		orig = mRegex.Search(seq.mSequence, seq.ID+"-orig").ToArray();
		ret.AddRange( orig );
		
		// Lost		
		foreach( WregexResult r in orig ) {
			/*if( r.Entry.Contains("NP_002968.1") )
				Console.WriteLine( "KK" );*/
			mutations = GetMutations( seq, r );
			foreach( List<Variant> m in mutations )
				if( GetVariantsResults(seq.ID, r.Match, r.Index, m).Count == 0 ) {
					WregexResult r2 = r;
					r2.Type = ResultType.Lost;
					r2.Entry = r2.Entry.Replace("-orig","-lost") + GetID(m, r, true);
					ret.Add( r2 );
				}
		}
		
		// Mutations
		mutations = GetMutations( seq, mRegex.MaxLength );
		mut = GetVariantsResults( seq.ID, seq.mSequence, mutations ).ToArray();
		for( i = 0; i < mut.Length; i++ ) {
			// Filter duplicates
			found = false;
			foreach( WregexResult r in ret )
				if( r.Index == mut[i].Index && r.Match.Equals(mut[i].Match) ) {
					found = true;
					break;
				}
			if( found )
				continue;
			// Assign names
			id = GetID ( seq.mVariants, mut[i] );
			if( id.Length == 0 )
				continue;
			mut[i].Entry += id;
			// Gained
			found = false;
			for( j = 0; j < orig.Length; j++ )
				if( mut[i].Index == orig[j].Index ) {
					found = true;
					break;
				}
			if( !found )
				mut[i].Type = ResultType.Gained;
			ret.Add( mut[i] );
		}
		
		return ret.Count == 0 ? null : ret;
	}
	
	private void WriteAln( List<WregexResult> results ) {
		int count = results[0].Groups.Count+1;
		int[] gsizes = new int[count];
		int i, j, gsize;
		
		// Calculate lengths for further alignment
		for( i = 0; i < count; i++ )
			gsizes[i] = 0;
		foreach( WregexResult result in results ) {
			gsize = result.Id.Length;
			if( gsize > gsizes[0] )
				gsizes[0] = gsize;
			for( i = 1; i < count; i++ ) {
				gsize = result.Groups[i-1].Length;
				if( gsizes[i] < gsize )
					gsizes[i] = gsize;
			}
		}
		
		TextWriter wr = new StreamWriter( Path.Combine(mOutputDir,mDataId) + ".aln", false );
		wr.WriteLine( "CLUSTAL 2.1 multiple sequence alignment (by WREGEX)\n\n" );
		foreach( WregexResult result in results ) {
			wr.Write( result.Id );
			for( j = result.Id.Length; j < gsizes[0]+4; j++ )
				wr.Write( ' ' );
			for( i = 1; i < count; i++ ) {
				wr.Write( result.Groups[i-1] );
				for( j = result.Groups[i-1].Length; j < gsizes[i]; j++ )
					wr.Write( '-' );
			}
			wr.WriteLine();
		}
		wr.WriteLine();
		wr.Close();
	}
	
	private void WriteCsv( List<WregexResult> results ) {
		TextWriter wr = new StreamWriter( Path.Combine(mOutputDir,mDataId) + ".csv", false );
		wr.WriteLine( "ID,Entry,Pos,Combinations,Sequence,Alignment,Score" );
		foreach( WregexResult result in results )
			wr.WriteLine( result.Id + "," + result.Entry + "," + (result.Index+1) + "," + result.Combinations
				+ "," + result.Match + "," + result.Alignment + "," + result.Score );
		wr.Close();
	}
	
	private void ShowResults( List<WregexResult> results ) {
		foreach( WregexResult res in results ) {
			Console.WriteLine( res.ToString() );
		}
	}
	
	private void ShowCandidates( List<WregexResult> results ) {
		string id;
		
		Console.WriteLine( "\nLooking for candidates with score difference > 15 ..." );
		foreach( WregexResult res in results ) {
			if( res.Type != ResultType.Original )
				continue;
			id = res.Entry.Substring(0,res.Entry.IndexOf('-')+1);
			foreach( WregexResult res2 in results ) {
				if( res2.Type == ResultType.Original || !res2.Entry.Contains(id) )
					continue;
				if( res.Index == res2.Index
					//&& (res.Score > 50.0 || res2.Score > 50.0)
					&& Math.Abs(res.Score-res2.Score) > 15.0 ) {
					Console.WriteLine( "* New Candidate!!" );
					Console.WriteLine( res.ToString() );
					Console.WriteLine( res2.ToString() );
				}
			}
		}
		
		Console.WriteLine( "\nLooking for candidates with match lost ..." );
		foreach( WregexResult res in results ) {			
			if( res.Type != ResultType.Lost )
				continue;
			Console.WriteLine( "* New Candidate!!" );
			Console.WriteLine( res.ToString() );
		}
		
		Console.WriteLine( "\nLooking for candidates with match gained ..." );		
		foreach( WregexResult res in results ) {
			if( res.Type != ResultType.Gained )
				continue;
			Console.WriteLine( "* New Candidate!!" );
			Console.WriteLine( res.ToString() );
		}
	}
	
	private WregexManager mRegex;
	private List<Fasta> mSeqs;
	private string mDataId;
	private string mOutputDir;
}

}	// namespace wregex