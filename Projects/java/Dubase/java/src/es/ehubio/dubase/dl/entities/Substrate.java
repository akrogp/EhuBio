package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


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
	private List<Ambiguity> ambiguities;

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


	//bi-directional many-to-one association to Ambiguity
	@OneToMany(mappedBy="substrateBean")
	public List<Ambiguity> getAmbiguities() {
		return this.ambiguities;
	}

	public void setAmbiguities(List<Ambiguity> ambiguities) {
		this.ambiguities = ambiguities;
	}

	public Ambiguity addAmbiguity(Ambiguity ambiguity) {
		getAmbiguities().add(ambiguity);
		ambiguity.setSubstrateBean(this);

		return ambiguity;
	}

	public Ambiguity removeAmbiguity(Ambiguity ambiguity) {
		getAmbiguities().remove(ambiguity);
		ambiguity.setSubstrateBean(null);

		return ambiguity;
	}

}