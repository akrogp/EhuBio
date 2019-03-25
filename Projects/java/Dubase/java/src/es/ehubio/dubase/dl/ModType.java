package es.ehubio.dubase.dl;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the ModType database table.
 * 
 */
@Entity
@NamedQuery(name="ModType.findAll", query="SELECT m FROM ModType m")
public class ModType implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;

	public ModType() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}