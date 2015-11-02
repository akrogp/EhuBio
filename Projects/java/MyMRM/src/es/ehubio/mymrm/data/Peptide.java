package es.ehubio.mymrm.data;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Peptide database table.
 * 
 */
@Entity
@NamedQuery(name="Peptide.findAll", query="SELECT p FROM Peptide p")
public class Peptide implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String massSequence;

	private String sequence;

	//bi-directional many-to-one association to PeptideEvidence
	@OneToMany(mappedBy="peptideBean")
	private List<PeptideEvidence> peptideEvidences;

	public Peptide() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMassSequence() {
		return this.massSequence;
	}

	public void setMassSequence(String massSequence) {
		this.massSequence = massSequence;
	}

	public String getSequence() {
		return this.sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public List<PeptideEvidence> getPeptideEvidences() {
		return this.peptideEvidences;
	}

	public void setPeptideEvidences(List<PeptideEvidence> peptideEvidences) {
		this.peptideEvidences = peptideEvidences;
	}

	public PeptideEvidence addPeptideEvidence(PeptideEvidence peptideEvidence) {
		getPeptideEvidences().add(peptideEvidence);
		peptideEvidence.setPeptideBean(this);

		return peptideEvidence;
	}

	public PeptideEvidence removePeptideEvidence(PeptideEvidence peptideEvidence) {
		getPeptideEvidences().remove(peptideEvidence);
		peptideEvidence.setPeptideBean(null);

		return peptideEvidence;
	}

}