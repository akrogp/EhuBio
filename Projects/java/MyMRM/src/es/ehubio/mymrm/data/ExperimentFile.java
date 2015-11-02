package es.ehubio.mymrm.data;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the ExperimentFile database table.
 * 
 */
@Entity
@NamedQuery(name="ExperimentFile.findAll", query="SELECT e FROM ExperimentFile e")
public class ExperimentFile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String fileName;

	//bi-directional many-to-one association to Experiment
	@ManyToOne
	@JoinColumn(name="experiment")
	private Experiment experimentBean;

	public ExperimentFile() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Experiment getExperimentBean() {
		return this.experimentBean;
	}

	public void setExperimentBean(Experiment experimentBean) {
		this.experimentBean = experimentBean;
	}

	@Override
	public String toString() {
		return getFileName();
	}
}