package es.ehubio.portal.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import es.ehubio.portal.model.Project;

@ManagedBean
@RequestScoped
public class HomeBean implements Serializable {
	private static final long serialVersionUID = 1L;
	final List<Project> projects;		

	public HomeBean() {
		projects = new ArrayList<>();
		
		Project project = new Project();
		project.setName("Wregex");
		project.setDescription(
			"A software tool for aminoacid motif searching termed Wregex (weighted regular expression). " +
			"Our novel approach combines regular expressions with a Position-Specific Scoring Matrix (PSSM).");
		//project.setUrl("http://wregex.ehubio.es");
		project.setUrl("http://ehubio.ehu.eus/wregex");
		projects.add(project);
		
		project = new Project();
		project.setName("PAnalyzer");
		project.setDescription(
			"A software tool focused on the protein inference process of shotgun proteomics. " +
			"Our approach considers all the identified proteins and groups them when necessary indicating their confidence using different evidence categories. " +
			"PAnalyzer can read protein identification files in the XML output format of the ProteinLynx Global Server (PLGS) software provided by Waters Corporation for their MSE data, " +
			"and also in the mzIdentML format recently standardized by HUPO-PSI.");
		project.setUrl("http://code.google.com/p/ehu-bio/wiki/PAnalyzer");
		projects.add(project);
		
		project = new Project();
		project.setName("MyMRM");
		project.setDescription(
			"MyMRM is a simple software tool to aid proteomic laboratories designing targeted proteomics methods " +
			"for their own equipment by using the data of their shotgun experiments.");
		project.setUrl("http://code.google.com/p/ehu-bio/wiki/MyMRM");
		projects.add(project);
		
		project = new Project();
		project.setName("spHPP");
		project.setDescription(
			"Contributions to the Spanish bioinformatics group of the Chromosome-centric Human Proteome Project (C-HPP).");
		project.setUrl("https://code.google.com/p/s-chpp/");
		projects.add(project);
	}
	
	public List<Project> getProjects() {
		return projects;
	}
}