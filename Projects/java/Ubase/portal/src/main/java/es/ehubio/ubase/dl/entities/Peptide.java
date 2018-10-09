package es.ehubio.ubase.dl.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


/**
 * The persistent class for the Peptide database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Peptide.findAll", query="SELECT p FROM Peptide p"),
	@NamedQuery(name="Peptide.findSeq", query="SELECT p FROM Peptide p WHERE p.sequence = :seq")
})
public class Peptide implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String sequence;

	public Peptide() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getSequence() {
		return this.sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

}