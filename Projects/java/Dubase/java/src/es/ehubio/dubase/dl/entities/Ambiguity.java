package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;


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
	private Protein proteinBean;
	private List<Modification> modifications;

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


	//bi-directional many-to-one association to Evidence
	@ManyToOne
	@JoinColumn(name="evidence")
	public Evidence getEvidenceBean() {
		return this.evidenceBean;
	}

	public void setEvidenceBean(Evidence evidenceBean) {
		this.evidenceBean = evidenceBean;
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


	//bi-directional many-to-one association to Modification
	@OneToMany(mappedBy="ambiguityBean", fetch=FetchType.EAGER)
	public List<Modification> getModifications() {
		return this.modifications;
	}

	public void setModifications(List<Modification> modifications) {
		this.modifications = modifications;
	}

	public Modification addModification(Modification modification) {
		getModifications().add(modification);
		modification.setAmbiguityBean(this);

		return modification;
	}

	public Modification removeModification(Modification modification) {
		getModifications().remove(modification);
		modification.setAmbiguityBean(null);

		return modification;
	}

}