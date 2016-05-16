package es.ehubio;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

public class Numbers {
	public static double parseDouble( String str ) throws ParseException {
		return DecimalFormat.getInstance(Locale.ENGLISH).parse(str.trim().toUpperCase().replaceAll("\\+","")).doubleValue();
	}
	
	public static String toString( double num ) {
		return String.format(Locale.ENGLISH, "%e", num);
	}
}
