package es.ehubio.io;

public class CsvUtils {
	public static String getCsv( char separator, Object... fields) {
		return getCsv(separator+"", fields);
	}
	
	public static String getCsv( String separator, Object... fields) {
		if( fields == null || fields.length == 0 )
			return "";
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for( ; i < fields.length - 1; i++ ) {
			builder.append(String.valueOf(fields[i]));
			builder.append(separator);
		}
		builder.append(String.valueOf(fields[i]));
		return builder.toString();
	}
}
