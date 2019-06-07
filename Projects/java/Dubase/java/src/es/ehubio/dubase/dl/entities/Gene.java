package es.ehubio.dubase.dl.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


/**
 * The persistent class for the Gene database table.
 * 
 */
@Entity
@NamedQueries({
@NamedQuery(name="Gene.findAll", query="SELECT g FROM Gene g"),
@NamedQuery(name="Gene.findByName", query="SELECT g FROM Gene g WHERE g.name = :name")
})
public class Gene implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String description;
	private String name;

	public Gene() {
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


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}