// $Id: MainWindow.cs 172 2014-06-03 14:44:57Z gorka.prieto@gmail.com $
// 
// MainWindow.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      MainWindow.cs
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
using Gtk;
using EhuBio.Proteomics.Inference;

public partial class MainWindow : Gtk.Window {
	private Gtk.FileChooserDialog m_dlgOpen;
	private Gtk.FileChooserDialog m_dlgSave;
	private PreferencesDlg m_dlgPrefs;
	private AboutDlg m_dlgAbout;
	private Mapper m_Mapper;
	private string m_LastDir;
	private Mapper.Software m_Software;
	private int m_nFiles;
	
	public enum States { EMPTY, LOADING, LOADED, EXECUTING, EXECUTED };

	public MainWindow () : base(Gtk.WindowType.Toplevel) {
		Build();
		
		m_Software.Name      = "PAnalyzer";
		m_Software.Version   = "1.1";
		m_Software.License   = "Released under the GNU General Public License";
		m_Software.Copyright = "(c) 2010-2014 by UPV/EHU";
		m_Software.Contact   = "gorka.prieto@ehu.es";
		m_Software.Customizations = "No customizations";
		m_Software.Url		 = "https://code.google.com/p/ehu-bio/wiki/PAnalyzer";
		
		m_dlgOpen = new Gtk.FileChooserDialog(
			"Select data file ...", this, FileChooserAction.Open,
			Stock.Open, ResponseType.Ok, Stock.Cancel, ResponseType.Cancel );
		m_dlgOpen.Filter = new FileFilter();
		m_dlgOpen.Filter.AddPattern( "*.xml" );
		m_dlgOpen.Filter.AddPattern( "*.mzid" );
		m_dlgOpen.SelectMultiple = true;
		m_LastDir = ".";
		
		m_dlgSave = new Gtk.FileChooserDialog(
			"Select data file ...", this, FileChooserAction.Save,
			Stock.SaveAs, ResponseType.Ok, Stock.Cancel, ResponseType.Cancel );
		m_dlgSave.Filter = new FileFilter();
		m_dlgSave.Filter.AddPattern( "*.csv" );
		m_dlgSave.Filter.AddPattern( "*.mzid" );
		
		ProteinsView.AppendColumn( "ID", new CellRendererText(), "text", 0 );
		ProteinsView.AppendColumn( "Entry", new CellRendererText(), "text", 1 );
		ProteinsView.AppendColumn( "Accession", new CellRendererText(), "text", 2 );
		ProteinsView.AppendColumn( "Evidence", new CellRendererText(), "text", 3 );
		ProteinsView.AppendColumn( "Description", new CellRendererText(), "text", 4 );
		ProteinsView.CursorChanged += OnProteinSelected;
		
		PeptidesView.AppendColumn( "ID", new CellRendererText(), "text", 0 );
		PeptidesView.AppendColumn( "Confidence", new CellRendererText(), "text", 1 );
		PeptidesView.AppendColumn( "Relation", new CellRendererText(), "text", 2 );
		PeptidesView.AppendColumn( "Sequence", new CellRendererText(), "text", 3 );
		PeptidesView.CursorChanged += OnPeptideSelected;
		
		m_dlgPrefs = new PreferencesDlg();
		m_dlgPrefs.PlgsThreshold = Peptide.ConfidenceType.Yellow;
		m_dlgPrefs.SeqThreshold = Peptide.ConfidenceType.Yellow;
		m_dlgPrefs.XTandemTh = 0.05;
		m_dlgPrefs.PassTh = true;
		m_dlgPrefs.RankTh = 0;
		m_dlgPrefs.Runs = 1;
		m_dlgPrefs.Hide();
		
		m_dlgAbout = new AboutDlg();
		m_dlgAbout.Version = m_Software.Name + " v" + m_Software.Version;
		m_dlgAbout.License = m_Software.License;
		m_dlgAbout.Copyright = m_Software.Copyright;
		m_dlgAbout.Hide();
		
		/*Log.Text = "".PadRight(80,'*');
		WriteLog( m_Version );
		WriteLog( m_License );
		WriteLog( m_Copyright );
		WriteLog( "\n".PadLeft(80,'*') );*/
		
		//preferencesAction.Sensitive = true;
		dialogInfoAction.Sensitive = true;
		State = States.EMPTY;
		
		m_Mapper = null;		
		m_nFiles = 0;
	}
	
	public TextBuffer Log {
		get { return m_Log.Buffer; }
	}
	
	public void WriteLog( string text ) {
		Log.Insert( Log.EndIter, "\n" + text );
	}
	
	public new States State {
		set {
			//PeptidesView.Sensitive = false;
			//ProteinsView.Sensitive = false;
			Tabs.Sensitive = false;
			switch( value ) {
				case States.EMPTY:
					openAction1.Sensitive = true;
					saveAction1.Sensitive = false;
					executeAction.Sensitive = false;
					Tabs.CurrentPage = 0;
					break;
				case States.LOADING:
					openAction1.Sensitive = false;
					saveAction1.Sensitive = false;
					executeAction.Sensitive = false;
					Tabs.CurrentPage = 0;
					break;
				case States.LOADED:
					openAction1.Sensitive = true;
					saveAction1.Sensitive = false;
					executeAction.Sensitive = true;
					//PeptidesView.Sensitive = true;
					//ProteinsView.Sensitive = true;
					break;
				case States.EXECUTING:
					goto case States.LOADING;
				case States.EXECUTED:
					openAction1.Sensitive = true;
					saveAction1.Sensitive = true;
					executeAction.Sensitive = false;
					//PeptidesView.Sensitive = true;
					//ProteinsView.Sensitive = true;
					Tabs.Sensitive = true;
					break;
			}
			m_State = value;
		}
		get { return m_State; }
	}
	
	private States m_State;
	
	private void DisplayData() {
		TreeStore store = new TreeStore( typeof(int), typeof(string), typeof(string), typeof(string), typeof(string) );
		foreach( Protein p in m_Mapper.Proteins )
			store.AppendValues( p.ID, p.Entry, p.Accession, p.Evidence.ToString(), p.Desc );
		ProteinsView.Model = store;
		
		store = new TreeStore( typeof(int), typeof(string), typeof(string), typeof(string) );
		foreach( Peptide f in m_Mapper.Peptides )
			store.AppendValues( f.ID, f.Confidence.ToString(), f.Relation.ToString(), f.Sequence );
		PeptidesView.Model = store;
	}

	protected void OnDeleteEvent (object sender, DeleteEventArgs a) {
		Application.Quit();
		a.RetVal = true;
	}
	
	protected void OnProteinSelected( object obj, EventArgs e ) {
		TreeSelection selection = (obj as TreeView).Selection;
		TreeModel model;
        TreeIter iter;
        if( !selection.GetSelected(out model, out iter) )
        	return;
        int i = int.Parse(model.GetPath(iter).ToString());
        //model.GetValue(iter,0);
        Protein p = m_Mapper.Proteins[i];
        
        if( p.Subset.Count == 0 ) {
        	textviewProt.Buffer.Text = "Peptide list: ";
        	foreach( Peptide f in p.Peptides )
        		textviewProt.Buffer.Text += f.ToString() + ' ';
        	textviewProt.Buffer.Text += "\nSequence: " + p.Sequence;
        } else {
        	textviewProt.Buffer.Text = "";
        	foreach( Protein t in p.Subset ) {
        		textviewProt.Buffer.Text += t.Name + ": ";
        		foreach( Peptide f in t.Peptides )
        			textviewProt.Buffer.Text += f.ToString() + ' ';
        		textviewProt.Buffer.Text += "\n";
        	} 
        }
	}
	
	protected void OnPeptideSelected( object obj, EventArgs e ) {
		TreeSelection selection = (obj as TreeView).Selection;
		TreeModel model;
        TreeIter iter;
        if( !selection.GetSelected(out model, out iter) )
        	return;
        int i = int.Parse(model.GetPath(iter).ToString());
        //model.GetValue(iter,0);
        Peptide f = m_Mapper.Peptides[i];
        textviewFrag.Buffer.Text = "Protein list: ";
        foreach( Protein p in f.Proteins )
        	textviewFrag.Buffer.Text += p.ID + " ";
        textviewFrag.Buffer.Text += "\nRuns: ";
        foreach( int run in f.Runs )
        	textviewFrag.Buffer.Text += run + " ";
        textviewFrag.Buffer.Text += "\nPTMs: ";
        if( f.Variants.Count == 1 )
        	textviewFrag.Buffer.Text += Peptide.Variant2Str( f.LastVariant );
        else {
        	i = 1;
        	foreach( List<PTM> v in f.Variants )
        		textviewFrag.Buffer.Text += "\n\tVariant #" + (i++) + ": " + Peptide.Variant2Str(v);
        }
	}
	
	protected virtual void OnOpenActionActivated( object sender, System.EventArgs e ) {
		m_dlgOpen.SelectFilename( m_LastDir );
		int res = m_dlgOpen.Run();
		m_dlgOpen.Hide();
		if( res != (int)ResponseType.Ok )
			return;
		m_LastDir = System.IO.Path.GetDirectoryName(m_dlgOpen.Filenames[0]);
		
		m_nFiles = 0;
		string title = " New Analysis ";
		int tlen = title.Length;
		Log.Text = title.PadLeft(40+tlen/2,'*').PadRight(80,'*');
		State = States.LOADING;		
		try {
			bool bThresholds = true;
			m_Mapper = Mapper.Create( m_dlgOpen.Filename, m_Software );
			m_Mapper.OnNotify += WriteLog;
			foreach( string xmlpath in m_dlgOpen.Filenames ) {
				m_Mapper.Load( xmlpath, true );
				if( m_Mapper.PlgsThreshold == Peptide.ConfidenceType.NoThreshold )
					bThresholds = false;
				m_nFiles++;
			}
			m_Mapper.PlgsThreshold = bThresholds ? Peptide.ConfidenceType.Yellow : Peptide.ConfidenceType.NoThreshold;
		} catch( Exception ex ) {
			WriteLog( "Error loading XML: " + ex.Message );
			WriteLog( "Stack trace:\n" + ex.StackTrace );
			State = States.EMPTY;
			return;
		}
		WriteLog( "Completed!" );
		if( m_Mapper.Spectra.Count != 0 ) {
			WriteLog( "\tLoaded " + m_Mapper.Spectra.Count + " spectra" );
			WriteLog( "\tLoaded " + m_Mapper.PsmCount + " PSMs" );
		}
		WriteLog( "\tLoaded " + m_Mapper.Peptides.Count + " possible peptides" );
		WriteLog( "\tLoaded " + m_Mapper.Proteins.Count + " possible proteins" );
		DisplayData();
		State = States.LOADED;
	}
	
	protected virtual void OnSaveActionActivated( object sender, System.EventArgs e ) {
		int res = m_dlgSave.Run();
		m_dlgSave.Hide();
		if( res != (int)ResponseType.Ok )
			return;
		WriteLog( "\nSaving ..." );
		string fpath = m_dlgSave.Filename;
		try {
			m_Mapper.Save( fpath );
		} catch( Exception ex ) {
			WriteLog( "Error saving results: " + ex.Message );
			return;
		}
		WriteLog( "Done!" );
	}
	
	protected virtual void OnExecuteActionActivated( object sender, System.EventArgs e ) {
		bool MzidPsm = m_Mapper.Type >= Mapper.SourceType.mzIdentML110 && m_Mapper.Type <= Mapper.SourceType.mzIdentML120;
		m_dlgPrefs.LengthThreshold = m_Mapper.LengthThreshold;
		m_dlgPrefs.PlgsThreshold = m_Mapper.PlgsThreshold;
		m_dlgPrefs.PlgsThSensitive = m_Mapper.Type == Mapper.SourceType.Plgs && m_Mapper.PlgsThreshold != Peptide.ConfidenceType.NoThreshold;
		m_dlgPrefs.SeqThreshold = m_Mapper.SeqThreshold;
		m_dlgPrefs.SeqThSensitive = MzidPsm && m_Mapper.SeqThreshold != Peptide.ConfidenceType.NoThreshold;
		m_dlgPrefs.MascotTh = m_Mapper.MascotThreshold;
		m_dlgPrefs.MascotThSensitive = m_Mapper.MascotAvailable;
		m_dlgPrefs.XTandemTh = m_Mapper.XTandemThreshold;
		m_dlgPrefs.XTandemThSensitive = m_Mapper.XTandemAvailable;
		m_dlgPrefs.PassTh = m_Mapper.RequirePassTh;
		m_dlgPrefs.PassThSensitive = MzidPsm;
		m_dlgPrefs.RankTh = m_Mapper.RankThreshold;
		m_dlgPrefs.FilterDecoys = m_Mapper.FilterDecoys;
		m_dlgPrefs.RankThSensitive = MzidPsm;
		m_dlgPrefs.MultiRunSensitive = m_nFiles > 1;
		m_dlgPrefs.Runs = m_nFiles;
		
		// Asks for preferences only when needed
		if( m_Mapper.PlgsThreshold != Peptide.ConfidenceType.NoThreshold || MzidPsm || m_nFiles > 1 ) {
			if( m_nFiles > 1 )
				m_dlgPrefs.RunTh = 2;
			int res = m_dlgPrefs.Run();
			m_dlgPrefs.Hide();
			if( res == (int)ResponseType.Cancel )
				return;
		}
		State = MainWindow.States.EXECUTING;

		m_Mapper.LengthThreshold = m_dlgPrefs.LengthThreshold;
		m_Mapper.PlgsThreshold = m_dlgPrefs.PlgsThreshold;
		m_Mapper.SeqThreshold = m_dlgPrefs.SeqThreshold;
		m_Mapper.MascotThreshold = m_dlgPrefs.MascotTh;
		m_Mapper.XTandemThreshold = m_dlgPrefs.XTandemTh;
		m_Mapper.RequirePassTh = m_dlgPrefs.PassTh;
		m_Mapper.RankThreshold = m_dlgPrefs.RankTh;
		m_Mapper.FilterDecoys = m_dlgPrefs.FilterDecoys;
		m_Mapper.RunsThreshold = m_dlgPrefs.RunTh;
		m_Mapper.Do();
		DisplayData();
		State = MainWindow.States.EXECUTED;
		WriteLog( "\nStats:" );
		WriteLog( "------" );
		WriteLog( "Peptides:" );
		WriteLog( "\tTotal: "+ m_Mapper.Stats.Peptides );
		if( m_Mapper.PlgsThreshold != Peptide.ConfidenceType.NoThreshold || m_Mapper.SeqThreshold != Peptide.ConfidenceType.NoThreshold ) {
			WriteLog( "\tRed: "+ m_Mapper.Stats.Red );
			WriteLog( "\tYellow: "+ m_Mapper.Stats.Yellow );
			WriteLog( "\tGreen: "+ m_Mapper.Stats.Green );
		}
		WriteLog( "Proteins: " );
		WriteLog( "\tMaximum: " + m_Mapper.Stats.MaxProteins );
		WriteLog( "\tConclusive: " + m_Mapper.Stats.Conclusive );
		WriteLog( "\tIndistinguisable: " + m_Mapper.Stats.Indistinguisable + " in " + m_Mapper.Stats.IGroups +
			(m_Mapper.Stats.IGroups == 1 ? " group" : " groups") );
		WriteLog( "\tGrouped: " + m_Mapper.Stats.Grouped + " in " + m_Mapper.Stats.Groups +
			(m_Mapper.Stats.Groups == 1 ? " group" : " groups") );
		WriteLog( "\tNon conclusive: " + m_Mapper.Stats.NonConclusive );
		WriteLog( "\tFiltered: " + m_Mapper.Stats.Filtered );
	}	
	
	protected virtual void OnDialogInfoActionActivated( object sender, System.EventArgs e ) {
		m_dlgAbout.Run();
		m_dlgAbout.Hide();
	}
}