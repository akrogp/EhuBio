package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Modification database table.
 * 
 */
@Entity
@NamedQuery(name="Modification.findAll", query="SELECT m FROM Modification m")
public class Modification implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private int position;
	private Evidence evidenceBean;
	private ModType modType;

	public Modification() {
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


	//bi-directional many-to-one association to Evidence
	@ManyToOne
	@JoinColumn(name="evidence")
	public Evidence getEvidenceBean() {
		return this.evidenceBean;
	}

	public void setEvidenceBean(Evidence evidenceBean) {
		this.evidenceBean = evidenceBean;
	}


	//uni-directional many-to-one association to ModType
	@ManyToOne
	@JoinColumn(name="type")
	public ModType getModType() {
		return this.modType;
	}

	public void setModType(ModType modType) {
		this.modType = modType;
	}

}