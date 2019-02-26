package es.ehubio.dubase.dl;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Superfamily database table.
 * 
 */
@Entity
@NamedQuery(name="Superfamily.findAll", query="SELECT s FROM Superfamily s")
public class Superfamily implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String shortname;
	private Clazz clazz;

	public Superfamily() {
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


	public String getShortname() {
		return this.shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}


	//uni-directional many-to-one association to Clazz
	@ManyToOne
	@JoinColumn(name="class")
	public Clazz getClazz() {
		return this.clazz;
	}

	public void setClazz(Clazz clazz) {
		this.clazz = clazz;
	}

}