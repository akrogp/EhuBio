package es.ehubio.proteomics;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Score {
	private ScoreType type;
	private String name;
	private double value;
	
	public Score() {		
	}
	
	public Score( ScoreType type, String name, double value ) {
		this.type = type;
		this.name = name;
		this.value = value;
	}
	
	public Score( ScoreType type, double value ) {
		this.type = type;
		this.name = type.getName();
		this.value = value;
	}
	
	@XmlAttribute
	public ScoreType getType() {
		return type;
	}
		
	public void setType(ScoreType type) {
		this.type = type;
	}
	
	@XmlAttribute
	public String getName() {
		if( name != null )
			return name;
		if( type != null )
			return type.getName();
		return null;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public int compare( double value2 ) {
		return type.compare(value, value2);
	}
	
	@Override
	public String toString() {
		return String.format("%s=%s", getName(), getValue());
	}
}
