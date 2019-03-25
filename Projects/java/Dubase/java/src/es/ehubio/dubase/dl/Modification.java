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
 * The persistent class for the Modification database table.
 * 
 */
@Entity
@NamedQuery(name="Modification.findAll", query="SELECT m FROM Modification m")
public class Modification implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private Evidence evidenceBean;
	private ModType modType;
	private int position;

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


	//uni-directional many-to-one association to Evidence
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
	
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}

}