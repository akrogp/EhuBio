package es.ehubio.mymrm.data;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;


/**
 * The persistent class for the Fragment database table.
 * 
 */
@Entity
@NamedQuery(name="Fragment.findAll", query="SELECT f FROM Fragment f")
public class Fragment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private int charge;

	private double error;

	private int position;

	private double intensity;

	private double mz;

	//bi-directional many-to-one association to Transition
	@OneToMany(mappedBy="fragmentBean")
	private List<Transition> transitions;

	//bi-directional many-to-one association to IonType
	@ManyToOne
	@JoinColumn(name="type")
	private IonType ionType;

	public Fragment() {
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

	public double getError() {
		return this.error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public int getPosition() {
		return this.position;
	}

	public void setPosition(int index) {
		this.position = index;
	}

	public double getIntensity() {
		return this.intensity;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

	public double getMz() {
		return this.mz;
	}

	public void setMz(double mz) {
		this.mz = mz;
	}

	public List<Transition> getTransitions() {
		return this.transitions;
	}

	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}

	public Transition addTransition(Transition transition) {
		getTransitions().add(transition);
		transition.setFragmentBean(this);

		return transition;
	}

	public Transition removeTransition(Transition transition) {
		getTransitions().remove(transition);
		transition.setFragmentBean(null);

		return transition;
	}

	public IonType getIonType() {
		return this.ionType;
	}

	public void setIonType(IonType ionType) {
		this.ionType = ionType;
	}

}