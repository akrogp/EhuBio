package es.ehubio.proteomics;

/**
 * This class holds all the masses used for the calculation of theoretical
 * masses.
 * 
 * Original author: Thilo Muth
 * 
 * The masses for all the amino acids including empty values for non-amino acid letters
 * Update on 05/25/10: Using NEW mono isotopic reference masses due to wrong values.
 *
 */
public enum Masses {
	A('A',71.037110),
	B('B',114.534930),
    C('C',103.009185),
    D('D',115.026943),
    E('E',129.042593),
    F('F',147.068414),
    G('G',57.021464),
    H('H',137.058912),
    I('I',113.084064),
    J('J',0.000000),
    K('K',128.094963),
    L('L',113.084064),
    M('M',131.040485),
    N('N',114.042927),
    O('O',0.000000),
    P('P',97.052764),
    Q('Q',128.058578),
    R('R',156.101111),
    S('S',87.032028),
    T('T',101.047679),
    U('U',0.000000),
    V('V',99.068414),
    W('W',186.079313),
    X('X',111.000000),
    Y('Y',163.06332),
    Z('Z',128.550590);
	
	public static final double Hydrogen = 1.007825;
    public static final double Carbon = 12.000000;
    public static final double Nitrogen = 14.003070;
    public static final double Oxygen = 15.994910;
    public static final double Electron = 0.005490;
    public static final double C_term = Oxygen+Hydrogen;
    public static final double N_term = Hydrogen;    
    public static final double H2O = 2*Hydrogen+Oxygen;
    public static final double NH3 = Nitrogen+3*Hydrogen;
	
	private Masses( char letter, double mass ) {
		this.letter = letter;
		this.mass = mass;
	}
	
	public char getLetter() {
		return letter;
	}

	public double getMass() {
		return mass;
	}

	private final char letter;
	private final double mass;    
}