package es.ehubio.dubase.dl.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;


/**
 * The persistent class for the FileType database table.
 * 
 */
@XmlRootElement
@Entity
@NamedQuery(name="FileType.findAll", query="SELECT f FROM FileType f")
public class FileType implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String description;
	private String name;

	public FileType() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlValue
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Column(length = 65535, columnDefinition="TEXT")
	@XmlTransient
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@XmlTransient
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}