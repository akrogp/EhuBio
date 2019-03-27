package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Method database table.
 * 
 */
@Entity
@NamedQuery(name="Method.findAll", query="SELECT m FROM Method m")
public class Method implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String openDescription;

	public Method() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Lob
	public String getOpenDescription() {
		return this.openDescription;
	}

	public void setOpenDescription(String openDescription) {
		this.openDescription = openDescription;
	}

}