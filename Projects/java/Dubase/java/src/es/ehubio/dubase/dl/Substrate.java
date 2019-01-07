package es.ehubio.dubase.dl;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Substrate database table.
 * 
 */
@Entity
@NamedQuery(name="Substrate.findAll", query="SELECT s FROM Substrate s")
public class Substrate implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String description;
	private String gene;

	public Substrate() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getGene() {
		return this.gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

}