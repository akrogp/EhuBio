package es.ehubio.db.go;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Ontology {
	public static List<Term> loadTerms(String oboPath) throws FileNotFoundException, IOException {
		List<Term> terms = new ArrayList<>();		
		try(BufferedReader br = new BufferedReader(new FileReader(oboPath))) {
			Term term = null;
			String line;
			while( (line=br.readLine()) != null) {
				if( line.startsWith("[") ) {
					if( term != null ) {
						terms.add(term);
						term = null;
					}
					if( line.equals("[Term]") )
						term = new Term();
				}
				if( term == null )
					continue;
				if( line.startsWith("id:") ) {
					term.setId(parseValue(line));
				} else if( line.startsWith("name:") ) {
					term.setName(parseValue(line));
				} else if( line.startsWith("namespace:") ) {
					term.setNamespace(parseValue(line));
				} else if( line.startsWith("is_obsolete:") ) {
					term.setObsolete("true".equalsIgnoreCase(parseValue(line)));
				}
			}
			if( term != null )
				terms.add(term);
		}		
		return terms;
	}

	private static String parseValue(String line) {
		return line.split(": ")[1].strip();
	}
}
