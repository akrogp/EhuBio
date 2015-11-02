// $Id$
// 
// mzId1_2.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      mzId1_2.cs
//  
// Copyright (c) 2013 Gorka Prieto
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
using EhuBio.Proteomics.Hupo.mzIdentML;
using EhuBio.Proteomics.Hupo.mzIdentML1_1;

namespace EhuBio.Proteomics.Inference {

public class mzId1_2 : mzId1_1 {
	public mzId1_2(Mapper.Software sw) : base(sw) {
		m_Type = Mapper.SourceType.mzIdentML120;
	}
	
	public override string ParserName {
		get {
			return "PSI-PI mzIdentML (v1.2)";
		}
	}
	
	protected override List<ProteinAmbiguityGroupType> BuildProteinDetectionList() {
		List<ProteinAmbiguityGroupType> groups = BuildPags();
		int count = AddPassThreshold(groups);
		CVParamType cv = new CVParamType();
		cv.cvRef = "PSI-MS";
		cv.accession = "MS:1002404";
		cv.name = "count of identified proteins";
		cv.value = count.ToString();
		m_mzid.Data.DataCollection.AnalysisData.ProteinDetectionList.Items = new AbstractParamType[]{cv};
		return groups;
	}		
	
	private List<ProteinAmbiguityGroupType> BuildPags() {
		int gid = 1;
		SortedList<string,ProteinDetectionHypothesisType> listPdh = new SortedList<string, ProteinDetectionHypothesisType>();
		SortedList<string,ProteinAmbiguityGroupType> listPag = new SortedList<string, ProteinAmbiguityGroupType>();
		List<ProteinAmbiguityGroupType> groups = new List<ProteinAmbiguityGroupType>();
		
		// Collect existing PDHs
		foreach( ProteinAmbiguityGroupType grp in m_mzid.Data.DataCollection.AnalysisData.ProteinDetectionList.ProteinAmbiguityGroup )
			foreach( ProteinDetectionHypothesisType pdh in grp.ProteinDetectionHypothesis )
				if( !listPdh.ContainsKey(pdh.dBSequence_ref) )
					listPdh.Add( pdh.dBSequence_ref, pdh );
		
		// Build PAGs for every group except non-conclusive proteins		
		foreach( Protein p in Proteins ) {
			if( p.Evidence == Protein.EvidenceType.NonConclusive || p.Evidence == Protein.EvidenceType.Filtered )
				continue;			
			ProteinAmbiguityGroupType g = new ProteinAmbiguityGroupType();
			g.id = "PAG_" + gid++;
			if( p.Subset.Count == 0 ) {
				g.ProteinDetectionHypothesis = new ProteinDetectionHypothesisType[]{listPdh[p.DBRef]};
				UpdatePdhCvs(g.ProteinDetectionHypothesis[0],p.Evidence);
				listPag.Add(g.ProteinDetectionHypothesis[0].dBSequence_ref,g);
			} else {
				List<ProteinDetectionHypothesisType> tmp = new List<ProteinDetectionHypothesisType>();
				foreach( Protein p2 in p.Subset ) {
					ProteinDetectionHypothesisType pdh = listPdh[p2.DBRef];
					UpdatePdhCvs(pdh,p2.Evidence);
					tmp.Add( pdh );
					listPag.Add(pdh.dBSequence_ref,g);
				}
				g.ProteinDetectionHypothesis = tmp.ToArray();
			}
			groups.Add( g );
		}
		
		// Include non-conclusive proteins in existing PAGs
		bool include;
		foreach( Protein p in Proteins ) {
			if( p.Evidence != Protein.EvidenceType.NonConclusive )
				continue;
			ProteinDetectionHypothesisType pdh = listPdh[p.DBRef];
			UpdatePdhCvs(pdh, Protein.EvidenceType.NonConclusive);
			foreach( Peptide f in p.Peptides )
				foreach( Protein t in f.Proteins ) {
					include = true;
					if( !listPag.ContainsKey(t.DBRef) )
						continue;
					ProteinAmbiguityGroupType g = listPag[t.DBRef];					
					foreach( ProteinDetectionHypothesisType h in g.ProteinDetectionHypothesis )
						if( GetBaseId(h) == GetBaseId(pdh) ) {
							include = false;
							break;
						}
					if( include ) {
						ProteinDetectionHypothesisType clon = ClonePDH(pdh);
						clon.id = GetBaseId(clon)+"@"+g.id;
						List<ProteinDetectionHypothesisType> tmp =
							new List<ProteinDetectionHypothesisType>(g.ProteinDetectionHypothesis);
						tmp.Add(clon);
						g.ProteinDetectionHypothesis = tmp.ToArray();
					}
				}
		}
		
		return groups;
	}
	
	private String GetBaseId( ProteinDetectionHypothesisType pdh ) {
		int i = pdh.id.IndexOf('@');
		return i < 0 ? pdh.id : pdh.id.Substring(0,i);
	}
	
	private ProteinDetectionHypothesisType ClonePDH( ProteinDetectionHypothesisType pdh ) {
		ProteinDetectionHypothesisType clon = new ProteinDetectionHypothesisType();
		clon.id = pdh.id;
		clon.name = pdh.name;
		clon.dBSequence_ref = pdh.dBSequence_ref;
		clon.passThreshold = pdh.passThreshold;
		clon.PeptideHypothesis = pdh.PeptideHypothesis;
		clon.Items = pdh.Items;
		return clon;
	}
	
	private void UpdatePdhCvs( ProteinDetectionHypothesisType pdh, Protein.EvidenceType evidence ) {
		List<AbstractParamType> list = new List<AbstractParamType>();
		foreach( AbstractParamType item in pdh.Items )
			if( !item.name.Contains("PAnalyzer") && !item.name.Contains("leading") )
				list.Add( item );
		CVParamType ev = new CVParamType(), ld = new CVParamType();
		ev.cvRef = "PSI-MS";
		ld.cvRef = "PSI-MS";
		switch( evidence ) {
			case Protein.EvidenceType.Conclusive:
				ev.accession = "MS:1002213";
				ev.name = "PAnalyzer:conclusive protein";
				ld.accession = "MS:1002401";
				ld.name = "leading protein";
				break;
			case Protein.EvidenceType.Indistinguishable:
				ev.accession = "MS:1002214";
				ev.name = "PAnalyzer:indistinguishable protein";
				ld.accession = "MS:1002401";
				ld.name = "leading protein";
				break;
			case Protein.EvidenceType.Group:
				ev.accession = "MS:1002216";
				ev.name = "PAnalyzer:ambiguous group member";
				ld.accession = "MS:1002401";
				ld.name = "leading protein";
				break;
			case Protein.EvidenceType.NonConclusive:
				ev.accession = "MS:1002215";
				ev.name = "PAnalyzer:non-conclusive protein";
				ld.accession = "MS:1002402";
				ld.name = "non-leading protein";
				break;
			default:	// filtered
				return;
		}
		list.Add(ld);
		list.Add(ev);		
		pdh.Items = list.ToArray();
	}	

	private int AddPassThreshold( List<ProteinAmbiguityGroupType> groups ) {
		int count = 0;
		CVParamType cv;
		bool pass;
		
		foreach( ProteinAmbiguityGroupType pag in groups ) {
			cv = new CVParamType();
			cv.cvRef = "PSI-MS";
			cv.accession = "MS:1002415";
			cv.name = "protein group passes threshold";
			pass = false;
			foreach( ProteinDetectionHypothesisType pdh in pag.ProteinDetectionHypothesis ) {
				if( !pdh.passThreshold )
					continue;
				foreach( AbstractParamType item in pdh.Items )
					if( item.name == "leading protein" ) {
						pass = true;
						break;
					}
				if( pass )
					break;
			}
			if( pass ) {				
				count++;
				cv.value = "true";
			} else
				cv.value = "false";
			pag.Items = new AbstractParamType[]{cv};
		}
		
		return count;
	}
}

}	// namespace EhuBio.Proteomics.Inference