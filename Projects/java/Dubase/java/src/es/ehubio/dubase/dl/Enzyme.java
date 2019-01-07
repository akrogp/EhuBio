package es.ehubio.dubase.dl;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Enzyme database table.
 * 
 */
@Entity
@NamedQuery(name="Enzyme.findAll", query="SELECT e FROM Enzyme e")
public class Enzyme implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String description;
	private String gene;
	private Superfamily superfamilyBean;

	public Enzyme() {
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


	//uni-directional many-to-one association to Superfamily
	@ManyToOne
	@JoinColumn(name="superfamily")
	public Superfamily getSuperfamilyBean() {
		return this.superfamilyBean;
	}

	public void setSuperfamilyBean(Superfamily superfamilyBean) {
		this.superfamilyBean = superfamilyBean;
	}

}