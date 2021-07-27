package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Publication database table.
 * 
 */
@Entity
@NamedQuery(name="Publication.findAll", query="SELECT p FROM Publication p")
public class Publication implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String doi;
	private String journal;
	private String pmid;
	private String title;
	private int year;
	private Experiment experiment;

	public Publication() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getDoi() {
		return this.doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}


	public String getJournal() {
		return this.journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}


	public String getPmid() {
		return this.pmid;
	}

	public void setPmid(String pmid) {
		this.pmid = pmid;
	}


	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public int getYear() {
		return this.year;
	}

	public void setYear(int year) {
		this.year = year;
	}


	//bi-directional many-to-one association to Experiment
	@ManyToOne
	@JoinColumn(name="Experiment_id")
	public Experiment getExperiment() {
		return this.experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

}