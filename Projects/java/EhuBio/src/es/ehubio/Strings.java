package es.ehubio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Strings {
	public static String capitalizeFirst( String str ) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public static String plural( String str ) {
		char last = str.charAt(str.length()-1);
		if( Character.toLowerCase(last) == 's' )
			return str;
		return str+(Character.isLowerCase(last)?'s':'S'); 
	}

	public static String merge( Set<String> strings ) {
		// Canonical cases
		if( strings.isEmpty() )
			return null;
		if( strings.size() == 1 )
			return strings.iterator().next();
	
		// Sort strings
		List<String> list = new ArrayList<>(strings);
		Collections.sort(list);
	
		// Merge strings
		if( mergePattern == null )
			mergePattern = Pattern.compile("[0-9a-zA-Z]+");
		Matcher matcher = mergePattern.matcher(list.get(0));
		if( !matcher.find() )
			return null;
		String base = matcher.group();
		StringBuilder name = new StringBuilder(base);
		boolean wildcard = false;
		for( int i = 1; i < list.size(); i++ ) {
			matcher = mergePattern.matcher(list.get(i));
			if( !matcher.find() )
				return null;
			String next = matcher.group();
			if( next.equals(base) ) {
				if( !wildcard ) {
					name.append('*');
					wildcard = true;
				}
			} else {
				base = next;
				name.append('+');
				name.append(base);
				wildcard = false;
			}
		}
		return name.toString();
	}
	
	private static Pattern mergePattern;
}
