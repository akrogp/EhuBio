package es.ehubio.db.fasta;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import es.ehubio.io.Streams;
import es.ehubio.io.UnixCfgReader;
import es.ehubio.model.Aminoacid;
import es.ehubio.model.Nucleotide;

public final class Fasta {
	private final String sequence;
	private final String header;
	private final SequenceType type;
	private final String entry;
	private final String accession;
	private final String description;
	private final String proteinName;
	private final String geneName;
	private final String geneAccession;
	
	public enum SequenceType {
		PROTEIN, DNA, RNA
	}
	
	public static class InvalidSequenceException extends Exception {
		private static final long serialVersionUID = 1L;
		public InvalidSequenceException() {
			super("Illegal character found in fasta sequence");
		}
		public InvalidSequenceException( String desc ) {
			super(desc);
		}
		public InvalidSequenceException( char ch ) {
			super("Illegal character '" + ch + "' found in fasta sequence");
		}
	}
	
	public Fasta( String accession, String description, String sequence, SequenceType type ) {
		StringBuilder header = new StringBuilder();
		if( accession != null ) {
			header.append(accession);
			header.append(' ');
		}
		if( description != null )
			header.append(description);
		this.header = header.toString();
		this.sequence = trim(sequence);
		this.type = type;
		this.accession = accession;
		this.entry = accession;
		this.description = description;
		proteinName = null;
		geneName = null;
		geneAccession = null;
	}
	
	public Fasta( String header, String sequence, SequenceType type ) throws InvalidSequenceException {
		this(header, guessParser(header), sequence, type);
	}
	
	public Fasta( String header, HeaderParser parser, String sequence, SequenceType type ) throws InvalidSequenceException {
		assert header != null && sequence != null;
		this.header = header;
		this.sequence = trim(sequence);		
		this.type = type;
		checkSequence(this.sequence, type);
		this.entry = header.split("[ \t]")[0];
		if( parser == null ) {
			accession = null;
			description = null;
			proteinName = null;
			geneName = null;
			geneAccession = null;
		} else {
			accession = parser.getAccession();
			description = parser.getDescription();
			proteinName = parser.getProteinName();
			geneName = parser.getGeneName();
			geneAccession = parser.getGeneAccession();
		}
	}
	
	public Fasta( String fasta, SequenceType type ) throws InvalidSequenceException {
		int off = fasta.indexOf('\n');
		header = fasta.substring(0, off).trim();
		sequence = trim(fasta.substring(off+1));
		this.type = type;
		checkSequence(sequence, type);
		HeaderParser parser = guessParser(header);
		this.entry = header.split("[ \t]")[0];
		if( parser == null ) {
			accession = null;
			description = null;
			proteinName = null;
			geneName = null;
			geneAccession = null;
		} else {
			accession = parser.getAccession();
			description = parser.getDescription();
			proteinName = parser.getProteinName();
			geneName = parser.getGeneName();
			geneAccession = parser.getGeneAccession();
		}
	}
	
	private static String trim( String seq ) {
		return seq.trim().replaceAll("[ \t\r\n]", "");
	}
	
	public static HeaderParser guessParser( String header ) {
		if( header == null )
			return null;
		HeaderParser parser = new UniprotParser();
		if( parser.parse(header) )
			return parser;
		parser = new NextprotParser();
		if( parser.parse(header) )
			return parser;
		parser = new GencodeParser();
		if( parser.parse(header) )
			return parser;
		
		parser = new DefaultParser();
		parser.parse(header);		
		return parser;
	}
	
	public static void checkSequence( String sequence, SequenceType type ) throws InvalidSequenceException {
		if( sequence.isEmpty() )
			throw new InvalidSequenceException("Empty sequence");
		List<Character> chars = new ArrayList<Character>();
		switch( type ) {
			case DNA:
				for( Nucleotide n : Nucleotide.values() )
					if( n.isDNA ) chars.add(Character.toUpperCase(n.symbol));
				break;			
			case RNA:
				for( Nucleotide n : Nucleotide.values() )
					if( n.isRNA ) chars.add(Character.toUpperCase(n.symbol));
				break;
			case PROTEIN:
				sequence = sequence.replaceAll("\\*.*", ""); // Stop codon
				for( Aminoacid a : Aminoacid.values() )
					chars.add(Character.toUpperCase(a.letter));
				break;
		}
		for( char c : sequence.toUpperCase().toCharArray() )
			if( !chars.contains(c) )
				throw new InvalidSequenceException(c);
	}
	
	public String getSequence() {
		return sequence;
	}
	
	public String getHeader() {
		return header;
	}
	
	public SequenceType getType() {
		return type;
	}
	
	public String getEntry() {
		return entry;
	}
	
	public String getAccession() {
		return accession;
	}
	
	public String getProteinName() {
		return proteinName;
	}
	
	public String getGeneName() {
		return geneName;
	}
	
	public String getGeneAccession() {
		return geneAccession;
	}
	
	public static List<Fasta> readEntries( String path, SequenceType type ) throws IOException, InvalidSequenceException {
		Reader rd = Streams.getTextReader(path);
		List<Fasta> list = readEntries(rd, type);
		rd.close();
		return list;
	}
	
	public static List<Fasta> readEntries( Reader rd, SequenceType type ) throws IOException, InvalidSequenceException {
		List<Fasta> list = new ArrayList<Fasta>();
		UnixCfgReader br = new UnixCfgReader(rd);
		String line, header = null;
		StringBuilder sequence = new StringBuilder();
		while( (line=br.readLine()) != null ) {
			if( line.startsWith(">") ) {
				if( header != null )
					list.add(new Fasta(header, sequence.toString(), type));
				header = line.substring(1).trim();
				sequence = new StringBuilder();					
			} else
				sequence.append(line);
		}
		if( header != null )
			list.add(new Fasta(header, sequence.toString(), type));
		return list;
	}
	
	public static void writeEntries( String path, Iterable<Fasta> list) throws FileNotFoundException, IOException {
		Writer wr = Streams.getTextWriter(path);
		writeEntries(wr, list);
		wr.close();
	}
	
	public static void writeEntries( Writer wr, Iterable<Fasta> list) {
		PrintWriter pw = new PrintWriter(new BufferedWriter(wr));
		for( Fasta f : list ) {
			pw.println(">" + f.getHeader());
			pw.println(f.getSequence());
		}
		pw.flush();
	}

	public String getDescription() {
		return description;
	}
	
	public static String formatSequence( final String seq, final int cols ) {
		if( seq == null || seq.isEmpty() )
			return "";				
		final int colSize = 10;
		int i = 0;
		int len;
		int col = 0;
		final int digits = (int)Math.log10(seq.length())+1;
		final StringBuilder builder = new StringBuilder();
		
		while( i < seq.length() ) {
			if( col == 0 )
				builder.append(String.format("%0"+digits+"d ", i+1));
			len = (i+colSize) < seq.length() ? colSize : (seq.length()-i);
			builder.append(seq.substring(i, i+len));
			i += len;			
			if( i >= seq.length() )
				break;
			if( ++col < cols ) {
				builder.append(' ');
			} else {
				col = 0;
				builder.append('\n');
				//builder.append(String.format(" %0"+digits+"d\n", i));
			}
		}
		
		return builder.toString();
	}
}
