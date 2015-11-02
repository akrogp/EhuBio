package es.ehubio.mymrm.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import es.ehubio.mymrm.data.LatestNew;
import es.ehubio.mymrm.data.PageSummary;

@ManagedBean
@RequestScoped
public class HomeBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private final List<PageSummary> pages;
	private List<PageSummary> firstPages = new ArrayList<>();
	private PageSummary lastPage = null;
	private final List<LatestNew> news;	

	public HomeBean() {
		pages = new ArrayList<>();
		
		PageSummary page = new PageSummary();
		page.setName("Home");
		page.setDescription("The MyMRM home page.");
		page.setAction("home");
		addPage(page);
		
		page = new PageSummary();
		page.setName("Experiments");
		page.setDescription("Feed database with new experiments.");
		page.setAction("feed");
		addPage(page);
		
		page = new PageSummary();
		page.setName("Materials & Methods");
		page.setDescription("Materials and methods.");
		page.setAction("methods");
		addPage(page);
		
		page = new PageSummary();
		page.setName("Databases");
		page.setDescription("Manage fasta databases for protein search.");
		page.setAction("databases");
		addPage(page);
		
		page = new PageSummary();
		page.setName("Search Peptide");
		page.setDescription("Search database for evidences and transactions of a given peptide secuence.");
		page.setAction("peptide");
		addPage(page);
		
		page = new PageSummary();
		page.setName("Search Protein");
		page.setDescription("Search database for evidences and transactions of unique peptides for a given protein.");
		page.setAction("protein");
		addPage(page);
		
		news = new ArrayList<>();
		news.add(new LatestNew("Dec 15, 2014", "MyMRM v1.2 with MSF support"));
		news.add(new LatestNew("Oct 10, 2014", "MyMRM v1.1 with extended options in experiment feed page"));
		news.add(new LatestNew("Oct 02, 2014", "MyMRM v1.0 ready for HUPO 2014"));				
	}
	
	public List<PageSummary> getPages() {
		return pages;
	}
	
	public List<PageSummary> getFirstPages() {
		return firstPages;
	}
	
	public PageSummary getLastPage() {
		return lastPage;
	}
	
	public void addPage( PageSummary page ) {
		if( lastPage != null )
			firstPages.add(lastPage);
		lastPage = page;
		pages.add(page);
	}
	
	public List<LatestNew> getNews() {
		return news;
	}
	
	public String getSignature() {
		return "MyMRM (v1.2)";
	}
	
	public String getLastUpdated() {
		return news.get(0).getDate();
	}
}