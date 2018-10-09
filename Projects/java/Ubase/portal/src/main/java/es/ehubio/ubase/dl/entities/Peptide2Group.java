package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Peptide2Group database table.
 * 
 */
@Entity
@NamedQuery(name="Peptide2Group.findAll", query="SELECT p FROM Peptide2Group p")
public class Peptide2Group implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private ProteinGroup proteinGroupBean;
	private PeptideEvidence peptideEvidence;

	public Peptide2Group() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	//uni-directional many-to-one association to ProteinGroup
	@ManyToOne
	@JoinColumn(name="proteinGroup")
	public ProteinGroup getProteinGroupBean() {
		return this.proteinGroupBean;
	}

	public void setProteinGroupBean(ProteinGroup proteinGroupBean) {
		this.proteinGroupBean = proteinGroupBean;
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

}