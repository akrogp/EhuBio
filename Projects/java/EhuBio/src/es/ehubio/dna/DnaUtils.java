package es.ehubio.dna;

import java.util.HashMap;
import java.util.Map;

public class DnaUtils {
	public static String getReverseStrand( String dna ) {
		Map<Character,Character> map = new HashMap<>();
		map.put('a', 't');
		map.put('t', 'a');
		map.put('g', 'c');
		map.put('c', 'g');
		StringBuilder builder = new StringBuilder();
		dna = dna.toLowerCase();
		Character complement;
		for( int i = dna.length()-1; i >= 0; i-- ) {
			complement = map.get(dna.charAt(i));
			if( complement == null )
				builder.append('?');
			else
				builder.append(complement);
		}			
		return builder.toString();
	}
}
