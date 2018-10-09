package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Protein database table.
 * 
 */
@Entity
@NamedQuery(name="Protein.findAll", query="SELECT p FROM Protein p")
public class Protein implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String accession;
	private String description;
	private String name;

	public Protein() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getAccession() {
		return this.accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
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