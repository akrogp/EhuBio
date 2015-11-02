package es.ehubio.org.hgvs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ehubio.model.Aminoacid;

// Human Genome Variation Society
public class Hgvs {
	private static Pattern proteinPattern;
	
	public static ProteinMutation parseProteinMutation( String str ) {
		if( proteinPattern == null )
			proteinPattern = Pattern.compile("(?:p\\.)?(\\D{1,3})(\\d*)(\\D{1,3})", Pattern.CASE_INSENSITIVE);
		Matcher matcher = proteinPattern.matcher(str);
		if( !matcher.matches() || matcher.groupCount() != 3 )
			return null;		
		ProteinMutation mut = new ProteinMutation();
		mut.setOriginal(Aminoacid.parse(matcher.group(1)));
		if( mut.getOriginal() == null )
			return null;
		try {
			mut.setPosition(Integer.parseInt(matcher.group(2)));
		} catch( NumberFormatException e ) {
			return null;
		}
		mut.setMutated(Aminoacid.parse(matcher.group(3)));
		if( mut.getMutated() == null )
			return null;
		if( mut.getMutated().equals(mut.getOriginal()) )
			mut.setType(ProteinMutation.Type.Synonymous);
		else
			mut.setType(ProteinMutation.Type.Missense);
		return mut;
	}
}
