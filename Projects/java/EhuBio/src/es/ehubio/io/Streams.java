package es.ehubio.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Streams {
	public static Reader getTextReader( File file ) throws FileNotFoundException, IOException {
		return getTextReader(file.getAbsolutePath());
	}
	
	public static Reader getTextReader( String path ) throws FileNotFoundException, IOException {
		if( isGzip(path) )
			return new InputStreamReader(new GZIPInputStream(new FileInputStream(path)));
		return new FileReader(path);
	}
	
	public static Writer getTextWriter( File file ) throws FileNotFoundException, IOException {
		return getTextWriter(file.getAbsolutePath());
	}
	
	public static Writer getTextWriter( String path ) throws FileNotFoundException, IOException {
		if( isGzip(path) )
			return new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(path)));
		return new FileWriter(path);
	}
	
	public static InputStream getBinReader( File file ) throws FileNotFoundException, IOException {
		return getBinReader(file.getAbsolutePath());
	}
	
	public static InputStream getBinReader( String path ) throws FileNotFoundException, IOException {
		InputStream stream = new FileInputStream(path);		
		if( isGzip(path) )
			stream = new GZIPInputStream(stream);
		return stream;
	}
	
	public static OutputStream getBinWriter( File file ) throws FileNotFoundException, IOException {
		return getBinWriter(file.getAbsolutePath());
	}
	
	public static OutputStream getBinWriter( String path ) throws FileNotFoundException, IOException {
		OutputStream stream = new FileOutputStream(path);
		if( isGzip(path) )
			stream = new GZIPOutputStream(stream);
		return stream;
	}
	
	public static boolean isGzip( String path ) {
		return path.toLowerCase().endsWith(".gz");
	}
}
