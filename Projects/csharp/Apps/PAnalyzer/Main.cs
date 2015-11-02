// $Id: Main.cs 78 2013-09-20 17:21:43Z gorka.prieto@gmail.com $
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
using Gtk;

class PAnalyzerGtk {
	public static void Main( string[] args ) {
		Application.Init();
		MainWindow win = new MainWindow();
		win.DeleteEvent += OnDelete;
		win.Show();
		Application.Run();
	}
	
    static void OnDelete( object obj, DeleteEventArgs args ) {
		Application.Quit();
	}
}