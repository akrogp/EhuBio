// $Id: WregexManager.cs 78 2013-09-20 17:21:43Z gorka.prieto@gmail.com $
// 
// WregexManager.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      WregexManager.cs
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
using System.Text.RegularExpressions;
using EhuBio.Database.Ehu;

namespace wregex {

public enum ResultType { Original, Mutated, Lost, Gained };

public struct WregexResult {	
	public string Entry;
	public int Index;
	public int Combinations;	// Number of overlapped results (only the best one is stored)
	public string Match;
	public int Length;
	public List<string> Groups;
	public double Score;
	public ResultType Type;	
	public string Alignment {
		get {
			string str = Groups[0];
			for( int i = 1; i < Groups.Count; i++ )
				str += "-" + Groups[i];
			return str;
		}
	}
	public override string ToString(){
		return Id + " (x" + Combinations + ") " + Alignment + " score=" + Score;
	}
	public string Id {
		get { return mId == null ? (Entry + "@" + (Index+1)) : mId; }
		set { mId = value; }
	}
	
	private String mId;
}

public class WregexManager {
	public WregexManager( string RegexStr, bool grouping ) {
		mRegex = new Regex( RegexStr, RegexOptions.IgnoreCase | RegexOptions.ECMAScript | RegexOptions.Multiline | RegexOptions.Compiled );
		mPssm = null;
		mMaxLength = mMinLength = 0;
		mGrouping = grouping;
	}
	
	public WregexManager( string RegexStr, PSSM pssm, bool grouping ) : this( RegexStr, grouping ) {
		mPssm = pssm;
	}
	
	public override string ToString() {
		return mRegex == null ? "" : mRegex.ToString();
	}
	
	public List<WregexResult> Search( string seq, string id ) {
		return Search( seq, id, ResultType.Original );
	}
	
	public List<WregexResult> Search( string seq, string id, ResultType type ) {
		List<WregexResult> results = new List<WregexResult>();		
		
		Match m = mRegex.Match(seq);
		if( !m.Success )
			return results;		
		
		WregexResult result;
		do {
			result = new WregexResult();
			//result.Id = id + "@" + m.Index;
			result.Entry = id;
			result.Index = m.Index;
			result.Combinations = 1;
			result.Groups = new List<string>();
			result.Match = m.Value;			
			result.Length = m.Length;
			if( mMaxLength == 0 || m.Length > mMaxLength )
				mMaxLength = m.Length;
			if( mMinLength == 0 || m.Length < mMinLength )
				mMinLength = m.Length;
			for( int i = 1; i < m.Groups.Count; i++ )
				result.Groups.Add( m.Groups[i].Value );
			result.Score = mPssm != null ? mPssm.GetScore(result) : 0.0;
			result.Type = type;
			results.Add( result );
			m = mRegex.Match( seq, result.Index + 1 );
		} while( m.Success );
		
		if( mGrouping )
			return Filter(results);
		return results;		
	}
	
	public int MinLength {
		get { return mMinLength; }
	}
	
	public int MaxLength {
		get { return mMaxLength; }
	}
	
	private List<WregexResult> Filter( List<WregexResult> data ) {
		List<WregexResult> results = new List<WregexResult>();
		WregexResult result;
		int i, j, tmp;
		
		for( i = 0; i < data.Count; i++ ) {
			for( j = 0; j < results.Count; j++ )
				if( results[j].Entry == data[i].Entry && Math.Abs(results[j].Index-data[i].Index) < results[j].Length )
					break;
			if( j < results.Count ) {	// Overlap detected
				if( data[i].Score > results[j].Score ) {
					tmp = results[j].Combinations + 1;
					results.RemoveAt( j );
					result = data[i];
					result.Combinations = tmp;
					results.Add( result );
				}
			} else
				results.Add( data[i] );
		}
		
		return results;
	}
	
	protected Regex mRegex;
	protected PSSM mPssm;
	protected int mMaxLength;
	protected int mMinLength;
	protected bool mGrouping;
}

}	// namespace wregex