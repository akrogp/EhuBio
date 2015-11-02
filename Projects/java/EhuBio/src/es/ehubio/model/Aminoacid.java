package es.ehubio.model;

import java.util.HashMap;
import java.util.Map;

public enum Aminoacid {
	ALANINE("Alanine",'A',"Ala",89.09404,6.01,2.35,9.87,-1.00,true,false,(short)0,true,true,false,false),
	CYSTEINE("Cysteine",'C',"Cys",121.15404,5.05,1.92,10.70,8.18,false,false,(short)0,true,false,false,false),
	ASPARTIC("Aspartic acid",'D',"Asp",133.10384,2.85,1.99,9.90,3.90,false,true,(short)-1,true,false,false,false),
	GLUTAMIC("Glutamic acid",'E',"Glu",147.13074,3.15,2.10,9.47,4.07,false,true,(short)-1,false,false,false,false),
	PHENYLALANINE("Phenylalanine",'F',"Phe",165.19184,5.49,2.20,9.31,-1.00,true,false,(short)0,false,false,true,false),
	GLYCINE("Glycine",'G',"Gly",75.06714,6.06,2.35,9.78,-1.00,true,false,(short)0,true,true,false,false),
	HISTIDINE("Histidine",'H',"His",155.15634,7.60,1.80,9.33,6.04,false,true,(short)1,false,false,true,false),
	ISOLEUCINE("Isoleucine",'I',"Ile",131.17464,6.05,2.32,9.76,-1.00,true,false,(short)0,false,false,false,true),
	LYSINE("Lysine",'K',"Lys",146.18934,9.60,2.16,9.06,10.54,false,true,(short)1,false,false,false,false),
	LEUCINE("Leucine",'L',"Leu",131.17464,6.01,2.33,9.74,-1.00,true,false,(short)0,false,false,false,true),
	METHIONINE("Methionine",'M',"Met",149.20784,5.74,2.13,9.28,-1.00,true,false,(short)0,false,false,false,false),
	ASPARAGINE("Asparagine",'N',"Asn",132.11904,5.41,2.14,8.72,-1.00,false,true,(short)0,true,false,false,false),
	PYRROLYSINE("Pyrrolysine",'O',"Pyl",-1.00000,-1.00,-1.00,-1.00,-1.00,false,false,(short)0,false,false,false,false),
	PROLINE("Proline",'P',"Pro",115.13194,6.30,1.95,10.64,-1.00,true,false,(short)0,true,false,false,false),
	GLUTAMINE("Glutamine",'Q',"Gln",146.14594,5.65,2.17,9.13,-1.00,false,true,(short)0,false,false,false,false),
	ARGININE("Arginine",'R',"Arg",174.20274,10.76,1.82,8.99,12.48,false,true,(short)1,false,false,false,false),
	SERINE("Serine",'S',"Ser",105.09344,5.68,2.19,9.21,-1.00,false,true,(short)0,true,true,false,false),
	THREONINE("Threonine",'T',"Thr",119.12034,5.60,2.09,9.10,-1.00,false,true,(short)0,true,false,false,false),
	SELENOCYSTEINE("Selenocysteine",'U',"Sec",168.05300,-1.00,-1.00,-1.00,5.73,true,false,(short)0,true,false,false,false),
	VALINE("Valine",'V',"Val",117.14784,6.00,2.39,9.74,-1.00,true,false,(short)0,true,false,false,true),
	TRYPTOPHAN("Tryptophan",'W',"Trp",204.22844,5.89,2.46,9.41,-1.00,true,false,(short)0,false,false,true,false),
	TYROSINE("Tyrosine",'Y',"Tyr",181.19124,5.64,2.20,9.21,10.46,false,true,(short)0,false,false,true,false),
	ASX("Aspartic acid or Asparagine",'B',"Asx",-1.00000,-1.00,-1.00,-1.00,-1.00,false,true,(short)0,true,false,false,false),
	GLX("Glutamic acid or Glutamine",'Z',"Glx",-1.00000,-1.00,-1.00,-1.00,-1.00,false,true,(short)0,false,false,false,false);
	
	
	public final String name;
	public final char letter;
	public final String abbrev;
	public final double mass;
	public final double pI;
	public final double pK1;
	public final double pK2;
	public final double pKa;
	public final boolean isAcidic;
	public final boolean isBasic;
	public final boolean isHydrophobic;
	public final boolean isPolar;
	public final short charge;
	public final boolean isPositive;
	public final boolean isNegative;
	public final boolean isSmall;
	public final boolean isTiny;
	public final boolean isAromatic;
	public final boolean isAliphatic;
	
	private Aminoacid( String name, char letter, String abbrev, double mass,
		double pI, double pK1, double pK2, double pKa,
		boolean isHydrophobic, boolean isPolar, short charge,
		boolean isSmall, boolean isTiny, boolean isAromatic, boolean isAliphatic ) {
		this.name = name;
		this.letter = letter;
		this.abbrev = abbrev;
		this.mass = mass;
		this.pI = pI;
		isAcidic = pI < 7.0;
		isBasic = pI > 7.0;
		this.pK1 = pK1;
		this.pK2 = pK2;
		this.pKa = pKa;
		this.isHydrophobic = isHydrophobic;
		this.isPolar = isPolar;
		this.charge = charge;
		isPositive = charge > 0;
		isNegative = charge < 0;
		this.isSmall = isSmall;
		this.isTiny = isTiny;
		this.isAromatic = isAromatic;
		this.isAliphatic = isAliphatic;		
	}
	
	private static void createLut() {
		char max = 'A';
		for( Aminoacid aa : values() )
			if( aa.letter > max )
				max = aa.letter;
		int size = max - 'A' + 1;
		lut = new Aminoacid[size];
		for( int i = 0; i < size; i++ )
			lut[i] = null;
		for( Aminoacid aa : values() )
			lut[aa.letter-'A'] = aa;
	}
	
	private static void createMap() {
		map = new HashMap<>();
		for( Aminoacid aa : values() )
			map.put(aa.abbrev, aa);
	}
	
	public static Aminoacid parseLetter( char ch ) {
		if( lut == null )
			createLut();
		int i = Character.toUpperCase(ch) - 'A';
		if( i < 0 || i >= lut.length )
			return null;
		return lut[i]; 
	}
	
	public static Aminoacid parseAbbrev( String abbrev ) {
		if( map == null )
			createMap();
		return map.get(abbrev);
	}
	
	public static Aminoacid parse( String str ) {
		if( str.length() == 1 )
			return parseLetter(str.charAt(0));
		if( str.length() == 3 )
			return parseAbbrev(str);
		return null;
	}
	
	public boolean equals( char ch ) {
		return Character.toUpperCase(ch) == letter; 
	}
	
	private static Aminoacid[] lut = null;	// Look-up table
	private static Map<String,Aminoacid> map;
}
