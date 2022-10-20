package es.ehubio.io;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public final class StringUtils {
	private StringUtils() {		
	}
	
	public static String fixGeneName(Date date) {
		return fixGeneName(date, ZoneId.systemDefault());
	}
	
	public static String fixGeneName(Date date, ZoneId zoneId) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMd", Locale.ENGLISH);		
		LocalDate ld = date.toInstant().atZone(zoneId).toLocalDate();
		return ld.format(dateFormatter).toUpperCase();
	}

	public static String truncate(String str, int len, String suffix) {
		if( str.length() <= len )
			return str;
		if( suffix != null )
			len -= suffix.length();
		if( len < 0 )
			len = 0;
		str = str.substring(0, len);		
		return suffix != null ? str+suffix : str;
	}
}
