package es.ehubio.proteomics.pipeline;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.io.Streams;
import es.ehubio.model.Aminoacid;
import es.ehubio.proteomics.Enzyme;

public class DecoyDb {
	public static enum Strategy { REVERSE, SHUFFLE, PSEUDO_REVERSE }
	
	public static void create( String targetPath, String decoyPath, Strategy strategy, Enzyme enzyme, String decoyPrefix) throws FileNotFoundException, IOException, InvalidSequenceException {
		Reader targetReader = Streams.getTextReader(targetPath); 
		Writer decoyWriter = Streams.getTextWriter(decoyPath);
		create(targetReader,decoyWriter,strategy,enzyme,decoyPrefix);
		targetReader.close();
		decoyWriter.close();
	}

	public static void create(Reader targetReader, Writer decoyWriter, Strategy strategy, Enzyme enzyme, String decoyPrefix) throws IOException, InvalidSequenceException {
		List<Fasta> targets = Fasta.readEntries(targetReader, SequenceType.PROTEIN);
		List<Fasta> decoys = new ArrayList<>();
		
		for( Fasta target : targets )
			decoys.add(getDecoy(target, strategy, enzyme, decoyPrefix));
		Fasta.writeEntries(decoyWriter, decoys);
	}
	
	public static Fasta getDecoy(Fasta target, Strategy strategy, Enzyme enzyme, String decoyPrefix) {
		String seq = getDecoy(target.getSequence(), strategy, enzyme);
		String desc = String.format("Decoy for %s using %s strategy", target.getAccession(), strategy);
		if( enzyme != null )
			desc = String.format("%s with %s", desc, enzyme);
		return new Fasta(decoyPrefix+target.getAccession(), desc, seq, SequenceType.PROTEIN);
	}

	public static String getDecoy(String sequence, Strategy strategy, Enzyme enzyme) {
		if( sequence == null || sequence.isEmpty() )
			return sequence;
		switch( strategy ) {			
			case REVERSE:
				return reverse(sequence);
			case SHUFFLE:
				return shuffle(sequence);
			case PSEUDO_REVERSE:
				if( enzyme == Enzyme.TRYPSIN || enzyme == Enzyme.TRYPSINP )
					return pseudoReverseTrypsin(sequence);
				return pseudoReverse(sequence, enzyme);
			default:
				throw new UnsupportedOperationException(String.format("Decoy strategy %s not supported", strategy));
		}
	}
	
	// Reverse
	
	private static String reverse( String seq ) {
		if( seq.isEmpty() )
			return seq;
		StringBuilder rev = new StringBuilder();
		char[] chars = seq.toCharArray();
		int last = chars.length-1;
		for( int i = 0; i <= last; i++ )
			rev.append(chars[last-i]);
		String result = rev.toString();
		if( result.equalsIgnoreCase(seq) )	// Palindrome
			return shuffle(seq);
		return result;
	}
	
	// Shuffle (respecting first amino acid)
	
	private static String shuffle( String seq ) {
		if( seq == null || seq.length() < 3 )
			return seq;
		List<Character> list = new ArrayList<>(seq.length()-1);
		for( int i = 1; i < seq.length(); i++ )
			list.add(seq.charAt(i));
		
		Collections.shuffle(list);
		
		StringBuilder str = new StringBuilder();
		str.append(seq.charAt(0));
		for( Character c : list )
			str.append(c);
		return str.toString();
	}
	
	// Pseudoreverse
	
	private static String pseudoReverse(String sequence, Enzyme enzyme) {			
		String[] peptides = Digester.digestSequence(sequence, enzyme);
		StringBuilder decoy = new StringBuilder();
		for( int p = 0; p < peptides.length - 1; p++ )
			decoy.append(pseudoReverse(peptides[p]));
		
		String lastPeptide = peptides[peptides.length-1];
		String test = lastPeptide+"AAA";
		if( Digester.digestSequence(test, enzyme).length > 1 )
			decoy.append(pseudoReverse(lastPeptide));
		else
			decoy.append(reverse(lastPeptide));
		
		return decoy.toString();
	}
	
	private static String pseudoReverse( String seq ) {
		int last = seq.length()-1;
		return reverse(seq.substring(0, last))+seq.charAt(last);
	}
	
	// Pseudoreverse with proline rule
	
	private static String pseudoReverseTrypsin(String sequence) {			
		String[] peptides = Digester.digestSequence(sequence, Enzyme.TRYPSINP);
		StringBuilder decoy = new StringBuilder();
		for( int p = 0; p < peptides.length - 1; p++ )
			decoy.append(pseudoReverseP(peptides[p]));
		
		String lastPeptide = peptides[peptides.length-1];		
		if( isTrypsin(lastPeptide, -1) )
			decoy.append(pseudoReverseP(lastPeptide));
		else
			decoy.append(reverseP(lastPeptide));
		
		return decoy.toString();
	}
	
	private static String reverseP( String seq ) {
		if( seq.isEmpty() )
			return seq;
		if( isProline(seq, 0) || isProline(seq, -1) )
			return seq.charAt(0)+reverse(seq.substring(1));		
		String result = reverse(seq);
		if( result.length() > 3 && isProline(result, 0) ) {
			int count = 5;
			do {
				result = shuffle(result);
			} while(--count > 0 && isProline(result, 0));
		}
		return result;
	}
	
	private static String pseudoReverseP( String seq ) {
		int last = seq.length()-1;
		return reverseP(seq.substring(0, last))+seq.charAt(last);
	}
	
	private static boolean isProline(String seq, int i) {
		return isAminoacid(Aminoacid.PROLINE, seq, i);				
	}
	
	private static boolean isTrypsin(String seq, int i) {
		return isAminoacid(Aminoacid.ARGININE, seq, i) || isAminoacid(Aminoacid.LYSINE, seq, i);
	}
	
	private static boolean isAminoacid(Aminoacid aa, String seq, int i) {
		if( i < 0 )
			i = seq.length()-1;
		return aa.equals(seq.charAt(i));
	}
}