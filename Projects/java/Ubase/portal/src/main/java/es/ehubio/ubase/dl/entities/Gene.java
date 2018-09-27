package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Gene database table.
 * 
 */
@Entity
@NamedQuery(name="Gene.findAll", query="SELECT g FROM Gene g")
public class Gene implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String accession;
	private String descriptionn;
	private String name;

	public Gene() {
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


	public String getDescriptionn() {
		return this.descriptionn;
	}

	public void setDescriptionn(String descriptionn) {
		this.descriptionn = descriptionn;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}