package es.ehubio.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class CsvReader {
	private final String sep;
	private final boolean useHeader;
	private final boolean filterMarks;
	private BufferedReader br;
	private Map<String, Integer> header = new HashMap<>();
	private String[] fields;
	private String line;
	
	public CsvReader( String sep, boolean useHeader, boolean filterMarks ) {
		this.sep = sep;
		this.useHeader = useHeader;
		this.filterMarks = filterMarks;
	}
	
	public CsvReader( String sep, boolean useHeader ) {
		this(sep,useHeader,true);
	}
	
	public CsvReader( String sep ) {
		this(sep,true,true);
	}
	
	public void open( Reader reader ) throws IOException {
		close();
		br = new BufferedReader(reader);		
		if( useHeader ) {			
			readLine();			
			for( int i = 0; i < fields.length; i++ )
				header.put(parseField(fields[i]), i);
		}
		fields = null;
	}
	
	public void open( String path ) throws IOException {
		open(new FileReader(path));
	}
	
	public void close() throws IOException {
		if( br != null )
			br.close();
		header.clear();
	}
	
	public String[] readLine() throws IOException  {
		line = br.readLine();
		if( line == null )
			return null;
		fields = line.split(sep);
		return fields;
	}
	
	public String[] getFields() {
		return fields;
	}
	
	public String getLine() {
		return line;
	}
	
	public String getField( int i ) {
		if( i >= fields.length )
			return null;
		return parseField(fields[i]);
	}
	
	public Integer getIntField( int i ) {
		String field = getField(i);
		if( field == null )
			return null;
		field = formatNumber(field);
		return Integer.parseInt(field);
	}
	
	public Double getDoubleField( int i ) {
		String field = getField(i);
		if( field == null )
			return null;
		field = formatNumber(field);
		return Double.parseDouble(field);
	}
	
	public String getField( String name ) {
		Integer pos = header.get(name);
		if( pos == null )
			return null;
		return getField(pos.intValue());
	}
	
	public Integer getIntField( String name ) {
		String field = getField(name);
		if( field == null )
			return null;
		field = formatNumber(field);
		return Integer.parseInt(field);
	}
	
	public Double getDoubleField( String name ) {
		String field = getField(name);
		if( field == null )
			return null;
		field = formatNumber(field);
		return Double.parseDouble(field);
	}
	
	public int getIndex( String field ) {
		Integer pos = header.get(field);
		if( pos == null )
			return -1;
		return pos.intValue();
	}
	
	public String[] getHeaders() {
		return header.keySet().toArray(new String[0]);
	}
	
	private String parseField( String field ) {
		if( filterMarks )
			if( field.length() >= 2 && field.startsWith("\"") && field.endsWith("\"") )
				return field.substring(1, field.length()-1);
		return field;
	}
	
	private String formatNumber( String field ) {
		return field.replace(',', '.');
	}
}