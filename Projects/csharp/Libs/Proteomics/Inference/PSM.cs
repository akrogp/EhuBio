// $Id$
// 
// PSM.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      PSM.cs
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

namespace EhuBio.Proteomics.Inference {

public class PSM {
	/// <summary>
	/// The ID.
	/// </summary>
	public int ID;

	/// <summary>
	/// The charge state.
	/// </summary>
	public int Charge = 0;
	
	/// <summary>
	/// The experimental mass to charge ratio.
	/// </summary>
	public double Mz = -1.0;
	
	/// <summary>
	/// The PSM rank.
	/// </summary>
	public int Rank = 0;
	
	/// <summary>
	/// The score.
	/// </summary>
	public double Score = -1.0;
		
	/// <summary>
	/// The type of the score.
	/// </summary>
	public string ScoreType = "N/A";
	
	/// <summary>
	/// The peptide matched.
	/// </summary>
	public Peptide Peptide;
	
	/// <summary>
	/// The original spectrum.
	/// </summary>
	public Spectrum Spectrum;
	
	/// <summary>
	/// The confidence.
	/// </summary>
	public EhuBio.Proteomics.Inference.Peptide.ConfidenceType Confidence = Peptide.ConfidenceType.PassThreshold;
	
	/// <summary>
	/// True if this PSM is considered valid.
	/// </summary>
	public bool passThreshold = true;
	
	public override string ToString() {
		return Mz+"("+Charge+") " + Score + "(" + ScoreType + ")->" + Confidence + " " + Spectrum;
	}
}

}	// namespace EhuBio.Proteomics.Inference