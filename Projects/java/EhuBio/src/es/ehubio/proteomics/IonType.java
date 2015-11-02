package es.ehubio.proteomics;

import java.util.HashMap;
import java.util.Map;

public enum IonType {
	PRECURSOR("precursor","frag: precursor ion",null,"MS:1001523"),
	PRECURSOR_H2O("precursor","frag: precursor ion - H2O","H2O","MS:1001521"),
	PRECURSOR_NH3("precursor","frag: precursor ion - NH3","NH3","MS:1001522"),
	A("a","frag: a ion",null,"MS:1001229"),
	A_H2O("a","frag: a ion - H2O","H2O","MS:1001234"),
	A_NH3("a","frag: a ion - NH3","NH3","MS:1001235"),
	B("b","frag: b ion",null,"MS:1001224"),
	B_H2O("b","frag: b ion - H2O","H2O","MS:1001222"),
	B_NH3("b","frag: b ion - NH3","NH3","MS:1001232"),
	C("c","frag: c ion",null,"MS:1001231"),
	C_H2O("c","frag: c ion - H2O","H2O","MS:1001515"),
	C_NH3("c","frag: c ion - NH3","NH3","MS:1001516"),
	X("x","frag: x ion",null,"MS:1001228"),
	X_H2O("x","frag: x ion - H2O","H2O","MS:1001519"),
	X_NH3("x","frag: x ion - NH3","NH3","MS:1001520"),
	Y("y","frag: y ion",null,"MS:1001220"),
	Y_H2O("y","frag: y ion - H2O","H2O","MS:1001223"),
	Y_NH3("y","frag: y ion - NH3","NH3","MS:1001233"),
	Z("z","frag: z ion",null,"MS:1001230"),
	Z_H2O("z","frag: z ion - H2O","H2O","MS:1001517"),
	Z_NH3("z","frag: z ion - NH3","NH3","MS:1001518"),
	IMMONIUM("immonium","frag: immonium ion",null,"MS:1001239");
	
	private IonType( String code, String name, String loss, String accession ) {
		this.code = code;
		this.name = name;
		this.loss = loss;
		this.accession = accession;
	}
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getAccession() {
		return accession;
	}
	
	public String getLoss() {
		return loss;
	}	
	
	public static IonType getByAccession( String accession ) {
		return mapAccession.get(accession);
	}
	
	public static IonType getByName( String name ) {
		return mapName.get(name);
	}

	private final String code;
	private final String name;
	private final String loss;
	private final String accession;
	
	private final static Map<String,IonType> mapAccession = new HashMap<>();	
	private final static Map<String,IonType> mapName = new HashMap<>();
	static {
		for( IonType type : IonType.values() ) {
			if( type.getAccession() != null )
				mapAccession.put(type.getAccession(), type);
			if( type.getName() != null )
				mapName.put(type.getName(), type);
		}
	}
}