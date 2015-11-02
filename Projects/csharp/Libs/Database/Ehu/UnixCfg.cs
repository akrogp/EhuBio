// $Id$
// 
// Unix.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      UnixCfg.cs
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

namespace EhuBio.Database.Ehu {

public class UnixCfg {
	public UnixCfg() {
	}
	
	public UnixCfg( string filename ) {
		m_rd = new StreamReader( filename );
	}
	
	public UnixCfg( TextReader rd ) {
		m_rd = rd;
	}
	
	public void Close() {
		if( m_rd != null )
			m_rd.Close();
	}
	
	public string ReadUnixLine() {
		return ReadUnixLine( m_rd );
	}
	
	public static string ReadUnixLine( TextReader rd ) {
		string line;
		char[] spaces = { ' ', '\t', '\r', '\n' };
		
		while( rd.Peek() >= 0 ) {
			line = rd.ReadLine();
			line = line.Trim( spaces );
			if( line.Length == 0 || line[0] == '#' )
				continue;
			return line;
		};
		
		return null;
	}
	
	private TextReader m_rd = null;
}

}	// namespace EhuBio.Database.Ehu