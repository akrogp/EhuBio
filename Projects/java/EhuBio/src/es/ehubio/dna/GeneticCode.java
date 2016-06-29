package es.ehubio.dna;

import java.util.HashMap;
import java.util.Map;

import es.ehubio.model.Aminoacid;

public class GeneticCode {
	
	public static Map<String, Codon> getStandard() {
		if( standard == null )
			standard = createStandard();
		return standard;
	}
	
	private static Map<String, Codon> createStandard() {
		Map<String, Codon> map = new HashMap<>();
		addCodons(map,Aminoacid.ALANINE,false,false,"GCT, GCC, GCA, GCG");
		addCodons(map,Aminoacid.ARGININE,false,false,"CGT, CGC, CGA, CGG, AGA, AGG");
		addCodons(map,Aminoacid.ASPARAGINE,false,false,"AAT, AAC");
		addCodons(map,Aminoacid.ASPARTIC,false,false,"GAT, GAC");
		addCodons(map,Aminoacid.CYSTEINE,false,false,"TGT, TGC");
		addCodons(map,Aminoacid.GLUTAMINE,false,false,"CAA, CAG");
		addCodons(map,Aminoacid.GLUTAMIC,false,false,"GAA, GAG");
		addCodons(map,Aminoacid.GLYCINE,false,false,"GGT, GGC, GGA, GGG");
		addCodons(map,Aminoacid.HISTIDINE,false,false,"CAT, CAC");
		addCodons(map,Aminoacid.ISOLEUCINE,false,false,"ATT, ATC, ATA");
		addCodons(map,Aminoacid.LEUCINE,false,false,"TTA, TTG, CTT, CTC, CTA, CTG");
		addCodons(map,Aminoacid.LYSINE,false,false,"AAA, AAG");
		addCodons(map,Aminoacid.METHIONINE,true,false,"ATG");
		addCodons(map,Aminoacid.PHENYLALANINE,false,false,"TTT, TTC");
		addCodons(map,Aminoacid.PROLINE,false,false,"CCT, CCC, CCA, CCG");
		addCodons(map,Aminoacid.SERINE,false,false,"TCT, TCC, TCA, TCG, AGT, AGC");
		addCodons(map,Aminoacid.THREONINE,false,false,"ACT, ACC, ACA, ACG");
		addCodons(map,Aminoacid.TRYPTOPHAN,false,false,"TGG");
		addCodons(map,Aminoacid.TYROSINE,false,false,"TAT, TAC");
		addCodons(map,Aminoacid.VALINE,false,false,"GTT, GTC, GTA, GTG");
		addCodons(map,null,false,true,"TAA, TGA, TAG");
		return map;
	}
	
	private static void addCodons(Map<String, Codon> map, Aminoacid aa, boolean start, boolean stop, String codons) {
		String[] list = codons.split(", ");
		for( String seq : list ) {
			Codon codon = new Codon();
			codon.setStart(start);
			codon.setStop(stop);
			codon.setAa(aa);
			map.put(seq, codon);
		}
	}

	private static Map<String, Codon> standard;
}
