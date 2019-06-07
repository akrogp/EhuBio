package es.ehubio.dubase.dl.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * The persistent class for the Author database table.
 * 
 */
@XmlRootElement
@Entity
@NamedQueries({
@NamedQuery(name="Author.findAll", query="SELECT a FROM Author a"),
@NamedQuery(name="Author.findByMail", query="SELECT a FROM Author a WHERE a.mail = :mail")
})
public class Author implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String affiliation;
	private String mail;
	private String name;

	public Author() {
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


	public String getAffiliation() {
		return this.affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}


	public String getMail() {
		return this.mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}