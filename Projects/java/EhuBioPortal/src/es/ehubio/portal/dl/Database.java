package es.ehubio.portal.dl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Database {
	private final String dbPath;
	private final static String PROJECTS = "projects.json";
	private final static String PAPERS = "papers.json";
	
	public Database(String dbPath) {
		this.dbPath = dbPath;
	}

	public List<Project> loadProjects() throws JSONException, IOException {	
		JSONArray array = new JSONArray(readDatabase(PROJECTS));
		List<Project> projects = new ArrayList<>(array.length());
		for( int i = 0; i < array.length(); i++ ) {
			JSONObject json = array.getJSONObject(i);
			Project project = new Project();
			project.setTitle(json.optString("title"));
			project.setSubtile(json.optString("subtitle"));
			project.setUrl(json.optString("url"));
			project.setPreview(json.optString("preview"));
			project.setPaper(json.optString("paper"));
			project.setDesc(json.optString("desc"));
			projects.add(project);
		}
		return projects;
	}
	
	public List<Work> loadPapers() throws JSONException, IOException {
		JSONObject json = new JSONObject(readDatabase(PAPERS));
		return Orcid.parseWorks(json);
	}
	
	private String readDatabase(String path) throws IOException {
		return Files.readString(Paths.get(dbPath, path), StandardCharsets.UTF_8);
	}
}