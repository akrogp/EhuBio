package es.ehubio;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Numbers {
	public static double parseDouble( String str ) throws ParseException {
		return DecimalFormat.getInstance(Locale.ENGLISH).parse(str.trim().toUpperCase().replaceAll("\\+","")).doubleValue();
	}
	
	public static double parseDoubleDiscarding( String str ) throws ParseException {
		Matcher match = PATTERN.matcher(str);
		return parseDouble(match.replaceAll(""));
	}
	
	public static Double optDouble( String str ) {
		if( str == null )
			return null;
		Double result = null;
		try {
			result = parseDouble(str);
		} catch (ParseException e) {
		}
		return result;
	}
	
	public static String toString( double num ) {
		return String.format(Locale.ENGLISH, "%e", num);
	}
	
	private static final Pattern PATTERN = Pattern.compile("[^0-9\\.eE+-]");
}
