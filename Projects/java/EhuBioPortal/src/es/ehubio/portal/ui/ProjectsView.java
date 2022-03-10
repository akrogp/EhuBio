package es.ehubio.portal.ui;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.json.JSONException;

import es.ehubio.portal.bl.EhuBio;
import es.ehubio.portal.dl.Project;

@Named
@RequestScoped
public class ProjectsView {
	private List<Project> projects;
	@EJB
	private EhuBio model;
	
	public List<Project> getProjects() throws JSONException, IOException {
		if( projects == null )
			projects = model.getProjects();
		return projects;
	}
}
