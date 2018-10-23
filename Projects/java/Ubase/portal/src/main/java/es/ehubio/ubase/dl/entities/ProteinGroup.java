package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the ProteinGroup database table.
 * 
 */
@Entity
@NamedQuery(name="ProteinGroup.findAll", query="SELECT p FROM ProteinGroup p")
public class ProteinGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;

	public ProteinGroup() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}