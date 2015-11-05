package es.ehubio.cli;

import java.util.ArrayList;
import java.util.List;

public class Terminal {
	public static List<String> splitLine(String text, int width) {
		return splitLine(text, width, null);
	}
			
	public static List<String> splitLine(String text, int width, String indent) {
		String seps = " \t.,:;-+=*";
		List<String> lines = new ArrayList<String>();
		int lastWord = 0, newWord = 0;
		boolean doIndent = false;
		for( int i = 0, j = 0; i < text.length(); i++, j++ ) {
			if( seps.indexOf(text.charAt(i)) != -1 )
				newWord = i+1;
			if( j >= width ) {
				j = 0;
				String line = text.substring(lastWord, newWord);
				if( doIndent )
					lines.add(indent+line);
				else {
					if( indent != null ) {
						doIndent = true;
						width -= indent.length();
					}
					lines.add(line);
				}
				lastWord = newWord;
			}
		}
		if( doIndent )
			lines.add(indent+text.substring(lastWord));
		else
			lines.add(text.substring(lastWord));
		return lines;
	}
}
