package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


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
	private List<Modification> modifications;

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


	//bi-directional many-to-one association to Modification
	@OneToMany(mappedBy="modType")
	public List<Modification> getModifications() {
		return this.modifications;
	}

	public void setModifications(List<Modification> modifications) {
		this.modifications = modifications;
	}

	public Modification addModification(Modification modification) {
		getModifications().add(modification);
		modification.setModType(this);

		return modification;
	}

	public Modification removeModification(Modification modification) {
		getModifications().remove(modification);
		modification.setModType(null);

		return modification;
	}

}