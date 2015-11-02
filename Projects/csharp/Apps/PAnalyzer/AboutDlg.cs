// $Id: AboutDlg.cs 78 2013-09-20 17:21:43Z gorka.prieto@gmail.com $
// 
// AboutDlg.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      AboutDlg.cs
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
using EhuBio.Proteomics.Inference;

public partial class AboutDlg : Gtk.Dialog {
	public AboutDlg() {
		this.Build ();
	}
	
	public string Version {
		set { VersionLabel.Text = value; }
	}
	
	public string License {
		set { LicenseLabel.Text = value; }
	}
	
	public string Copyright {
		set { CopyrightLabel.Text = value; }
	}
}