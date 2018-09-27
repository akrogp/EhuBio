package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Modification database table.
 * 
 */
@Entity
@NamedQuery(name="Modification.findAll", query="SELECT m FROM Modification m")
public class Modification implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private double deltaMass;
	private String description;
	private String name;

	public Modification() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public double getDeltaMass() {
		return this.deltaMass;
	}

	public void setDeltaMass(double deltaMass) {
		this.deltaMass = deltaMass;
	}


	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}