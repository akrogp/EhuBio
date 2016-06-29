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
	
	public static String translate( String dna, Map<String, Codon> code ) {
		StringBuilder prot = new StringBuilder();
		dna = dna.toUpperCase();
		int i, len = dna.length();
		for( i = 0; i < len-3; i++ ) {
			Codon codon = code.get(dna.substring(i,i+3));
			if( codon.isStart() )
				break;
		}
		for( ; i < len; i+= 3 ) {
			Codon codon = code.get(dna.substring(i,i+3));
			if( codon.isStop() )
				break;
			prot.append(codon.getAa().letter);
		}
		return prot.toString();
	}
}
