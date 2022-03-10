package es.ehubio.portal.ui;

import java.io.IOException;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.json.JSONException;

import es.ehubio.portal.bl.EhuBio;
import es.ehubio.portal.bl.Project;

@Named
@RequestScoped
public class ProjectsView {
	private List<Project> projects;
	
	public List<Project> getProjects() throws JSONException, IOException {
		if( projects == null )
			projects = EhuBio.getProjects();
		return projects;
	}
}
