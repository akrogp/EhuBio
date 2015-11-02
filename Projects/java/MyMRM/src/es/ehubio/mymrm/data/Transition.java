package es.ehubio.mymrm.data;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Transition database table.
 * 
 */
@Entity
@NamedQuery(name="Transition.findAll", query="SELECT t FROM Transition t")
public class Transition implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	//bi-directional many-to-one association to Fragment
	@ManyToOne
	@JoinColumn(name="fragment")
	private Fragment fragmentBean;

	//bi-directional many-to-one association to Precursor
	@ManyToOne
	@JoinColumn(name="precursor")
	private Precursor precursorBean;

	public Transition() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Fragment getFragmentBean() {
		return this.fragmentBean;
	}

	public void setFragmentBean(Fragment fragmentBean) {
		this.fragmentBean = fragmentBean;
	}

	public Precursor getPrecursorBean() {
		return this.precursorBean;
	}

	public void setPrecursorBean(Precursor precursorBean) {
		this.precursorBean = precursorBean;
	}

}