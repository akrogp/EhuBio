package es.ehubio.dubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the Experiment database table.
 * 
 */
@Entity
@NamedQuery(name="Experiment.findAll", query="SELECT e FROM Experiment e")
public class Experiment implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private Date expDate;
	private Date pubDate;
	private Author authorBean;
	private Enzyme enzymeBean;
	private Method methodBean;

	public Experiment() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Temporal(TemporalType.DATE)
	public Date getExpDate() {
		return this.expDate;
	}

	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}


	@Temporal(TemporalType.DATE)
	public Date getPubDate() {
		return this.pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}


	//uni-directional many-to-one association to Author
	@ManyToOne
	@JoinColumn(name="author")
	public Author getAuthorBean() {
		return this.authorBean;
	}

	public void setAuthorBean(Author authorBean) {
		this.authorBean = authorBean;
	}


	//uni-directional many-to-one association to Enzyme
	@ManyToOne
	@JoinColumn(name="enzyme")
	public Enzyme getEnzymeBean() {
		return this.enzymeBean;
	}

	public void setEnzymeBean(Enzyme enzymeBean) {
		this.enzymeBean = enzymeBean;
	}


	//uni-directional many-to-one association to Method
	@ManyToOne
	@JoinColumn(name="method")
	public Method getMethodBean() {
		return this.methodBean;
	}

	public void setMethodBean(Method methodBean) {
		this.methodBean = methodBean;
	}

}