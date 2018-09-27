package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the ProteinEvidence database table.
 * 
 */
@Entity
@NamedQuery(name="ProteinEvidence.findAll", query="SELECT p FROM ProteinEvidence p")
public class ProteinEvidence implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private int end;
	private int start;
	private PeptideEvidence peptideEvidenceBean;
	private Protein proteinBean;

	public ProteinEvidence() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public int getEnd() {
		return this.end;
	}

	public void setEnd(int end) {
		this.end = end;
	}


	public int getStart() {
		return this.start;
	}

	public void setStart(int start) {
		this.start = start;
	}


	//uni-directional many-to-one association to PeptideEvidence
	@ManyToOne
	@JoinColumn(name="peptideEvidence")
	public PeptideEvidence getPeptideEvidenceBean() {
		return this.peptideEvidenceBean;
	}

	public void setPeptideEvidenceBean(PeptideEvidence peptideEvidenceBean) {
		this.peptideEvidenceBean = peptideEvidenceBean;
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