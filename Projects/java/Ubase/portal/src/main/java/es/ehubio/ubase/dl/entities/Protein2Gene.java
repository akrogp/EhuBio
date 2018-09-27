package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Protein2Gene database table.
 * 
 */
@Entity
@NamedQuery(name="Protein2Gene.findAll", query="SELECT p FROM Protein2Gene p")
public class Protein2Gene implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private Gene geneBean;
	private Protein proteinBean;

	public Protein2Gene() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	//uni-directional many-to-one association to Gene
	@ManyToOne
	@JoinColumn(name="gene")
	public Gene getGeneBean() {
		return this.geneBean;
	}

	public void setGeneBean(Gene geneBean) {
		this.geneBean = geneBean;
	}


	//uni-directional many-to-one association to Protein
	@ManyToOne
	@JoinColumn(name="protein")
	public Protein getProteinBean() {
		return this.proteinBean;
	}

	public void setProteinBean(Protein proteinBean) {
		this.proteinBean = proteinBean;
	}

}