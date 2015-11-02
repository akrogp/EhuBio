package es.ehubio.mymrm.data;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;


/**
 * The persistent class for the IonType database table.
 * 
 */
@Entity
@NamedQuery(name="IonType.findAll", query="SELECT i FROM IonType i")
public class IonType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String name;

	//bi-directional many-to-one association to Fragment
	@OneToMany(mappedBy="ionType")
	private List<Fragment> fragments;

	public IonType() {
	}

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

	public List<Fragment> getFragments() {
		return this.fragments;
	}

	public void setFragments(List<Fragment> fragments) {
		this.fragments = fragments;
	}

	public Fragment addFragment(Fragment fragment) {
		getFragments().add(fragment);
		fragment.setIonType(this);

		return fragment;
	}

	public Fragment removeFragment(Fragment fragment) {
		getFragments().remove(fragment);
		fragment.setIonType(null);

		return fragment;
	}

}