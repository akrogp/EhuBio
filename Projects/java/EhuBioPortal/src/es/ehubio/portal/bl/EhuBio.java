package es.ehubio.portal.bl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EhuBio {
	private final static String PROJECTS_PATH ="/media/data/EhuBio/projects.json";

	public static List<Project> getProjects() throws JSONException, IOException {	
		JSONArray array = new JSONArray(
			Files.readString(Paths.get(PROJECTS_PATH), StandardCharsets.UTF_8));
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
}
