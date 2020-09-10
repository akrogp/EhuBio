package es.ehubio.db.cosmic;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import es.ehubio.io.CsvReader;

public class Census implements Closeable {
	public static class Entry {
		private String geneName;
		private String mutationAa;
		private String mutationDescriptionAa;
		private int recurrence;

		public String getGeneName() {
			return geneName;
		}

		public void setGeneName(String geneName) {
			this.geneName = geneName;
		}

		public String getMutationAa() {
			return mutationAa;
		}

		public void setMutationAa(String mutationAa) {
			this.mutationAa = mutationAa;
		}

		public String getMutationDescriptionAa() {
			return mutationDescriptionAa;
		}

		public void setMutationDescriptionAa(String mutationDescriptionAa) {
			this.mutationDescriptionAa = mutationDescriptionAa;
		}

		public int getRecurrence() {
			return recurrence;
		}

		public void setRecurrence(int recurrence) {
			this.recurrence = recurrence;
		}
	}	
	
	public void open(String path) throws FileNotFoundException, IOException {
		csv.open(new InputStreamReader(new GZIPInputStream(new FileInputStream(path))));
	}
	
	@Override
	public void close() throws IOException {
		csv.close();		
	}
	
	public Entry next() throws IOException {
		if( csv.readLine() == null )
			return null;
		Entry entry = new Entry();
		entry.setGeneName(csv.getField(0));
		entry.setMutationAa(csv.getField("Mutation AA"));
		entry.setMutationDescriptionAa(csv.getField("Mutation Description AA"));
		entry.setRecurrence(csv.getIntField("COSMIC_SAMPLE_MUTATED"));
		return entry;
	}
	
	private final CsvReader csv = new CsvReader("\\t", true);
}
