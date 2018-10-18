package es.ehubio.ubase.dl.providers;

import java.io.BufferedReader;
import java.io.File;

import es.ehubio.io.Streams;

public class CsvFileType extends FileType {
	private String[] fields;
	
	public CsvFileType(String name, String description, boolean large, String... fields) {
		super(name, description, large);
		this.fields = fields;
	}

	@Override
	public boolean checkSignature(File file) {
		try( BufferedReader br = new BufferedReader(Streams.getTextReader(file)) ) {
			String header = br.readLine();
			for( String field : fields )
				if( !header.contains(field) )
					return false;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
