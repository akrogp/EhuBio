// $Id$
// 
// UniprotXml.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      UniprotXml.cs
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
using System.IO.Compression;
using System.Xml;
using System.Xml.Serialization;

namespace EhuBio.Database.Ebi {

public class UniprotXml {
	public UniprotXml() {
		mSerializer = new XmlSerializer(typeof(Xml.entry));
	}	
	
	public UniprotXml( string path ) : this() {
		Open ( path );
	}
	
	public void Open( String path ) {		
		mXml = new XmlTextReader(new GZipStream(new FileStream(path,FileMode.Open), CompressionMode.Decompress));
	}
	
	public void Close() {
		mXml.Close();
	}
	
	public Xml.entry ReadEntry() {
		if( !mXml.ReadToFollowing("entry") )
			return null;
		Xml.entry e;
		String s = "<entry xmlns=\"http://uniprot.org/uniprot\">" + mXml.ReadInnerXml() + "</entry>";
		StringReader r = new StringReader(s);
		e = (Xml.entry)mSerializer.Deserialize(r);
		return e;
	}
	
	private XmlTextReader mXml;
	private XmlSerializer mSerializer;
}

}	// namespace EhuBio.Database.Ebi