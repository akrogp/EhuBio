package es.ehubio.ubase.dl.providers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CsvFileType extends FileType {
	private String[] fields;
	
	public CsvFileType() {
		super();
	}
	
	public CsvFileType(String name, String description, String... fields) {
		super(name, description);
		this.fields = fields;
	}

	@Override
	public boolean checkSignature(File file) {
		try( BufferedReader br = new BufferedReader(new FileReader(file)) ) {
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
