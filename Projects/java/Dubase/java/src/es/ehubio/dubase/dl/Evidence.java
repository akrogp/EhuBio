package es.ehubio.dubase.dl;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Evidence database table.
 * 
 */
@Entity
@NamedQuery(name="Evidence.findAll", query="SELECT e FROM Evidence e")
public class Evidence implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private Experiment experimentBean;

	public Evidence() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
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