package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the SupportingFile database table.
 * 
 */
@Entity
@NamedQuery(name="SupportingFile.findAll", query="SELECT s FROM SupportingFile s")
public class SupportingFile implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private Experiment experimentBean;
	private FileType fileType;

	public SupportingFile() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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


	//uni-directional many-to-one association to FileType
	@ManyToOne
	@JoinColumn(name="type")
	public FileType getFileType() {
		return this.fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

}