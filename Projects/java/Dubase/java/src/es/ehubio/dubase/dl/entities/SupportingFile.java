package es.ehubio.dubase.dl.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * The persistent class for the SupportingFile database table.
 * 
 */
@XmlRootElement
@Entity
@NamedQuery(name="SupportingFile.findAll", query="SELECT s FROM SupportingFile s")
public class SupportingFile implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String url;
	private Experiment experimentBean;
	private FileType fileType;

	public SupportingFile() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
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
	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	//bi-directional many-to-one association to Experiment
	@ManyToOne
	@JoinColumn(name="experiment")
	@XmlTransient
	public Experiment getExperimentBean() {
		return this.experimentBean;
	}

	public void setExperimentBean(Experiment experimentBean) {
		this.experimentBean = experimentBean;
	}


	//uni-directional many-to-one association to FileType
	@ManyToOne
	@JoinColumn(name="type")
	@XmlElement(name="type")
	public FileType getFileType() {
		return this.fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}	

}