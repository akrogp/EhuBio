package es.ehubio.ubase.dl.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;


/**
 * The persistent class for the Protein2Group database table.
 * 
 */
@Entity
@NamedQuery(name="Protein2Group.findAll", query="SELECT p FROM Protein2Group p")
public class Protein2Group implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private Boolean leading;
	private Protein proteinBean;
	private ProteinGroup proteinGroup;

	public Protein2Group() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	@Column(name="leadingProt")
	public Boolean getLeading() {
		return this.leading;
	}

	public void setLeading(Boolean leading) {
		this.leading = leading;
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


	//uni-directional many-to-one association to ProteinGroup
	@ManyToOne
	@JoinColumn(name="proteinGroup")
	public ProteinGroup getProteinGroup() {
		return this.proteinGroup;
	}

	public void setProteinGroup(ProteinGroup proteinGroup) {
		this.proteinGroup = proteinGroup;
	}

}