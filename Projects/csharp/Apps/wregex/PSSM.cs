// $Id$
// 
// PSSM.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      PSSM.cs
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
using EhuBio.Database.Ehu;

namespace wregex {

public struct PSSMentry {
	public string condition;
	public double score;
}

public struct PSSMposition {
	public uint order;
	public List<PSSMentry> entries;
}

public class PSSM {
	public PSSM() {
		m_pssm = new List<PSSMposition>();
	}
	
	public PSSM( string filename ) : this() {
		Load( filename );
		Normalize();
	}
	
	/*public void Load( string filename ) {
		UnixCfg rd = new UnixCfg( filename );
		string line = rd.ReadUnixLine();
		char[] sep = new char[]{ ':' };
		string[] fields;
		
		while( line != null ) {
			PSSMposition pos;
			pos.order = uint.Parse( line.Replace("{","") );
			pos.entries = new List<PSSMentry>();
			line = rd.ReadUnixLine();
			while( line != null && !line.Contains("}") ) {
				PSSMentry entry;
				fields = line.Split( sep );
				entry.condition = fields[0].Trim();
				entry.score = double.Parse( fields[1] );
				pos.entries.Add( entry );
				line = rd.ReadUnixLine();
			}
			if( line != null )
				line = rd.ReadUnixLine();
			m_pssm.Add( pos );
		}
		rd.Close();
	}*/
	
	public void Load( string filename ) {
		UnixCfg rd = new UnixCfg( filename );
		char[] sep = new char[]{ ' ', '\t' };
		string[] fields;
		PSSMentry entry;
		
		string line = rd.ReadUnixLine();
		if( line == null ) {
			rd.Close();
			return;
		}
		fields = line.Split( sep );
		PSSMposition[] pos = new PSSMposition[fields.Length-1];
		for( uint i = 0; i < pos.Length; i++ ) {
			pos[i].order = i;
			pos[i].entries = new List<PSSMentry>();
		}
		do {
			fields = line.Split( sep );
			entry.condition = fields[0].Trim();
			for( uint i = 0; i < pos.Length; i++ ) {
				entry.score = double.Parse( fields[i+1] );
				pos[i].entries.Add( entry );
			}
			line = rd.ReadUnixLine();
		} while( line != null );
		rd.Close();
		
		m_pssm.AddRange( pos );
	}
	
	public void Normalize() {
		int i, j;
		double max;
		PSSMentry tmp;
		
		for( i = 0; i < m_pssm.Count; i++ ) {
			max = m_pssm[i].entries[0].score;
			for( j = 1; j < m_pssm[i].entries.Count; j++ )
				if( max < m_pssm[i].entries[j].score )
					max = m_pssm[i].entries[j].score;
			for( j = 0; j < m_pssm[i].entries.Count; j++ ) {
				tmp = m_pssm[i].entries[j];
				tmp.score -= max;
				m_pssm[i].entries[j] = tmp;
			}
		}
	}
	
	public double GetScore( WregexResult w ) {
		if( m_pssm.Count == 0 )
			return 0.0;
			
		double score = 0.0;
		int i, j, k, len;
		for( i = 0; i < w.Groups.Count; i++ ) {
			for( j = 0; j < m_pssm.Count && m_pssm[j].order != i; j++ ) {
				if( m_pssm[j].order >= w.Groups.Count )
					throw new ApplicationException( "Regex and PSSM have diferent lengths" );
			}
			if( j == m_pssm.Count )
				continue;
			//Console.WriteLine( "DEBUG: " + w.Groups[i] + ", " + w.Groups[i].Length );
			len = w.Groups[i].Length;
			for( k = 0; k < len; k++ )
				score += GetScore(m_pssm[j], AminoAcid.Get((w.Groups[i])[k]));///len;
		}
			
		return Math.Pow(10,score/w.Match.Length)*100;
	}
	
	private double GetScore( PSSMposition pos, AminoAcid aa ) {
		foreach( PSSMentry entry in pos.entries ) {
			if( entry.condition.Length == 1 ) {
				if( entry.condition.CompareTo(aa.Letter.ToString())==0 )
					return entry.score;
				else
					continue;
			}
			switch( entry.condition.ToLower() ) {
				case "acidic":
					if( aa.Acidic ) return entry.score;
					break;
				case "basic":
					if( aa.Basic ) return entry.score;
					break;
				case "hydrophobic":
					if( aa.Hydrophobic ) return entry.score;
					break;
				case "polar":
					if( aa.Polar ) return entry.score;
					break;
				case "charge":
					if( aa.Charge != 0 ) return entry.score;
					break;
				case "positive":
					if( aa.Positive ) return entry.score;
					break;
				case "negative":
					if( aa.Negative ) return entry.score;
					break;
				case "aromatic":
					if( aa.Aromatic ) return entry.score;
					break;
				case "aliphatic":
					if( aa.Aliphatic ) return entry.score;
					break;
				default:
					throw new ApplicationException( "Undefined PSSM condition: " + entry.condition );
			}
		}
		return 0.0;
	}
	
	private List<PSSMposition> m_pssm;
}

}	// namespace wregex