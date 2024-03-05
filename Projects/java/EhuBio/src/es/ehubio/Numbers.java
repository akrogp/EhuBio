package es.ehubio;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Numbers {
	public static double parseDouble( String str ) throws ParseException {
		return DecimalFormat.getInstance(Locale.ENGLISH).parse(str
				.trim()
				.toUpperCase()
				.replaceAll("\\+","")
				.replace(',', '.')
			).doubleValue();
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
	
	public static boolean between(Number num, Number start, Number end) {
		if( num == null || start == null || end == null )
			return false;
		return num.doubleValue() >= start.doubleValue() && num.doubleValue() <= end.doubleValue();
	}
	
	public static double overlap(Number refStart, Number refEnd, Number testStart, Number testEnd) {
		if( refStart == null || refEnd == null || testStart == null || testEnd == null )
			return 0;
		double start = Math.max(refStart.doubleValue(), testStart.doubleValue());
		double end = Math.min(refEnd.doubleValue(), testEnd.doubleValue());
		if( start > end )
			return 0;
		return (end - start + 1)/(refEnd.doubleValue() - refStart.doubleValue() + 1);
	}
	
	private static final Pattern PATTERN = Pattern.compile("[^0-9\\.eE+-]");
}
