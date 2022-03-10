package es.ehubio.portal.bl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import es.ehubio.portal.dl.Database;
import es.ehubio.portal.dl.Orcid;
import es.ehubio.portal.dl.Project;
import es.ehubio.portal.dl.Work;

@LocalBean
@Singleton
public class EhuBio {
	private final static String EHUBIO_PATH ="/media/data/EhuBio/";	
	private static final String ORCID_URL = "https://pub.orcid.org/v3.0/0000-0002-6433-8452/works";
	
	private final Database db = new Database(EHUBIO_PATH);
	private List<Work> works;
	private Thread thread;
	
	public List<Work> getWorks() throws JSONException, IOException {
		if( works == null )
			works = filterWorks(db.loadPapers());
		if( thread == null || !thread.isAlive() ) {
			thread = new Thread(() -> {
				JSONObject json = requestWorks();
				works = filterWorks(Orcid.parseWorks(json));
				thread = null;
			});
			thread.start();
		}
		return works;
	}
	
	private List<Work> filterWorks(List<Work> works) {
		String[] bannedJournals = {
			"IEEE", "URSI", "Image Communication", "EuCAP"
		};
		List<Work> results = works.stream()
			.filter(work -> work.getYear() >= 2012)
			.filter(
				work -> work.getJournal() == null ||
				Stream.of(bannedJournals).noneMatch(banned -> work.getJournal().contains(banned))
			)
			.collect(Collectors.toList());
		Integer year = null;
		for( Work work : results )
			if( year == null || !year.equals(work.getYear()) ) {
				year = work.getYear();
				work.setFirst(true);
			}
		return results;
	}

	public List<Project> getProjects() throws JSONException, IOException {
		return db.loadProjects();
	}
	
	private JSONObject requestWorks() {
		String json = ClientBuilder.newClient()
			.target(ORCID_URL)
			.request(MediaType.APPLICATION_JSON)
			.get(String.class);
		return new JSONObject(json);
	}
}
