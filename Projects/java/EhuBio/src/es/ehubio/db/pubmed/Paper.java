package es.ehubio.db.pubmed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Paper {
	private String pmid;
	private Date date;
	private String title;
	private String abs;
	private final List<Author> authors = new ArrayList<>();
	private String journal;
	
	public String getPmid() {
		return pmid;
	}
	public void setPmid(String pmid) {
		this.pmid = pmid;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAbs() {
		return abs;
	}
	public void setAbs(String abs) {
		this.abs = abs;
	}
	public List<Author> getAuthors() {
		return authors;
	}
	public Author getLastAuthor() {
		return getAuthors().get(getAuthors().size()-1);
	}
	public Author getFirstAuthor() {
		return getAuthors().get(0);
	}
	public String getJournal() {
		return journal;
	}
	public void setJournal(String journal) {
		this.journal = journal;
	}
}
