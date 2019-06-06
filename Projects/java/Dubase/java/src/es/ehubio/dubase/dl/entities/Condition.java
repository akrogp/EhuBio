package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Condition database table.
 * 
 */
@Entity
@NamedQuery(name="Condition.findAll", query="SELECT c FROM Condition c")
public class Condition implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private boolean control;
	private String description;
	private String name;
	private String replicateType;
	private Experiment experimentBean;
	private List<Replicate> replicates;

	public Condition() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public boolean getControl() {
		return this.control;
	}

	public void setControl(boolean control) {
		this.control = control;
	}


	@Lob
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getReplicateType() {
		return this.replicateType;
	}

	public void setReplicateType(String replicateType) {
		this.replicateType = replicateType;
	}


	//bi-directional many-to-one association to Experiment
	@ManyToOne
	@JoinColumn(name="experiment")
	public Experiment getExperimentBean() {
		return this.experimentBean;
	}

	public void setExperimentBean(Experiment experimentBean) {
		this.experimentBean = experimentBean;
	}


	//bi-directional many-to-one association to Replicate
	@OneToMany(mappedBy="conditionBean", fetch=FetchType.EAGER)
	public List<Replicate> getReplicates() {
		return this.replicates;
	}

	public void setReplicates(List<Replicate> replicates) {
		this.replicates = replicates;
	}

	public Replicate addReplicate(Replicate replicate) {
		getReplicates().add(replicate);
		replicate.setConditionBean(this);

		return replicate;
	}

	public Replicate removeReplicate(Replicate replicate) {
		getReplicates().remove(replicate);
		replicate.setConditionBean(null);

		return replicate;
	}

}