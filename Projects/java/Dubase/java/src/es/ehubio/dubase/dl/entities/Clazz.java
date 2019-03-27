package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Class database table.
 * 
 */
@Entity
@Table(name="Class")
@NamedQuery(name="Clazz.findAll", query="SELECT c FROM Clazz c")
public class Clazz implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;

	public Clazz() {
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