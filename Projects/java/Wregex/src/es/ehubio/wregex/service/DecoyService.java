package es.ehubio.wregex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.db.uniprot.xml.Entry;
import es.ehubio.db.uniprot.xml.PositionType;

public class DecoyService {
	public static class Region {
		private int i1, i2;

		public int getBegin() {
			return i1;
		}

		public void setBegin(int i1) {
			if( i1 <= 0 )
				throw new IllegalArgumentException("Index is zero-based (starting at 1)");
			/*if( i1 > i2 )
				throw new IllegalArgumentException("Begin should be less or equal than end");*/
			this.i1 = i1;
		}

		public int getEnd() {
			return i2;
		}

		public void setEnd(int i2) {
			/*if( i2 < i1 )
				throw new IllegalArgumentException("End should be greater or equal than begin");*/
			this.i2 = i2;
		}
	}
	
	public static Fasta getDecoyUniprot(Fasta input, Entry uniprot, String decoyPrefix) {
		List<Region> regions = new ArrayList<>();
		
		if( uniprot != null && uniprot.getFeature() != null ) {
			if( uniprot.getSequence() != null && uniprot.getSequence().getLength() == input.getSequence().length() ) {
				uniprot.getFeature().stream()
					.filter(feat -> feat.getDescription() != null && feat.getDescription().contains("isordered"))
					.forEach(feat -> {
						PositionType fbegin = feat.getLocation().getBegin();
						PositionType fend = feat.getLocation().getEnd();
						if( fbegin != null && fend != null ) {
							Region region = new Region();
							region.setBegin(fbegin.getPosition().intValue());
							region.setEnd(fend.getPosition().intValue());
							regions.add(region);
						}
					});
			}
		}
		
		return getDecoy(input, regions, decoyPrefix);
	}

	public static Fasta getDecoy(Fasta input, List<Region> regions, String decoyPrefix) {
		Collections.sort(regions, new Comparator<Region>() {
			@Override
			public int compare(Region r1, Region r2) {
				int comp = r1.getBegin() - r2.getBegin();
				if( comp == 0 )
					comp = r1.getEnd() - r2.getEnd();
				return comp;
			}
		});
		String seq = input.getSequence();
		List<String> seqs = new ArrayList<>();
		seqs.add(seq.charAt(0)+"");	// preserve fist aa
		int i1 = 1;	// skip first aa (zero-based index)
		for( Region region : regions ) {
			int i2 = region.i1-1;	// convert to zero-based index
			if( i2 < i1 )
				i2 = i1;
			if( region.i2 > i2 ) {
				seqs.add(seq.substring(i1, i2));
				seqs.add(seq.substring(i2, region.i2));
				i1 = region.i2;
			}
		}
		seqs.add(seq.substring(i1));
		
		return buildDecoy(input, seqs, decoyPrefix, regions != null && !regions.isEmpty());
	}

	private static Fasta buildDecoy(Fasta input, List<String> seqs, String decoyPrefix, boolean preserveIdr) {
		StringBuilder str = new StringBuilder();
		for( String seq : seqs )
			str.append(shuffle(seq));
		String desc = String.format("Decoy for %s %s", input.getAccession(), preserveIdr ? "preserving amino acid composition of known IDRs" : "without known IDRs");
		return new Fasta(decoyPrefix+input.getAccession(), desc, str.toString(), SequenceType.PROTEIN);
	}

	private static String shuffle(String seq) {
		List<Character> list = new ArrayList<>(seq.length());
		for( int i = 0; i < seq.length(); i++ )
			list.add(seq.charAt(i));
		Collections.shuffle(list);
		return list.stream().map(String::valueOf).collect(Collectors.joining());
	}
}
