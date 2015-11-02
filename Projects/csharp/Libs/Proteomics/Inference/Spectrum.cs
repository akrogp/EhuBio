// $Id$
// 
// Spectrum.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      Spectrum.cs
//  
// Copyright (c) 2014 Gorka Prieto
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

public class Spectrum {
	/// <summary>
	/// The ID.
	/// </summary>
	public int ID;

	/// <summary>
	/// The spectra data file.
	/// </summary>
	public string File;
	
	/// <summary>
	/// The unique identifier for the spectrum in the spectra data set.
	/// </summary>
	public string SpectrumID;
	
	/// <summary>
	/// Returns a <see cref="System.String"/> that represents the current <see cref="EhuBio.Proteomics.Inference.Spectrum"/>.
	/// </summary>
	/// <returns>
	/// A <see cref="System.String"/> that represents the current <see cref="EhuBio.Proteomics.Inference.Spectrum"/>.
	/// </returns>
	public override string ToString() {
		return SpectrumID+"@"+File;
	}
	
	/// <summary>
	/// The list of PSMs.
	/// </summary>
	public List<PSM> Psm;
}

}	// namespace EhuBio.Proteomics.Inference