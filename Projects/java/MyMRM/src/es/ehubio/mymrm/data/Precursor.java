package es.ehubio.mymrm.data;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Precursor database table.
 * 
 */
@Entity
@NamedQuery(name="Precursor.findAll", query="SELECT p FROM Precursor p")
public class Precursor implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private int charge;

	private Double intensity;

	private double mz;

	private Double rt;

	//bi-directional many-to-one association to PeptideEvidence
	@OneToMany(mappedBy="precursorBean")
	private List<PeptideEvidence> peptideEvidences;

	//bi-directional many-to-one association to Transition
	@OneToMany(mappedBy="precursorBean")
	private List<Transition> transitions;

	public Precursor() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCharge() {
		return this.charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public Double getIntensity() {
		return this.intensity;
	}

	public void setIntensity(Double intensity) {
		this.intensity = intensity;
	}

	public double getMz() {
		return this.mz;
	}

	public void setMz(double mz) {
		this.mz = mz;
	}

	public Double getRt() {
		return this.rt;
	}

	public void setRt(Double rt) {
		this.rt = rt;
	}

	public List<PeptideEvidence> getPeptideEvidences() {
		return this.peptideEvidences;
	}

	public void setPeptideEvidences(List<PeptideEvidence> peptideEvidences) {
		this.peptideEvidences = peptideEvidences;
	}

	public PeptideEvidence addPeptideEvidence(PeptideEvidence peptideEvidence) {
		getPeptideEvidences().add(peptideEvidence);
		peptideEvidence.setPrecursorBean(this);

		return peptideEvidence;
	}

	public PeptideEvidence removePeptideEvidence(PeptideEvidence peptideEvidence) {
		getPeptideEvidences().remove(peptideEvidence);
		peptideEvidence.setPrecursorBean(null);

		return peptideEvidence;
	}

	public List<Transition> getTransitions() {
		return this.transitions;
	}

	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}

	public Transition addTransition(Transition transition) {
		getTransitions().add(transition);
		transition.setPrecursorBean(this);

		return transition;
	}

	public Transition removeTransition(Transition transition) {
		getTransitions().remove(transition);
		transition.setPrecursorBean(null);

		return transition;
	}

}