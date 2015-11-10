package es.ehubio;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Numbers {
	public static double parseDouble( String str ) throws ParseException {
		return NumberFormat.getInstance(Locale.ENGLISH).parse(str.replaceAll("\\+","").trim()).doubleValue();
	}
	
	public static String toString( double num ) {
		return String.format(Locale.ENGLISH, "%f", num);
	}
}
