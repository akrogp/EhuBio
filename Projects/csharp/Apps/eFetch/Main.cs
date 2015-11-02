// $Id$
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
using EhuBio.Database.Ncbi.eFetch.Sequences;

class MainClass {
	public static int Main( string[] args ) {
		if( args.Length != 2 ) {
			Console.WriteLine( "Usage:\n\teFetch <db> <id>" );
			return 1;
		}
		string db = args[0];
		string id = args[1];
		
		//Console.WriteLine( "Retrieving \"" + id + "\" from NCBI \"" + db + "\" ..." );
		try {
			eFetchSequenceService eFetch = new eFetchSequenceService();		
			MessageEFetchRequest req = new MessageEFetchRequest();
			req.db = db;
			req.id = id;
			MessageEFetchResult res = eFetch.run_eFetch( req );
			foreach( GBSeq seq in res.GBSet ) {
				//Console.WriteLine( ">lcl|" + id + "|gnl|" + db + '|' + seq.GBSeq_accessionversion + ' ' + seq.GBSeq_definition );
				Console.WriteLine( ">gnl|" + db + '|' + seq.GBSeq_accessionversion + ' ' + seq.GBSeq_definition );
				Console.WriteLine( seq.GBSeq_sequence + '\n' );
			}
		} catch( Exception e ) {
			Console.WriteLine( "Error: " + e.Message );
			return 1;
		}
		
		return 0;
	}
}