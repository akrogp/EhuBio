package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Cell database table.
 * 
 */
@Entity
@NamedQuery(name="Cell.findAll", query="SELECT c FROM Cell c")
public class Cell implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private Taxon taxonBean;

	public Cell() {
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


	//uni-directional many-to-one association to Taxon
	@ManyToOne
	@JoinColumn(name="taxon")
	public Taxon getTaxonBean() {
		return this.taxonBean;
	}

	public void setTaxonBean(Taxon taxonBean) {
		this.taxonBean = taxonBean;
	}

}