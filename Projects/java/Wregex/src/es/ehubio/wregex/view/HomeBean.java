package es.ehubio.wregex.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import es.ehubio.wregex.data.LatestNew;
import es.ehubio.wregex.data.PageSummary;
import es.ehubio.wregex.data.Versions;

@ManagedBean
@ApplicationScoped
public class HomeBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private final List<PageSummary> pages;
	private List<PageSummary> firstPages = new ArrayList<>();
	private PageSummary lastPage = null;
	private final List<LatestNew> news;	

	@SuppressWarnings("unused")
	public HomeBean() {
		pages = new ArrayList<>();
		
		PageSummary page = new PageSummary();
		page.setName("Home");
		page.setDescription( "The Wregex home page." );
		page.setAction("home");
		addPage(page);
		
		page = new PageSummary();
		page.setName("Search");
		page.setDescription(
			"Search motifs in protein sequences provided as an input fasta file. " +
			"The motif can be selected from a dropdown list or a custom motif can be provided by the user by " +
			"entering a regular expression and an optional PSSM. This PSSM can be built using the Training page." );
		page.setAction("search");
		addPage(page);
		
		if( Versions.MAJOR >= 2 ) {
			page = new PageSummary();
			page.setName("Charts");
			page.setDescription( "Browse charts with most interesting Wregex motif candidates." );
			page.setAction("charts");
			addPage(page);
		}
		
		page = new PageSummary();
		page.setName("Training");
		page.setDescription(
			"Build a custom PSSM by providing a regular expression and a set of training motifs. " +
			"Please read first the user manual to get familiar with matching groups in Wregex regular expressions.");
		page.setAction("training");
		addPage(page);
		
		page = new PageSummary();
		page.setName("Documentation");
		page.setDescription(
			"Here there is the user manual and a paper explaining the details of the Wregex algorithm." );
		page.setAction("documentation");
		addPage(page);
		
		page = new PageSummary();
		page.setName("Downloads");
		page.setDescription(
			"Wregex is free software and licensed under the GPL. In this page you can find a link to the source code " +
			"and the binary redistributable." );
		page.setAction("downloads");
		addPage(page);
		
		news = new ArrayList<>();
		if( Versions.DEV )
			setWregexLogDev(news);
		else if( Versions.MAJOR == 1 )
			setWregexLog1(news);
		else if( Versions.MAJOR == 2 )
			setWregexLog2(news);
			
	}
	
	private void setWregexLogDev(List<LatestNew> news2) {
		news.add(new LatestNew("May 12, 2016", "Wregex v2.0 published in Scientific Reports"));
		news.add(new LatestNew("Nov 04, 2015", "Search results are now cached"));
		news.add(new LatestNew("Feb 08, 2015", "Updated to COSMIC v71"));
		news.add(new LatestNew("Jan 28, 2015", "Included NESdb in target database list"));
		news.add(new LatestNew("Jan 26, 2015", "Included support for a second auxiliary motif"));
		news.add(new LatestNew("Jan 20, 2015", "Mutation is displayed in red and mutation effect is also saved in the CSV"));
		news.add(new LatestNew("Dec 29, 2014", "Included mutation effect score"));
		news.add(new LatestNew("Apr 16, 2014", "Made more robust the training page to avoid denial of service to another users"));
		news.add(new LatestNew("Apr 08, 2014", "Updated site template"));
		news.add(new LatestNew("Apr 03, 2014", "Included PTM chart"));
		news.add(new LatestNew("Apr 01, 2014", "Database and statistics initialization are moved to separate threads"));
		news.add(new LatestNew("Mar 31, 2014", "Improved tooltips in bubble chart"));
		news.add(new LatestNew("Mar 31, 2014", "Filtered predictions and duplicates in dbPTM"));
		news.add(new LatestNew("Mar 26, 2014", "Included tooltips in bubble chart"));
		news.add(new LatestNew("Mar 04, 2014", "First bubble chart support!"));
		news.add(new LatestNew("Mar 03, 2014", "Included Wregex NLS motifs"));
		news.add(new LatestNew("Mar 03, 2014", "Included support for optional capturing groups"));
		news.add(new LatestNew("Feb 17, 2014", "Included support for dbPTM!"));
		news.add(new LatestNew("Feb 13, 2014", "Now it is possible to search for all Wregex and ELM motifs at the same time!"));
		news.add(new LatestNew("Feb 11, 2014", "Included proteins of cancer genes from nature12912 paper as a predefined target"));
		news.add(new LatestNew("Feb 11, 2014", "Wregex-v1.1 beta features moved to a test server"));
		news.add(new LatestNew("Feb 11, 2014", "Included beta support for COSMIC database! Results are sorted by mutations when COSMIC is enabled"));
		news.add(new LatestNew("Feb 10, 2014", "Included human proteome as a predefined target"));
		news.add(new LatestNew("Jan 06, 2014", "Wregex v1.0 published in Bioinformatics"));
	}

	private void setWregexLog2(List<LatestNew> news) {
		news.add(new LatestNew("May 12, 2016", "Wregex v2.0 published in Scientific Reports"));
		news.add(new LatestNew("Dec 29, 2014", "Finished COSMIC support"));
		news.add(new LatestNew("Apr 16, 2014", "Included time constraints to avoid denial of service to other users"));
		news.add(new LatestNew("Mar 26, 2014", "Included bubble charts"));
		news.add(new LatestNew("Feb 10, 2014", "Included human proteome as a predefined target"));
		news.add(new LatestNew("Jan 06, 2014", "Wregex v1.0 published in Bioinformatics"));		
	}

	private void setWregexLog1(List<LatestNew> news) {
		news.add(new LatestNew("Apr 16, 2014", "Included time constraints to avoid denial of service to other users"));
		news.add(new LatestNew("Feb 11, 2014", "Website documentation completed"));
		news.add(new LatestNew("Jan 06, 2014", "Wregex v1.0 published in Bioinformatics"));		
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
		return Versions.SIGN;
	}
	
	public int getMajor() {
		return Versions.MAJOR; 
	}
	
	public boolean isDevelopment() {
		return Versions.DEV;
	}
	
	public boolean isProduction() {
		return Versions.PROD;
	}
	
	public String getLastUpdated() {
		return news.get(0).getDate();
	}
	
	public String getWebUrl() {
		return "http://ehubio.ehu.eus/wregex/";
	}
	
	public String getWikiUrl() {
		return "https://github.com/akrogp/EhuBio/wiki/Wregex";
	}
	
	public String getCodeUrl() {
		return "https://github.com/akrogp/EhuBio/tree/master/Projects/java/Wregex";
	}
	
	public String getBinaryUrl() {
		return "http://ehubio.ehu.eus/static/wregex.war";
	}
}