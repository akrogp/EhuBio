package es.ehubio.dubase.dl.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;


/**
 * The persistent class for the MethodType database table.
 * 
 */
@Entity
@NamedQuery(name="MethodType.findAll", query="SELECT m FROM MethodType m")
@XmlRootElement
public class MethodType implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;

	public MethodType() {
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


	@XmlTransient
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}