package es.ehubio.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class UnixCfgReader {
	private BufferedReader mReader;
	private List<String> comments;	

	public UnixCfgReader( Reader rd ) {
		mReader = new BufferedReader(rd);
		comments = new ArrayList<>();
	}
	
	public String readLine() throws IOException {
		String str = mReader.readLine();
		while( str != null ) {
			str = str.trim();
			if( str.isEmpty() )
				str = mReader.readLine();
			else if( str.startsWith("#") ) {
				comments.add(str.substring(1).trim());
				str = mReader.readLine();
			} else
				break;
		}
		return str;
	}
	
	public void close() throws IOException {
		if( mReader != null )
			mReader.close();
	}
	
	public String getComment( String str ) {
		for( String comment : comments )
			if( comment.startsWith(str) )
				return comment;
		return null;
	}
	
	public List<String> getComments() {
		return comments;
	}
}