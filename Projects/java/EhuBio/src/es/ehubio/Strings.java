package es.ehubio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ehubio.io.CsvUtils;

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
		int i;
		for( i = 0; i < list.get(0).length(); i++ ) {
			int j;
			for( j = 1; j < list.size(); j++ )
				if( list.get(j).charAt(i) != list.get(0).charAt(i) )
					break;
			if( j != list.size() )
				break;
		}
		if( i < 3 )
			return CsvUtils.getCsv('+', list.toArray());
		
		StringBuilder name = new StringBuilder();
		name.append(list.get(0).substring(0, i));
		int j = 0;
		if( list.get(0).length() == i ) {
			name.append('+');
			j++;
		}
		name.append('[');
		for( ; j < list.size(); j++ ) {			
			name.append(list.get(j).substring(i));
			if( j < list.size()-1 )
				name.append("|");
		}
		name.append(']');
		return name.toString();
	}
	
	public static String[] fromArray( Object[] array ) {
		String[] result = new String[array.length];
		for( int i = 0; i < array.length; i++ )
			result[i] = array[i].toString();
		return result;
	}
	
	public static String match(Pattern pattern, String target) {
		Matcher matcher = pattern.matcher(target);
		if( !matcher.find() || matcher.groupCount() < 1 )
			return null;
		return matcher.group(1);
	}
	
	public static int counti(String str, char ch) {
		return count(str.toLowerCase(), Character.toLowerCase(ch));
	}
	
	public static int count(String str, char ch) {
		int count = 0;
		for( int i = 0; i < str.length(); i++ )
			if( str.charAt(i) == ch )
				count++;
		return count;
	}
}
