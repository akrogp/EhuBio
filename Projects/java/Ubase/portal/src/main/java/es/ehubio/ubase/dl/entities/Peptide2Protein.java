package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Peptide2Protein database table.
 * 
 */
@Entity
@NamedQuery(name="Peptide2Protein.findAll", query="SELECT p FROM Peptide2Protein p")
public class Peptide2Protein implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private int position;
	private PeptideEvidence peptideEvidence;
	private Protein proteinBean;

	public Peptide2Protein() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public int getPosition() {
		return this.position;
	}

	public void setPosition(int position) {
		this.position = position;
	}


	//uni-directional many-to-one association to PeptideEvidence
	@ManyToOne
	@JoinColumn(name="peptide")
	public PeptideEvidence getPeptideEvidence() {
		return this.peptideEvidence;
	}

	public void setPeptideEvidence(PeptideEvidence peptideEvidence) {
		this.peptideEvidence = peptideEvidence;
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