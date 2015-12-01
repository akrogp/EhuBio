package es.ehubio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
		/*if( mergePattern == null )
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
		}*/
				
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
	
	/*public static void main( String[] args ) {
		Set<String> set = new HashSet<>();
		set.add("hola-2");
		set.add("hola-3");
		set.add("hola");
		System.out.println(merge(set));
	}*/
	
	public static String[] fromArray( Object[] array ) {
		String[] result = new String[array.length];
		for( int i = 0; i < array.length; i++ )
			result[i] = array[i].toString();
		return result;
	}
	
	//private static Pattern mergePattern;
}
