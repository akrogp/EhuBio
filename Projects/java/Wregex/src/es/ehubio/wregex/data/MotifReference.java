package es.ehubio.wregex.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="reference")
public final class MotifReference implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String authors;
	private String title;
	private String journal;
	private String year;
	private String volume;
	private String number;
	private String pages;
	private String doi;
	private String link;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlTransient
	public String getHtml() {
		if( name != null )
			return name;
		StringBuilder builder = new StringBuilder();
		if( authors != null )
			builder.append(authors);
		if( year != null )
			builder.append(" ("+year+")");
		if( title != null )
			builder.append(". " + title);
		if( journal != null )
			builder.append(". <i>"+journal+"</i>");
		if( volume != null )
			builder.append(", <b>"+volume+"</b>");
		if( number != null )
			builder.append("("+number+")");
		if( pages != null )
			builder.append(", "+pages);
		if( doi != null )
			builder.append(", doi:"+doi);
		builder.append('.');
		return builder.toString();
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getJournal() {
		return journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}
}
