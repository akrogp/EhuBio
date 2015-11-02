// $Id: Mapper.cs 172 2014-06-03 14:44:57Z gorka.prieto@gmail.com $
// 
// Mapper.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      Mapper.cs
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
using System.Xml;
using EhuBio.UI.Html;

namespace EhuBio.Proteomics.Inference {

/// <summary>
/// Infers proteins from peptide identifications
/// </summary>
public abstract class Mapper {
	/// <summary>
	/// Supported input file types.
	/// </summary>
	public enum SourceType { Unknown, Plgs, mzIdentML100, mzIdentML110, mzIdentML120 };

	/// <summary>
	/// Counts
	/// </summary>
	public struct StatsStruct {
		public int Peptides;
		public int Red, Yellow, Green;
		public int MaxProteins;
		public int Conclusive;
		public int NonConclusive;
		public int Groups, Grouped;
		public int IGroups, Indistinguisable;
		public int Filtered;
	}

	/// <summary>
	/// Software information.
	/// </summary>
	public struct Software {	
		public string Name;
		public string Version;
		public string License;
		public string Copyright;
		public string Contact;
		public string Customizations;
		public string Url;
		public override string ToString() {
			return Name + " (v" + Version + ")";
		}
	}
	
	/// <summary>
	/// Create a new mapper based on the XML file type and version.
	/// </summary>
	public static Mapper Create( string xml, Software sw ) {
		const int size = 2000;
		char[] buffer = new char[size];
		TextReader tr = new StreamReader( xml );
		tr.ReadBlock( buffer, 0, size );
		tr.Close();
		
		// PLGS
		string str = new string(buffer);
		if( str.Contains("GeneratedBy") )
			return new Plgs(sw);
		
		// mzIdentML 1.1 and 1.2
		int i = str.IndexOf( "MzIdentML" );
		string version;
		if( i != -1 ) {
			str = str.Remove(0,i);
			i = str.IndexOf( "version" );
			if( i == -1 )
				throw new ApplicationException( "mzIdentML version not provided" );
			str = str.Remove(0,i);
			version = str.Split(new char[]{'"'})[1];
			switch( version ) {
				case "1.1.0":
					return new mzId1_1(sw);
				case "1.2.0":
					return new mzId1_2(sw);
			}
			throw new ApplicationException( "mzIdentML version '" + version + "' not supported" );
		}
		
		// mzIdentML 1.0
		str = new string(buffer);
		i = str.IndexOf( "mzIdentML" );
		if( i == -1 )
			throw new ApplicationException( "Identification file format not supported" );
		str = str.Remove(0,i);
		i = str.IndexOf( "version" );
		if( i == -1 )
			throw new ApplicationException( "mzIdentML version not provided" );
		str = str.Remove(0,i);
		version = str.Split(new char[]{'"'})[1];
		switch( version ) {
			case "1.0.0":
				return new mzId1_0(sw);
		}
		throw new ApplicationException( "mzIdentML version '" + version + "' not supported" );
	}

	/// <summary>
	/// Default constructor
	/// </summary>
	public Mapper( Software sw ) {
		m_Software = sw;
		Proteins = new List<Protein>();
		Peptides = new List<Peptide>();
		Spectra = new List<Spectrum>();
		m_Stats = new StatsStruct();
		m_Format = new System.Globalization.NumberFormatInfo();
		m_Format.NumberDecimalSeparator = ".";
		m_Run = 0;
		m_Type = SourceType.Unknown;
		LengthThreshold = 0;
		PlgsThreshold = Peptide.ConfidenceType.NoThreshold;
		SeqThreshold = Peptide.ConfidenceType.NoThreshold;
		XTandemThreshold = 0.05;
		XTandemAvailable = false;
		MascotThreshold = 0.05;
		MascotAvailable = false;
		RequirePassTh = true;
		RankThreshold = 0;
		FilterDecoys = true;
		RunsThreshold = 1;
		m_InputFiles = new List<string>();
	}
	
	/// <summary>
	/// Parser type.
	/// </summary>
	/// <returns>
	/// The name.
	/// </returns>
	public abstract string ParserName { get; }
	
	/// <summary>
	/// Loads data from a peptide identification file
	/// </summary>
	/// <param name="merge">
	/// A <see cref="System.Boolean"/> indicating wether merge the new file with the existing data
	/// </param>
	public void Load( string path, bool merge ) {
		if( !merge || m_Run == 0 ) {
			m_InputFiles.Clear();
			Proteins.Clear();
			Peptides.Clear();
			Spectra.Clear();
			m_gid = 1;
			m_SortedProteins = new SortedList<string, Protein>();
			m_Run = 1;
		} else
			m_Run++;
		Load( path );
		m_InputFiles.Add( Path.GetFileName(path) );
	}
	
	/// <summary>
	/// Override to support different protein identification input file formats
	/// </summary>
	protected abstract void Load( string path );
	
	
	/// <summary>
	/// Saves results to a CSV file. Override to support other output file formats
	/// </summary>
	public virtual void Save( string fpath ) {
		SaveCSV( Path.ChangeExtension(fpath,".csv"), ":" );
		SaveHtml( Path.ChangeExtension(fpath,".html"), Path.GetFileNameWithoutExtension(fpath) );
	}
	
	/// <summary>
	/// Saves results to a CSV file.
	/// </summary>
	public void SaveCSV( string fpath, string sep ) {
		TextWriter w = new StreamWriter( fpath );
		w.WriteLine( "ID"+sep+"Entry"+sep+"Accession"+sep+"Evidence"+sep+"Group"+sep+"Description"+sep+"Peptides"+sep+"Sequence"+sep+"PTMs" );
		foreach( Protein p in Proteins ) {
			if( p.Subset.Count == 0 )
				SaveCSVEntry( w, p, "", sep );
			else
				foreach( Protein p2 in p.Subset )
					SaveCSVEntry( w, p2, p.Entry, sep );
		}
		w.Close();
		Notify( "Saved to " + fpath );
	}
	
	private void SaveCSVEntry( TextWriter w, Protein p, string grp, string sep ) {
		w.Write( p.ID + sep + p.Entry + sep + p.Accession + sep + p.Evidence.ToString() + sep + grp + sep + p.Desc + sep );
		foreach( Peptide f in p.Peptides )
			w.Write(f.ToString() + ' ');
		w.Write( sep + p.Sequence + sep );
		String ptm;
		foreach( Peptide peptide in p.Peptides ) {
			ptm = GetPtmCsv(peptide);
			if( ptm.Length != 0 )
				w.Write(ptm+" ");
		}
		w.WriteLine();
	}
	
	private String GetPtmCsv (Peptide peptide) {
		String result = "";
		List<PTM> ptms = new List<PTM>();
		foreach( List<PTM> variant in peptide.Variants )
			foreach( PTM ptm in variant )
				if( !ptms.Contains(ptm) ) {
					ptms.Add(ptm);
					result += peptide.ToString()+"/"+ptm.ToString()+" ";
				}
		return result;
	}
	
	#region HTML output
	
	/// <summary>
	/// Saves results to a HTML file.
	/// </summary>
	public void SaveHtml( string fpath, string name ) {
		TextWriter w = new StreamWriter(fpath);
		string title = "PAnalyzer: Protein identification report for " + name;
		Tag tr = new Tag( "tr", true );
		Tag td = new Tag( "td" );
		Tag th = new Tag( "th" );
		Tag a = new Tag( "a", "href" );
		
		#region Head
		w.WriteLine( "<html>\n<head>" );
		w.WriteLine( "<title>" + title + "</title>"  );
		w.WriteLine( "<style>" );
		w.WriteLine( "body { padding: 0px; margin: 20px; }" );
		w.WriteLine( "caption, .caption { font-size: 120%; color: darkgreen; text-align: left; }" );
		w.WriteLine( "th, td { padding-left: 2px; padding-right: 2px; }" );
		w.WriteLine( "th { text-align: left; }" );
		w.WriteLine( "tr.odd { background-color: #e0e0e0; }" );
		w.WriteLine( "tr.even { background-color: #fefefe; }" );
		w.WriteLine( "table { border: 2px black solid; border-collapse: collapse; }" );
		//w.WriteLine( "table { table-layout: fixed; }" );
		w.WriteLine( "</style>" );
		w.WriteLine( "<body>" );
		w.WriteLine( "<a name=\"top\"/><h2>" + title + "</h2><hr/>" );
		#endregion
		
		#region Index
		w.WriteLine( "<div class=\"caption\"><a name=\"index\"/>Content:</div>" );
		w.WriteLine( "<ol>" );
		w.WriteLine( "<li><a class=\"caption\" href=\"#config\">Analysis Configuration</a>" );
		w.WriteLine( "<li><a class=\"caption\" href=\"#summary\">Protein Summary</a>" );
		w.WriteLine( "<li><a class=\"caption\" href=\"#proteins\">Protein List</a>" );
		w.WriteLine( "<li><a class=\"caption\" href=\"#details\">Protein Details</a>" );
		if( Spectra.Count != 0 )
			w.WriteLine( "<li><a class=\"caption\" href=\"#spectra\">Spectra Details</a>" );
		w.WriteLine( "</ol><br/>" );
		#endregion
		
		#region Configuration
		w.WriteLine( "<table>\n<caption><a name=\"config\"/>Analysis Configuration</caption>" );
		//w.WriteLine( "<col width=\"150px\"/><col width=\"300px\"/>" );
		w.WriteLine( tr.Render(th.Render("Software")+td.Render(a.Render(m_Software.Url,m_Software.ToString()))) );
		w.WriteLine( tr+"\n"+th.Render("Analysis type") );
		if( m_InputFiles.Count == 1 ) {
			w.Write( td.Render("Single run analysis")+tr );
			w.WriteLine( tr.Render(th.Render("Input file")+td.Render(m_InputFiles[0])) );
		}
		else {
			w.WriteLine( td.Render("Multirun analysis")+tr );
			w.WriteLine( tr.Render(th.Render("Number of runs")+td.Render(m_InputFiles.Count.ToString())) );
			w.WriteLine( tr.Render(th.Render("Runs threshold")+td.Render(RunsThreshold.ToString())) );
			w.WriteLine( tr+th.Render("Input files")+"\n<td>" );
			foreach( string f in m_InputFiles )
				w.WriteLine( f+"<br/>" );
			w.WriteLine( "</td>"+tr );
		}
		w.WriteLine( tr.Render(th.Render("Input file type")+td.Render(ParserName)) );		
		if( Type == SourceType.Plgs )
			w.WriteLine( tr.Render(th.Render("PLGS peptide threshold")+td.Render(PlgsThreshold.ToString())) );
		else {
			w.WriteLine( tr.Render(th.Render("SpectrumIdentificationItem passThreshold")+td.Render(RequirePassTh.ToString())) );
			w.WriteLine( tr.Render(th.Render("SpectrumIdentificationItem rank threshold")+td.Render(RankThreshold.ToString())) );
			w.WriteLine( tr.Render(th.Render("PeptideEvidence isDecoy")+td.Render(FilterDecoys?"Filter":"Ignore")) );
			if( Type >= SourceType.mzIdentML110 && Type <= SourceType.mzIdentML120 ) {
				if( SeqThreshold != Peptide.ConfidenceType.NoThreshold )
					w.WriteLine( tr.Render(th.Render("ProteomeDiscoverer/SEQUEST xcorr PSM threshold")+td.Render(SeqThreshold.ToString())) );
			}
		}
		w.WriteLine( "</table><br/>" );
		#endregion
		
		#region Summary
		w.WriteLine( "<table>\n<caption><a name=\"summary\"/>Protein Summary</caption>" );
		//w.WriteLine( "<col width=\"150px\"/><col width=\"300px\"/>" );
		w.WriteLine( tr.Render(th.Render("Maximum")+td.Render(Stats.MaxProteins.ToString())) );
		w.WriteLine( tr.Render(th.Render("Conclusive")+td.Render(Stats.Conclusive.ToString())) );
		w.WriteLine( tr.Render(th.Render("Indistinguishable")+
			td.Render(Stats.Indistinguisable==0?"0":Stats.Indistinguisable.ToString()+" in "+Stats.IGroups+
				(Stats.IGroups==1?" group":" groups"))) );
		w.WriteLine( tr.Render(th.Render("Ambiguous groups")+
			td.Render(Stats.Grouped==0?"0":Stats.Grouped.ToString()+" in "+Stats.Groups+
				(Stats.Groups==1?" group":" groups"))) );
		w.WriteLine( tr.Render(th.Render("Non conclusive")+td.Render(Stats.NonConclusive.ToString())) );
		w.WriteLine( tr.Render(th.Render("Filtered")+td.Render(Stats.Filtered.ToString())) );
		w.WriteLine( "</table><br/>" );
		#endregion
		
		#region Protein List
		tr.Reset();
		w.WriteLine( "<table>\n<caption><a name=\"proteins\"/>Protein List</caption>" );
		//w.WriteLine( "<col width=\"10%\"/><col width=\"10%\"/><col width=\"10%\"/><col width=\"20%\"/><col width=\"50%\"/>" );
		w.WriteLine( tr.Render("<th>Name</th><th>Evidence</th><th colspan=\"2\" width=\"40%\">"+
			"Peptide list (unique, discriminating*, non-discriminating**)</th><th>Description</th>") );
		WriteProteinList( w, tr, Protein.EvidenceType.Conclusive );
		WriteProteinList( w, tr, Protein.EvidenceType.Indistinguishable );
		WriteProteinList( w, tr, Protein.EvidenceType.Group );
		WriteProteinList( w, tr, Protein.EvidenceType.NonConclusive );
		WriteProteinList( w, tr, Protein.EvidenceType.Filtered );
		w.WriteLine( "</table><br/>" );
		#endregion
		
		#region Details
		w.WriteLine( "<hr/><a name=\"details\"/>" );
		WriteProteinDetails( w, Protein.EvidenceType.Conclusive );
		WriteProteinDetails( w, Protein.EvidenceType.Indistinguishable );
		WriteProteinDetails( w, Protein.EvidenceType.Group );
		WriteProteinDetails( w, Protein.EvidenceType.NonConclusive );
		//WriteProteinDetails( w, Protein.EvidenceType.Filtered );
		#endregion
		
		#region Details
		if( Spectra.Count != 0 ) {
			w.WriteLine( "<hr/><a name=\"spectra\"/>" );
			WriteSpectraDetails( w );
		}
		#endregion
		
		w.WriteLine( "</body>\n</html>" );
		w.Close();
		
		Notify( "Saved to " + fpath );
	}
	
	private void WriteProteinList( TextWriter w, Tag tr, Protein.EvidenceType evidence ) {
		Tag a = new Tag( evidence == Protein.EvidenceType.Filtered ? null : "a", "href" );
		Tag td = new Tag( "td" );
		Tag tdr = new Tag( "td", "rowspan" );
		Tag tdc = new Tag( "td", "colspan" );
		
		foreach( Protein p in Proteins ) {
			if( p.Evidence != evidence )
				continue;
			if( p.Subset.Count == 0 ) {
				w.Write( tr.Render(
					td.Render(a.Render("#"+p.Accession,p.EntryEx))+
					td.Render(p.Evidence.ToString())+
					tdc.Render("2",Peptides2Html(p))+
					td.Render(p.Desc) ));
				continue;
			}
			w.WriteLine( tr.Render(
				tdr.Render(p.Subset.Count.ToString(),p.Entry)+
				tdr.Render(p.Subset.Count.ToString(),p.Evidence.ToString())+
				td.Render(a.Render("#"+p.Subset[0].Accession,p.Subset[0].EntryEx)+": ")+
				td.Render(Peptides2Html(p.Subset[0]))+
				td.Render(p.Subset[0].Desc) ));
			tr.Hold = true;
			for( int i = 1; i < p.Subset.Count; i++ )
				w.WriteLine( tr.Render(
					td.Render(a.Render("#"+p.Subset[i].Accession,p.Subset[i].EntryEx)+": ")+
					td.Render(Peptides2Html(p.Subset[i]))+
					td.Render(p.Subset[i].Desc) ));
			tr.Hold = false;
		}
	}
	
	private string Peptides2Html( Protein p ) {
		if( p.Peptides.Count == 0 )
			return "";
		int i;
		Tag a = new Tag( "a", "href" );
		string str = "";
		for( i = 0; i < p.Peptides.Count-1; i++ )
			str += a.Render("#"+p.Accession+"__"+p.Peptides[i].ID,p.Peptides[i].ToString()) + ", ";
		str += a.Render("#"+p.Accession+"__"+p.Peptides[i].ID,p.Peptides[i].ToString());
		return str;
	}
	
	private void WriteProteinDetails( TextWriter w, Protein.EvidenceType evidence ) {
		foreach( Protein p in Proteins ) {
			if( p.Evidence != evidence )
				continue;
			if( p.Subset.Count == 0 ) {
				WriteProteinDetails( w, p );
				continue;
			}
			foreach( Protein p2 in p.Subset )
				WriteProteinDetails( w, p2 );
		}
	}
	
	private void WriteProteinDetails( TextWriter w, Protein p ) {
		Tag tr = new Tag( "tr", true );
		Tag td = new Tag( "td", "colspan" );
		Tag th = new Tag( "th", "rowspan" );
		Tag a = new Tag( "a", "href" );
		int i;
		
		w.WriteLine( "<table>\n<caption><a name=\""+p.Accession+"\"/>Protein "+p.Accession+"</caption>" );
		//w.WriteLine( "<col width=\"10%\"/><col width=\"5%\"/><col width=\"10%\"/><col width=\"75%\"/>" );
		w.WriteLine( tr.Render(th.Render("Name")+td.Render("3",p.EntryEx)) );
		w.WriteLine( tr.Render(th.Render("Description")+td.Render("3",p.Desc)) );
		w.WriteLine( tr.Render(th.Render("Sequence")+td.Render("3","<pre>"+p.ParseSeq(10)+"</pre>")) );
		w.WriteLine( tr.Render(th.Render("Evidence")+td.Render("3",p.Evidence.ToString())) );
		w.Write( tr+th.Render("Peptide list")+"<td colspan=\"3\">" );
		if( p.Peptides.Count > 0 ) {
			for( i = 0; i < p.Peptides.Count-1; i++ )
				w.Write( a.Render("#"+p.Accession+"__"+p.Peptides[i].ID,p.Peptides[i].ToString()) + ", " );
			w.Write( a.Render("#"+p.Accession+"__"+p.Peptides[i].ID,p.Peptides[i].ToString()) );
		}
		w.WriteLine( "</td>"+tr );
		if( p.Peptides.Count == 0 ) {
			w.WriteLine( "</table><br/>" );
			return;
		}
		int rows = 8;
		if( Spectra.Count != 0 )
			rows++;
		w.Write( tr+th.Render((p.Peptides.Count*rows).ToString(),"Peptides") );
		bool first = true;
		foreach( Peptide f in p.Peptides ) {
			if( first )
				first = false;
			else
				w.Write( tr.ToString() );
			w.Write( th.Render(rows.ToString(),f.ToString()) );
			w.WriteLine( th.Render("<a name=\""+p.Accession+"__"+f.ID+"\"/>Confidence")+td.Render(f.Confidence.ToString())+tr );
			tr.Hold = true;
			w.WriteLine( tr.Render(th.Render("Decoy")+td.Render(f.Decoy.ToString())) );
			w.Write( tr+th.Render("Runs")+td );
			for( i = 0; i < f.Runs.Count-1; i++ )
				w.Write( f.Runs[i].ToString() + ", " );
			w.WriteLine( f.Runs[i].ToString()+td+tr );
			w.WriteLine( tr.Render(th.Render("Relation")+td.Render(f.Relation.ToString())) );
			w.Write( tr+th.Render("Proteins")+td );
			for( i = 0; i < f.Proteins.Count-1; i++ )
				w.Write( a.Render("#"+f.Proteins[i].Accession,f.Proteins[i].EntryEx) + ", " );
			w.WriteLine( a.Render("#"+f.Proteins[i].Accession,f.Proteins[i].EntryEx)+td.ToString()+tr );
			w.WriteLine( tr.Render(th.Render("Sequence")+td.Render("<pre>"+f.Sequence+"</pre>")) );
			w.WriteLine( tr.Render(th.Render("Position")+td.Render(f.GetPositions(p))) );
			w.Write( tr+th.Render("PTMs")+td );
			if( f.Variants.Count == 1 )
				w.Write( Peptide.Variant2Str(f.LastVariant) );
			else {
        		i = 1;
        		foreach( List<PTM> v in f.Variants )
        			w.Write( "Variant #"+(i++)+": "+Peptide.Variant2Str(v)+"<br/>" );
        	}
			w.WriteLine( td.ToString()+tr );
			if( Spectra.Count != 0 ) {
				w.Write( tr+th.Render("PSMs")+td );
				if( f.Psm != null ) {
					for( i = 0; i < f.Psm.Count-1; i++ )
						w.Write( a.Render("#PSM"+f.Psm[i].ID,f.Psm[i].ID.ToString()) + ", " );
					w.WriteLine( a.Render("#PSM"+f.Psm[i].ID,f.Psm[i].ID.ToString()) );
				}
				w.WriteLine( td.ToString()+tr );
			}
			tr.Hold = false;
		}
		w.WriteLine( "</table><br/>" );
	}
	
	private void WriteSpectraDetails( TextWriter w ) {
		foreach( Spectrum s in Spectra )
			if( s.Psm != null && s.Psm.Count > 0 )
				WriteSpectraDetails( w, s );
	}
	
	private void WriteSpectraDetails( TextWriter w, Spectrum s ) {
		Tag tr = new Tag( "tr", true );
		Tag td = new Tag( "td", "colspan" );
		Tag th = new Tag( "th", "rowspan" );
		Tag a = new Tag( "a", "href" );
		int i;
		
		w.WriteLine( "<table>\n<caption><a name=\"Spectrum"+s.ID+"\"/>Spectrum "+s.SpectrumID+"</caption>" );
		w.WriteLine( tr.Render(th.Render("ID")+td.Render("3",s.SpectrumID)) );
		w.WriteLine( tr.Render(th.Render("Location")+td.Render("3",s.File)) );
		w.Write( tr+th.Render("PSM list")+"<td colspan=\"3\">" );
		if( s.Psm.Count > 0 ) {
			for( i = 0; i < s.Psm.Count-1; i++ )
				w.Write( a.Render("#PSM"+s.Psm[i].ID,s.Psm[i].ID.ToString()) + ", " );
			w.Write( a.Render("#PSM"+s.Psm[i].ID,s.Psm[i].ID.ToString()) );
		}
		w.WriteLine( "</td>"+tr );
		if( s.Psm.Count == 0 ) {
			w.WriteLine( "</table><br/>" );
			return;
		}
		w.Write( tr+th.Render((s.Psm.Count*8).ToString(),"PSMs") );
		bool first = true;
		foreach( PSM psm in s.Psm ) {
			if( first )
				first = false;
			else
				w.Write( tr.ToString() );
			w.Write( th.Render("8",psm.ID.ToString()) );			
			w.WriteLine( th.Render("<a name=\"PSM"+psm.ID+"\"/>Charge")+td.Render(psm.Charge.ToString())+tr );
			tr.Hold = true;
			w.WriteLine( tr+th.Render("M/Z")+td.Render(psm.Mz.ToString())+tr );
			w.WriteLine( tr+th.Render("Rank")+td.Render(psm.Rank.ToString())+tr );
			w.WriteLine( tr+th.Render("Score")+td.Render(psm.Score<0.0?"N/A":psm.Score.ToString())+tr );
			w.WriteLine( tr+th.Render("Score type")+td.Render(psm.ScoreType)+tr );
			w.WriteLine( tr+th.Render("Confidence")+td.Render(psm.Confidence.ToString())+tr );
			w.WriteLine( tr+th.Render("PassThreshold")+td.Render(psm.passThreshold.ToString())+tr );
			w.Write( tr+th.Render("Peptide")+td );
			if( psm.Peptide != null ) {
				w.Write( psm.Peptide.ToString() );
				if( psm.Peptide.Proteins.Count > 0 ) {
					w.Write( " (" );
					for( i = 0; i < psm.Peptide.Proteins.Count - 1; i++ )
						w.Write( a.Render("#"+psm.Peptide.Proteins[i].Accession+"__"+psm.Peptide.ID,psm.Peptide.Proteins[i].EntryEx) + ", " );
					w.Write( a.Render("#"+psm.Peptide.Proteins[i].Accession+"__"+psm.Peptide.ID,psm.Peptide.Proteins[i].EntryEx) + ")" );
				}
			} else
				w.Write( "N/A (ProCon duplicate?)" );
			w.WriteLine( td.ToString()+tr);
			tr.Hold = false;
		}
		w.WriteLine( "</table><br/>" );
		w.Flush();
	}
	
	#endregion
	
	/// <summary>
	/// Parses the confidence enum to a string.
	/// </summary>
	public string ParseConfidence( Protein.EvidenceType e ) {
		switch( e ) {
			case Protein.EvidenceType.Conclusive:
				return "conclusive";
			case Protein.EvidenceType.Group:
				return "ambiguous group";
			case Protein.EvidenceType.Indistinguishable:
				return "indistinguishable";
			case Protein.EvidenceType.NonConclusive:
				return "non conclusive";
		}
		return e.ToString();
	}
		
	/// <summary>
	/// Process the loaded data
	/// </summary>
	public void Do() {
		m_Run = 0;
		FilterPsms();
		FilterPeptides();
		ClasifyPeptides();
		ClasifyProteins();
		DoStats();
	}
	
	/// <summary>
	/// Removes peptides with low score, duplicated (same sequence) or not voted (multirun)
	/// </summary>
	private void FilterPeptides() {
		List<Peptide> peptides = new List<Peptide>();
		int id = 1;
		
		// Remove previous relations
		foreach( Protein p in Proteins )
			p.Peptides.Clear();
		
		// Filters duplicated (same sequence) peptides
		SortedList<string,Peptide> SortedPeptides = new SortedList<string, Peptide>();
		foreach( Peptide f in Peptides ) {
			// Low score peptide
			if( !CheckPeptide(f) ) {
				if( f.Psm != null )
					foreach( PSM psm in f.Psm )
						psm.Spectrum.Psm.Remove(psm);
				continue;
			}
			// Duplicated peptide, new protein?
			if( SortedPeptides.ContainsKey(f.Sequence) ) {
				Peptide fo = SortedPeptides[f.Sequence];
				if( (int)f.Confidence > (int)fo.Confidence )
					fo.Confidence = f.Confidence;
				if( !fo.Runs.Contains(f.Runs[0]) )
					fo.Runs.Add(f.Runs[0]);
				fo.AddVariant( f.LastVariant );
				bool dp = false;	// duplicated protein?, needed for PLGS
				foreach( Protein po in fo.Proteins )
					if( po.ID == f.Proteins[0].ID ) {
						dp = true;
						break;
					}
				if( !dp )
					fo.Proteins.Add( f.Proteins[0] );
				if( fo.Psm == null )
					fo.Psm = f.Psm;
				else if( f.Psm != null )
					fo.Psm.AddRange(f.Psm);
				if( fo.Psm != null )
					foreach( PSM psm in fo.Psm )
						psm.Peptide = fo;
			// New peptide
			} else {
				f.ID = id++;
				SortedPeptides.Add( f.Sequence, f );
				peptides.Add( f );
			}
		}
		
		// Vote peptides
		if( RunsThreshold > 1 ) {
			Peptides = new List<Peptide>();
			foreach( Peptide f in peptides )
				if( f.Runs.Count >= RunsThreshold )
					Peptides.Add(f);
		} else
			Peptides = peptides;
		
		// Asigns new peptides to proteins
		foreach( Peptide f in Peptides )
			foreach( Protein p in f.Proteins )
				p.Peptides.Add(f);
	}
	
	/// <summary>
	/// Removes invalid PSMs and their associated peptides (if neccessary).
	/// </summary>
	private void FilterPsms() {
		if( Spectra.Count == 0 )
			return;
		List<Spectrum> spectra = new List<Spectrum>();

		// Remove previous relations
		foreach( Peptide f in Peptides )
			f.Psm = new List<PSM>();
		
		foreach( Spectrum spectrum in Spectra ) {
			if( spectrum.Psm == null )
				continue;
			Spectrum tmp = null;
			foreach( PSM psm in spectrum.Psm ) {
				if( !CheckPsm(psm) )
					continue;
				if( tmp == null ) {
					tmp = new Spectrum();
					tmp.File = spectrum.File;
					tmp.ID = spectrum.ID;
					tmp.SpectrumID = spectrum.SpectrumID;
					tmp.Psm = new List<PSM>();
				}
				tmp.Psm.Add(psm);
				Peptide f = psm.Peptide;
				if( !f.Psm.Contains(psm) )
					f.Psm.Add(psm);
				if( f.Confidence < psm.Confidence )
					f.Confidence = psm.Confidence;
			}
			if( tmp != null )
				spectra.Add(tmp);
		}
		
		Spectra = spectra;
	}
	
	private bool CheckPeptide( Peptide f ) {
		if( f.Proteins.Count == 0 )
			return false;
		if( Spectra.Count != 0 && (f.Psm == null || f.Psm.Count == 0) )
			return false;
		if( FilterDecoys && f.Decoy )
			return false;
		if (f.Sequence.Length < LengthThreshold)
			return false;
		if( Type == SourceType.Plgs && (int)f.Confidence < (int)PlgsThreshold )
			return false;
		return true;
	}
	
	private bool CheckPsm( PSM psm ) {
		if( psm.Peptide == null )
			return false;
		return CheckPsm( psm.passThreshold, psm.Rank, psm.Confidence, psm.Score, psm.ScoreType );
	}
	
	protected bool CheckPsm( bool passThreshold, int rank, Peptide.ConfidenceType confidence, double score, string type ) {
		if( RequirePassTh && !passThreshold )
			return false;
		if( RankThreshold != 0 && (rank == 0 || rank > RankThreshold) )
			return false;		
		if( Type >= SourceType.mzIdentML110 && Type <= SourceType.mzIdentML120 && (int)confidence < (int)SeqThreshold )
			return false;
		if( type == "Mascot expectation value" && score > MascotThreshold )
			return false;
		if( type == "X!Tandem expect" && score > XTandemThreshold )
			return false;				
		return true;
	}
	
	/// <summary>
	/// Peptide classifications
	/// </summary>
	private void ClasifyPeptides() {
		// 1. Locate unique peptides
		foreach( Peptide f in Peptides )
			if( f.Proteins.Count == 1 ) {
				f.Relation = Peptide.RelationType.Unique;
				f.Proteins[0].Evidence = Protein.EvidenceType.Conclusive;
			}
			else
				f.Relation = Peptide.RelationType.Discriminating;
		
		// 2. Locate non-discriminating peptides (first round)
		foreach( Protein p in Proteins )
			if( p.Evidence == Protein.EvidenceType.Conclusive )
				foreach( Peptide f in p.Peptides )
					if( f.Relation != Peptide.RelationType.Unique )
						f.Relation = Peptide.RelationType.NonDiscriminating;
		
		// 3. Locate non-discriminating peptides (second round)
		foreach( Peptide f in Peptides ) {
			if( f.Relation != Peptide.RelationType.Discriminating )
				continue;
			foreach( Peptide f2 in f.Proteins[0].Peptides ) {
				if( f2.Relation == Peptide.RelationType.NonDiscriminating )
					continue;
				if( f2.Proteins.Count <= f.Proteins.Count )
					continue;
				bool is_shared = false;
				foreach( Protein p in f.Proteins )
					if( !p.HasPeptide(f2) ) {
						is_shared = true;
						break;
					}
				if( !is_shared )
					f2.Relation = Peptide.RelationType.NonDiscriminating;
			}
		}
	}
	
	/// <summary>
	/// Classifies proteins according to their peptides
	/// </summary>
	private void ClasifyProteins() {
		List<Protein> proteins = new List<Protein>();
		int id = 1;
				
		foreach( Protein p in Proteins )
			// Conclusive proteins
			if( p.Evidence == Protein.EvidenceType.Conclusive ) {
				p.ID = id++;
				proteins.Add( p );
			} else {
				bool is_group = false;
				foreach( Peptide f in p.Peptides )
					if( f.Relation == Peptide.RelationType.Discriminating ) {
						is_group = true;
						break;
					}
				// Group
				if( is_group )
					p.Evidence = Protein.EvidenceType.Group;
				// Non conclusive
				else {
					p.Evidence = Protein.EvidenceType.NonConclusive;
					p.ID = id++;
					proteins.Add( p );
				}
			}
				
		// Group proteins
		foreach( Protein p in Proteins )
			if( p.Evidence == Protein.EvidenceType.Group )
				AddToGroup( p, ref id, ref proteins );
		
		// Filtered and Undistinguisable
		foreach( Protein p in proteins )
			if( p.Subset.Count > 1 ) {
				if( IsIndistinguisable(p) ) {
					p.Evidence = Protein.EvidenceType.Indistinguishable;
					foreach( Protein p2 in p.Subset )
						p2.Evidence = Protein.EvidenceType.Indistinguishable;
				}
			} else if( p.Peptides.Count == 0 )
				p.Evidence = Protein.EvidenceType.Filtered;
		
		Proteins = proteins;
	}
	
	/// <summary>
	/// Includes a protein in a group
	/// </summary>
	private void AddToGroup( Protein p, ref int id, ref List<Protein> proteins ) {
		Protein g;
		
		if( p.Group == null ) {
			g = new Protein( id++, String.Format("GROUP{0:000}",m_gid++), "", p.Name, "" );
			g.Evidence = Protein.EvidenceType.Group;
			p.Group = g;
			p.ID = id++;
			g.Subset.Add( p );
			proteins.Add( g );
		} else
			g = p.Group;
		
		foreach( Peptide f in p.Peptides ) {
			if( f.Relation != Peptide.RelationType.Discriminating )
				continue;
			foreach( Protein t in f.Proteins )
				if( t.Evidence == Protein.EvidenceType.Group && t.Group == null ) {
					t.ID = id++;
					t.Group = g;
					g.Subset.Add( t );
					g.Desc += " + " + t.Name;
				}
		}
	}
	
	private bool IsIndistinguisable( Protein g ) {
		List<Peptide> discriminating = new List<Peptide>();
		foreach( Protein prot in g.Subset )
			foreach( Peptide pep in prot.Peptides )
				if( pep.Relation == Peptide.RelationType.Discriminating )
					discriminating.Add(pep);
		foreach( Protein prot in g.Subset )
			foreach( Peptide pep in discriminating )
				if( !prot.Peptides.Contains(pep) )
					return false;
		return true;
	
		/*foreach( Peptide f in g.Subset[0].Peptides ) {
			if( f.Relation != Peptide.RelationType.Discriminating )
				continue;
			foreach( Protein p in g.Subset )
				if( !p.HasPeptide(f) )
					return false;
		}
		return true;*/
	}
	
	private void DoStats() {
		// Peptides
		m_Stats.Peptides = Peptides.Count;
		m_Stats.Red = m_Stats.Yellow = m_Stats.Green = 0;
		foreach( Peptide f in Peptides )
			switch( f.Confidence ) {
				case Peptide.ConfidenceType.Red:
					m_Stats.Red++;
					break;
				case Peptide.ConfidenceType.Yellow:
					m_Stats.Yellow++;
					break;
				case Peptide.ConfidenceType.Green:
					m_Stats.Green++;
					break;
			}
		
		// Proteins
		m_Stats.MaxProteins = 0;
		m_Stats.Conclusive = 0;
		m_Stats.NonConclusive = 0;
		m_Stats.Groups = m_Stats.Grouped = 0;
		m_Stats.IGroups = m_Stats.Indistinguisable = 0;
		m_Stats.Filtered = 0;
		foreach( Protein p in Proteins )
			switch( p.Evidence ) {
				case Protein.EvidenceType.Conclusive:
					m_Stats.Conclusive++;
					m_Stats.MaxProteins++;
					break;
				case Protein.EvidenceType.NonConclusive:
					m_Stats.NonConclusive++;
					m_Stats.MaxProteins++;
					break;
				case Protein.EvidenceType.Group:
					m_Stats.Groups++;
					m_Stats.Grouped += p.Subset.Count;
					m_Stats.MaxProteins += p.Subset.Count;
					break;
				case Protein.EvidenceType.Indistinguishable:
					m_Stats.IGroups++;
					m_Stats.Indistinguisable += p.Subset.Count;
					m_Stats.MaxProteins += p.Subset.Count;
					break;
				case Protein.EvidenceType.Filtered:
					m_Stats.Filtered++;
					break;
			}
		//m_Stats.MinProteins = m_Stats.Conclusive + m_Stats.Groups;
	}
	
	protected void Notify( string message ) {
		if( OnNotify != null )
			OnNotify( message );
		else
			Console.WriteLine( message );
	}
	
	/// <summary>
	/// Returns counts
	/// </summary>
	public StatsStruct Stats {
		get { return m_Stats; }
	}
	
	/// <summary>
	/// Delegate used for sending messages from the lib to the app instead of using stdout
	/// </summary>
	public delegate void NotifyDelegate( string message );
	
	/// <summary>
	/// Event used for sending messages from the lib to the app instead of using stdout
	/// </summary>
	public event NotifyDelegate OnNotify;
	
	/// <summary>
	/// Protein list
	/// </summary>
	public List<Protein> Proteins;
	
	/// <summary>
	/// Peptide list
	/// </summary>
	public List<Peptide> Peptides;
	
	/// <summary>
	/// The spectra.
	/// </summary>
	public List<Spectrum> Spectra;

	/// <summary>
	/// Peptide length threshold.
	/// </summary>
	public int LengthThreshold;
	
	/// <summary>
	/// Threshold used in PLGS.
	/// </summary>
	public Peptide.ConfidenceType PlgsThreshold;
	
	/// <summary>
	/// Threshold used in PD/SEQUEST.
	/// </summary>
	public Peptide.ConfidenceType SeqThreshold;

	/// <summary>
	/// The X!Tandem e-value threshold.
	/// </summary>
	public double XTandemThreshold;

	/// <summary>
	/// Wether X!Tandem thresholds are available.
	/// </summary>
	public bool XTandemAvailable;
	
	/// <summary>
	/// The Mascot expectaction score threshold.
	/// </summary>
	public double MascotThreshold;

	/// <summary>
	/// Wether Mascot thresholds are available.
	/// </summary>
	public bool MascotAvailable;
	
	/// <summary>
	/// Returns the input file type.
	/// </summary>
	public SourceType Type {
		get { return m_Type; }
	}
	
	/// <summary>
	/// mzIdentML Spectrum Identification Item passThreshold.
	/// </summary>
	public bool RequirePassTh;
	
	/// <summary>
	/// The mzIdentML rank threshold.
	/// </summary>
	public int RankThreshold;
	
	/// <summary>
	/// Filter decoy peptides.
	/// </summary>
	public bool FilterDecoys;
	
	/// <summary>
	/// The minimum number of runs required.
	/// </summary>
	public int RunsThreshold;
	
	/// <summary>
	/// Gets the psm count.
	/// </summary>
	/// <value>
	/// The psm count.
	/// </value>
	public int PsmCount {
		get {
			int count = 0;
			foreach( Spectrum spectrum in Spectra )
				if( spectrum.Psm != null )
					count += spectrum.Psm.Count;
			return count;
		}
	}
		
	private int m_gid;
	private StatsStruct m_Stats;
	protected SourceType m_Type;
	protected System.Globalization.NumberFormatInfo m_Format;
	protected SortedList<string,Protein> m_SortedProteins;
	protected int m_Run;
	protected List<string> m_InputFiles;
	protected Software m_Software;
}

} // namespace EhuBio.Proteomics.Inference