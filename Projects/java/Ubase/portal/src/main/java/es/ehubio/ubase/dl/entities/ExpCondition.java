package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the ExpCondition database table.
 * 
 */
@Entity
@NamedQuery(name="ExpCondition.findAll", query="SELECT e FROM ExpCondition e")
public class ExpCondition implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String description;
	private String name;
	private Experiment experimentBean;

	public ExpCondition() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


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


	//uni-directional many-to-one association to Experiment
	@ManyToOne
	@JoinColumn(name="experiment")
	public Experiment getExperimentBean() {
		return this.experimentBean;
	}

	public void setExperimentBean(Experiment experimentBean) {
		this.experimentBean = experimentBean;
	}

}