package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


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
	private List<Superfamily> superfamilies;

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


	//bi-directional many-to-one association to Superfamily
	@OneToMany(mappedBy="clazz")
	public List<Superfamily> getSuperfamilies() {
		return this.superfamilies;
	}

	public void setSuperfamilies(List<Superfamily> superfamilies) {
		this.superfamilies = superfamilies;
	}

	public Superfamily addSuperfamily(Superfamily superfamily) {
		getSuperfamilies().add(superfamily);
		superfamily.setClazz(this);

		return superfamily;
	}

	public Superfamily removeSuperfamily(Superfamily superfamily) {
		getSuperfamilies().remove(superfamily);
		superfamily.setClazz(null);

		return superfamily;
	}

}