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
}
