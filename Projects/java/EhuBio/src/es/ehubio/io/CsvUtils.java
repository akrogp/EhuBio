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
		String str;
		boolean escape;
		for( ; i < fields.length; i++ ) {
			str = String.valueOf(fields[i]);
			escape = str.contains(separator);
			if( escape )
				builder.append('"');
			builder.append(str);
			if( escape )
				builder.append('"');
			if( i < fields.length - 1)
				builder.append(separator);
		}
		return builder.toString();
	}
}
