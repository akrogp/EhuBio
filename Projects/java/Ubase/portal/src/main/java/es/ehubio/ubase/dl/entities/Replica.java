package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Replica database table.
 * 
 */
@Entity
@NamedQuery(name="Replica.findAll", query="SELECT r FROM Replica r")
public class Replica implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private Condition conditionBean;

	public Replica() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	//uni-directional many-to-one association to Condition
	@ManyToOne
	@JoinColumn(name="condition")
	public Condition getConditionBean() {
		return this.conditionBean;
	}

	public void setConditionBean(Condition conditionBean) {
		this.conditionBean = conditionBean;
	}

}