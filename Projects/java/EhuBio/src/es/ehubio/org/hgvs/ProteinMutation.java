package es.ehubio.org.hgvs;

import es.ehubio.model.Aminoacid;

public class ProteinMutation {
	public enum Type {Frameshift, Nonsense, Missense, Synonymous, Unknown};
	
	public Aminoacid getOriginal() {
		return original;
	}
	
	public void setOriginal(Aminoacid original) {
		this.original = original;
	}
	
	public Aminoacid getMutated() {
		return mutated;
	}
	
	public void setMutated(Aminoacid mutated) {
		this.mutated = mutated;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	private Aminoacid original;
	private Aminoacid mutated;
	private int position;
	private Type type;
}