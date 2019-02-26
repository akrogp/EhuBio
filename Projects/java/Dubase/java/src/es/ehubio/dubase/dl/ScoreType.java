package es.ehubio.dubase.dl;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the ScoreType database table.
 * 
 */
@Entity
@NamedQuery(name="ScoreType.findAll", query="SELECT s FROM ScoreType s")
public class ScoreType implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String description;
	private String name;

	public ScoreType() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}