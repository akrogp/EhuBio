package es.ehubio.dubase.dl;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;


/**
 * The persistent class for the Replicate database table.
 * 
 */
@Entity
@NamedQuery(name="Replicate.findAll", query="SELECT r FROM Replicate r")
public class Replicate implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private boolean control;
	private Evidence evidenceBean;

	public Replicate() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	//uni-directional many-to-one association to Evidence
	@ManyToOne
	@JoinColumn(name="evidence")
	public Evidence getEvidenceBean() {
		return this.evidenceBean;
	}

	public void setEvidenceBean(Evidence evidenceBean) {
		this.evidenceBean = evidenceBean;
	}


	public boolean isControl() {
		return control;
	}


	public void setControl(boolean control) {
		this.control = control;
	}

}