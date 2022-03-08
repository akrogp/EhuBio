package es.ehubio.portal.ui;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class ProjectsView {
	private final List<Project> projects = new ArrayList<Project>();
	
	public ProjectsView() {
		Project prj;
		
		prj = new Project();
		prj.setTitle("DUBase");
		prj.setSubtile("Human Deubiquitinating Enzymes' Substrate Database");
		prj.setUrl("https://ehubio.ehu.eus/dubase/");
		prj.setPreview("img/dubase.jpg");
		prj.setPaper("https://doi.org/10.1016/j.semcdb.2022.01.001");
		prj.setDesc(
			"Database of high-confidence human DUB substrates obtained from large scale proteomics experiments and from manual curation. " +
			"Custom quality thresholds for large-scale experiments can be defined by the user. " +
			"Data can be browsed graphically or queried by the gene name of the DUB or substrate of interest. " +
			"Integration with external tools and databases is also available."
		);
		projects.add(prj);
		
		prj = new Project();
		prj.setTitle("Wregex");
		prj.setSubtile("Search for Linear Protein Motifs using Scores");
		prj.setUrl("https://ehubio.ehu.eus/wregex/");
		prj.setPreview("img/wregex.jpg");
		prj.setPaper("https://www.nature.com/articles/srep25869");
		prj.setDesc(
			"Wregex (weighted regular expression) combines a regular expression with a Position-Specific Scoring Matrix (PSSM) " + 
			"to efficiently search large databases of protein sequences (i.e. the human proteome) for linear motifs and " + 
			"prioritize candidates for experimental testing. Wregex can also be integrated with COSMIC to predict the impact on " +
			"motif scores of recurrent mutations found in cancer samples."
		);
		projects.add(prj);
		
		prj = new Project();
		prj.setTitle("LPGF");
		prj.setSubtile("Protein Probabilities for Shotgun Proteomics");
		prj.setUrl("https://github.com/akrogp/SpHPP/tree/master/dist/LPGF");
		prj.setPreview("img/lpgf.png");
		prj.setPaper("https://pubs.acs.org/doi/10.1021/acs.jproteome.9b00819");
		prj.setDesc(
			"LPGF is a novel protein-level scoring algorithm that uses the scores of the identified peptides and maintains all of the " +
			"properties expected for a true protein probability. We also present a refinement of the picked method to calculate FDR at the protein level."
		);
		projects.add(prj);
		
		prj = new Project();
		prj.setTitle("PAnalyzer");
		prj.setSubtile("Protein Inference in Shotgun Proteomics");
		prj.setUrl("https://code.google.com/archive/p/ehu-bio/wikis/PAnalyzer.wiki");
		prj.setPreview("img/panalyzer.png");
		prj.setPaper("https://bmcbioinformatics.biomedcentral.com/articles/10.1186/1471-2105-13-288");
		prj.setDesc(
			"PAnalyzer is focused on the protein inference problem of shotgun proteomics. Our approach considers all the identified proteins " +
			"and groups them into protein ambiguity groups indicating their confidence using different evidence categories."
		);
		projects.add(prj);
		
		prj = new Project();
		prj.setTitle("MyMRM");
		prj.setSubtile("Design Targeted SRM/MRM Proteomics Experiments");
		prj.setUrl("https://code.google.com/archive/p/ehu-bio/wikis/MyMRM.wiki");
		prj.setPreview("img/mymrm.png");
		prj.setPaper("https://drive.google.com/file/d/0B1U_FilyidMsamIzTDZFN0ZVa2s/view?usp=sharing");
		prj.setDesc(
			"MyMRM helps selecting proteotypic peptides, precursor and fragment ions for a a given protein using the information of "+
			"shotgun experiments from the own lab."
		);
		projects.add(prj);
	}
	
	public List<Project> getProjects() {
		return projects;
	}
}
