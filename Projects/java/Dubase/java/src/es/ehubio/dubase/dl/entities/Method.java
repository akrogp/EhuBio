package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Method database table.
 * 
 */
@Entity
@NamedQuery(name="Method.findAll", query="SELECT m FROM Method m")
public class Method implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String column;
	private String description;
	private double foldThreshold;
	private String instrument;
	private double pvalueThreshold;

	public Method() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getColumn() {
		return this.column;
	}

	public void setColumn(String column) {
		this.column = column;
	}


	@Lob
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public double getFoldThreshold() {
		return this.foldThreshold;
	}

	public void setFoldThreshold(double foldThreshold) {
		this.foldThreshold = foldThreshold;
	}


	public String getInstrument() {
		return this.instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}


	public double getPvalueThreshold() {
		return this.pvalueThreshold;
	}

	public void setPvalueThreshold(double pvalueThreshold) {
		this.pvalueThreshold = pvalueThreshold;
	}

}