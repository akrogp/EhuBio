package es.ehubio.model;

import java.util.HashMap;
import java.util.Map;

public enum ProteinModificationType {
	PHOSPHORYLATION("Phosphorylation",79.97),
	ACETYLATION("Acetyl",42.01),
	CARBAMIDOMETHYLATION("Carbamidomethyl",57.02),
	OXIDATION("Oxidation",15.99);
	
	private ProteinModificationType(String name, double mass) {
		this.name = name;
		this.mass = mass;
	}
	
	public double getMass() {
		return mass;
	}

	public String getName() {
		return name;
	}
	
	public static ProteinModificationType getByName( String name ) {
		return map.get(name);
	}
	
	public static ProteinModificationType guessFromName( String name ) {
		for( ProteinModificationType item : values() )
			if( name.toUpperCase().contains(item.getName().toUpperCase()) )
				return item;
		return null;
	}
	
	public static ProteinModificationType guessFromMass( double mass ) {
		for( ProteinModificationType item : values() )
			if( Math.abs(item.mass-mass) < 0.01 )
				return item;
		return null;
	}

	private final String name;
	private final double mass;
	private final static Map<String, ProteinModificationType> map = new HashMap<>();
	static {
		for( ProteinModificationType mod : ProteinModificationType.values() )
			map.put(mod.getName(), mod);
	}
}
