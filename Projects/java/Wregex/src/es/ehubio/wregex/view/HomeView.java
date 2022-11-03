package es.ehubio.wregex.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import es.ehubio.wregex.data.LatestNew;
import es.ehubio.wregex.data.PageSummary;
import es.ehubio.wregex.data.Versions;

@Named
@ApplicationScoped
public class HomeView implements Serializable {
	private static final long serialVersionUID = 1L;
	private final List<PageSummary> pages = new ArrayList<>();
	private final List<PageSummary> firstPages = new ArrayList<>();
	private final List<CompatibilityBean> compat = new ArrayList<>();
	private PageSummary lastPage = null;
	private final List<LatestNew> news = new ArrayList<>();
	@Inject
	private SearchView searchView;
	
	public HomeView() {
		populatePages();
		populateLog();
		populateCompat();
	}
	
	private void populateCompat() {
		CompatibilityBean test = new CompatibilityBean();
		test.setOs("Linux");
		test.setOsVersion("Ubuntu 20.04 LTS");
		test.getBrowser().put("Firefox", "105.0");
		test.getBrowser().put("Google Chrome", "106.0.5249.103");
		test.getBrowser().put("Microsoft Edge", "n/a");
		test.getBrowser().put("Safari", "n/a");
		compat.add(test);
		
		test = new CompatibilityBean();
		test.setOs("macOS");
		test.setOsVersion("Monterey 12.6");
		test.getBrowser().put("Firefox", "105.0.3");
		test.getBrowser().put("Google Chrome", "106.0.5249.119");
		test.getBrowser().put("Microsoft Edge", "n/a");
		test.getBrowser().put("Safari", "16.0");
		compat.add(test);	
		
		test = new CompatibilityBean();
		test.setOs("Windows");
		test.setOsVersion("Server 2016");
		test.getBrowser().put("Firefox", "104.0.1");
		test.getBrowser().put("Google Chrome", "107.0.5304.88");
		test.getBrowser().put("Microsoft Edge", "107.0.1418.26");
		test.getBrowser().put("Safari", "n/a");
		compat.add(test);
	}

	@SuppressWarnings("unused")
	private void populateLog() {
		if( Versions.DEV )
			setWregexLogDev(news);
		else if( Versions.MAJOR == 1 )
			setWregexLog1(news);
		else if( Versions.MAJOR == 2 )
			setWregexLog2(news);		
		else if( Versions.MAJOR == 3 )
			setWregexLog3(news);
	}

	private void populatePages() {
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
		
		page = new PageSummary();
		page.setName("About");
		page.setDescription( "Publications and authors." );
		page.setAction("about");
		addPage(page);		
	}

	private void setWregexLogDev(List<LatestNew> news) {
		news.add(new LatestNew("Jun 30, 2022", "Databases updated"));
		news.add(new LatestNew("Sep 10, 2020", "Databases updated"));
		news.add(new LatestNew("Sep 28, 2017", "Included support for coarse/fine PSSM"));
		news.add(new LatestNew("Sep 26, 2017", "Filtered SNPs from COSMIC"));
		news.add(new LatestNew("Sep 14, 2017", "Updated to latest versions of COSMIC and UniProt"));
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
	
	private void setWregexLog3(List<LatestNew> news) {
		news.add(new LatestNew("Nov 03, 2022", "Wregex v3.0 preview published"));
		news.add(new LatestNew("Sep 26, 2017", "Wregex v2.1 published"));
		news.add(new LatestNew("May 12, 2016", "Wregex v2.0 published in Scientific Reports"));
		news.add(new LatestNew("Jan 06, 2014", "Wregex v1.0 published in Bioinformatics"));		
	}

	private void setWregexLog2(List<LatestNew> news) {
		news.add(new LatestNew("Jun 30, 2022", "Databases updated"));
		news.add(new LatestNew("Sep 10, 2020", "Databases updated"));
		news.add(new LatestNew("Sep 26, 2017", "Wregex v2.1 published"));
		news.add(new LatestNew("Sep 14, 2017", "Updated to latest versions of COSMIC and UniProt"));
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
		return "https://ehubio.ehu.eus/wregex/";
	}
	
	public String getWikiUrl() {
		return "https://github.com/akrogp/EhuBio/wiki/Wregex";
	}
	
	public String getCodeUrl() {
		return "https://github.com/akrogp/EhuBio/tree/master/Projects/java/Wregex";
	}
	
	public String getBinaryUrl() {
		return "https://ehubio.ehu.eus/static/wregex.war";
	}
	
	public List<CompatibilityBean> getCompat() {
		return compat;
	}
	
	public List<String> getBrowsers() {
		return new ArrayList<>(compat.get(0).getBrowser().keySet());
	}
	
	public String navigateCase(String preset) {
		UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
		ValueChangeEvent event = new ValueChangeEvent(component, null, preset);
		searchView.onSelectPreset(event);
		return "search";
	}
}