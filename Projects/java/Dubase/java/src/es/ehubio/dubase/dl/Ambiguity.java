package es.ehubio.dubase.dl;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Ambiguity database table.
 * 
 */
@Entity
@NamedQuery(name="Ambiguity.findAll", query="SELECT a FROM Ambiguity a")
public class Ambiguity implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private Evidence evidenceBean;
	private Substrate substrateBean;

	public Ambiguity() {
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


	//uni-directional many-to-one association to Substrate
	@ManyToOne
	@JoinColumn(name="substrate")
	public Substrate getSubstrateBean() {
		return this.substrateBean;
	}

	public void setSubstrateBean(Substrate substrateBean) {
		this.substrateBean = substrateBean;
	}

}