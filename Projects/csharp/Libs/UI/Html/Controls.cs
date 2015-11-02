// $Id$
// 
// MyClass.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      MyClass.cs
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

namespace EhuBio.UI.Html {

/// <summary>
/// Tag with state.
/// </summary>
public class Tag {
	/// <summary>
	/// Initializes a new instance of the <see cref="EhuBio.UI.Html.Tag"/> class.
	/// </summary>
	/// <param name='name'>
	/// Name.
	/// </param>
	public Tag( string name ) {
		mName = name;
		mAttr = null;
		mOdd = true;
		mOpen = false;
		OddEvenEnabled = false;
		mHold = false;
	}
	
	/// <summary>
	/// Initializes a new instance of the <see cref="EhuBio.UI.Html.Tag"/> class.
	/// </summary>
	/// <param name='name'>
	/// Name.
	/// </param>
	/// <param name='attr'>
	/// Attribute.
	/// </param>
	public Tag( string name, string attr ) : this(name) {
		mAttr = attr;
	}
	
	/// <summary>
	/// Initializes a new instance of the <see cref="EhuBio.UI.Html.Tag"/> class.
	/// </summary>
	/// <param name='name'>
	/// Name.
	/// </param>
	/// <param name='state'>
	/// Wether to used odd/even state.
	/// </param>
	public Tag( string name, bool state ) : this( name ) {
		OddEvenEnabled = true;
	}
	
	/// <summary>
	/// Returns a <see cref="System.String"/> that represents the current state of <see cref="EhuBio.UI.Html.Tag"/>.
	/// </summary>
	/// <returns>
	/// A <see cref="System.String"/> that represents the current state of <see cref="EhuBio.UI.Html.Tag"/>.
	/// </returns>
	public override string ToString() {
		return RenderTag( null );
	}
	
	private string RenderTag( string attr ) {
		if( mName == null || mName.Length == 0 )
			return "";		
		string res;
		if( !mOpen ) {
			if( !OddEvenEnabled )
				res = "<" + mName;
			else if( mOdd )
				res = "<" + mName + " class=\"odd\"";
			else
				res = "<" + mName + " class=\"even\"";
		} else {
			res = "</" + mName;
			if( !mHold )
				mOdd = !mOdd;
		}
		mOpen = !mOpen;
		if( mOpen && attr != null )
			res += " " + mAttr + "=\"" + attr + "\"";
		return res + ">";
	}
	
	/// <summary>
	/// Renders the specified value between open and close tags.
	/// </summary>
	/// <param name='val'>
	/// Value.
	/// </param>
	public string Render( string val ) {
		return Render( null, val );
	}
	
	/// <summary>
	/// Renders the specified value between open and close tags and using the specified attribute.
	/// </summary>
	/// <param name='attr'>
	/// Attribute.
	/// </param>
	/// <param name='val'>
	/// Value.
	/// </param>
	public string Render( string attr, string val ) {
		return RenderTag(attr) + val + RenderTag(null);
	}
	
	/// <summary>
	/// Resets odd/even state.
	/// </summary>
	public void Reset() {
		mOdd = true;
	}
	
	/// <summary>
	/// Reset the odd/even state to the one of the specified tag.
	/// </summary>
	/// <param name='tag'>
	/// Tag.
	/// </param>
	public void Reset( Tag tag ) {
		mOdd = !tag.mOdd;
	}
	
	/// <summary>
	/// Holds/Unholds odd/event count.
	/// </summary>
	public bool Hold {
		set {
			if( value == mHold )
				return;
			mHold = value;
			mOdd = !mOdd;
		}
		get {
			return mHold;
		}
	}
	
	/// <summary>
	/// Enables the odd and even classes.
	/// </summary>
	public bool OddEvenEnabled;
	
	protected string mName;
	protected string mAttr;
	private bool mOdd;
	private bool mOpen;
	private bool mHold;
}

}