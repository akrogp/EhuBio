package es.ehubio.dubase.pl.views;

import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import es.ehubio.dubase.pl.Colors;

@Named
@RequestScoped
public class Formats {
	public static String exp10(Double pValue) {
		if( pValue == null )
			return na();
		String pValueFmt = String.format(Locale.ENGLISH, "%4.1e", pValue);
		String[] fields = pValueFmt.split("[eE]");
		if( fields.length == 2 )
			pValueFmt = String.format("%s x 10<sup>%s</sup>", fields[0], fields[1]);
		return pValueFmt;
	}
	
	public static String imputed(String value) {
		return "<i>"+value+"</i>";
	}
	
	public static String na() {
		return imputed("N/A");
	}
	
	public static String decimal1(Double score) {
		if( score == null )
			return na();
		return String.format(Locale.ENGLISH, "%.1f", score);
	}
	
	public static String decimal2(Double score) {
		if( score == null )
			return na();
		return String.format(Locale.ENGLISH, "%.2f", score);
	}
	
	public static String decimal3(Double score) {
		if( score == null )
			return na();
		return String.format(Locale.ENGLISH, "%.3f", score);
	}
	
	public static String total(String value) {
		return "<b>"+value+"</b>";
	}
	
	public static String percent(Double value) {
		if( value == null )
			return na();
		return String.format("%.1f %%", value);
	}
	
	/*public static String method(Method m) {
		if( m.getSubtype() == null )
			return m.getType().getName();
		return String.format("%s (%s)", m.getType().getName(), m.getSubtype().getName());
	}*/
	
	public static String logChange(Double change) {
		if( change == null )
			return na();
		return String.format(Locale.ENGLISH,
			"<font color='%s'>%.2f</font>",
			change >= 0 ? Colors.UP_REGULATED : Colors.DOWN_REGULATED,
			change);
	}
}
