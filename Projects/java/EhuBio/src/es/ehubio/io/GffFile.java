package es.ehubio.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;

public class GffFile {
	public static class Feature {
		public String getSeqid() {
			return seqid;
		}
		public void setSeqid(String seqid) {
			this.seqid = seqid;
		}
		public String getSource() {
			return source;
		}
		public void setSource(String source) {
			this.source = source;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getStrand() {
			return strand;
		}
		public void setStrand(String strand) {
			this.strand = strand;
		}
		public Integer getStart() {
			return start;
		}
		public void setStart(Integer start) {
			this.start = start;
		}
		public Integer getEnd() {
			return end;
		}
		public void setEnd(Integer end) {
			this.end = end;
		}
		public Integer getPhase() {
			return phase;
		}
		public void setPhase(Integer phase) {
			this.phase = phase;
		}
		public Double getScore() {
			return score;
		}
		public void setScore(Double score) {
			this.score = score;
		}
		public void setAttribute(String name, String value) {
			mapAttr.put(name, value);
		}
		public String getAttribute(String name) {
			return mapAttr.get(name);
		}
		public Set<String> getAttributes() {
			return mapAttr.keySet();
		}
		private String seqid, source, type, strand;
		private Integer start, end, phase;
		private Double score;
		private final Map<String, String> mapAttr = new HashMap<>();
	}
	
	public void readFile(String path, SequenceType type) throws FileNotFoundException, IOException, InvalidSequenceException {
		try(
			FileReader rd = new FileReader(path);
		) {
			readStream(rd, type);
		}
	}
	
	public void readStream(Reader rd, SequenceType type) throws IOException, InvalidSequenceException {
		BufferedReader br = new BufferedReader(rd);		
		if( !br.readLine().equalsIgnoreCase("##gff-version 3") )
			throw new FileFormatException("Invalid GFF signature");	
		if( readFeatures(br) )
			readFasta(br, type);
	}
	
	public List<Feature> getFeatures() {
		return features;
	}
	
	public Fasta getFasta(String seqid) {
		return mapFasta.get(seqid);
	}
	
	private void readFasta(BufferedReader br, SequenceType type) throws IOException, InvalidSequenceException {
		if( type == null )
			type = SequenceType.DNA;
		for( Fasta fasta : Fasta.readEntries(br, type) )
			mapFasta.put(fasta.getEntry(), fasta);
	}

	private boolean readFeatures(BufferedReader br) throws IOException {
		String line;
		String[] fields;
		String[] attributes;
		String[] pair;
		while( (line=br.readLine()) != null ) {
			if( line.equalsIgnoreCase("##FASTA") )
				return true;
			fields = line.split("\t");
			if( fields.length != 9 )
				throw new FileFormatException("Invalid GFF file");
			Feature f = new Feature();
			f.setSeqid(getString(fields[0]));
			f.setSource(getString(fields[1]));
			f.setType(getString(fields[2]));
			f.setStart(getInteger(fields[3]));
			f.setEnd(getInteger(fields[4]));
			f.setScore(getDouble(fields[5]));
			f.setStrand(getString(fields[6]));
			f.setPhase(getInteger(fields[7]));
			if( getString(fields[8]) != null ) {
				attributes = fields[8].split(";");
				for( String attribute : attributes ) {
					pair = attribute.split("=");
					f.setAttribute(pair[0], pair[1]);
				}
			}
			features.add(f);
		}
		return false;
	}
	
	private String getString(String field) {
		if( field.equals(".") )
			return null;
		return field;
	}
	
	private Integer getInteger(String field) {
		if( field.equals(".") )
			return null;
		return Integer.parseInt(field);
	}
	
	private Double getDouble(String field) {
		if( field.equals(".") )
			return null;
		return Double.parseDouble(field);
	}
	
	private final List<Feature> features = new ArrayList<>();
	private final Map<String, Fasta> mapFasta = new HashMap<>();
}
