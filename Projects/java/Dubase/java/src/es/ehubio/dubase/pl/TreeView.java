package es.ehubio.dubase.pl;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.mindmap.DefaultMindmapNode;
import org.primefaces.model.mindmap.MindmapNode;

import es.ehubio.dubase.bl.ClassBean;
import es.ehubio.dubase.bl.Database;
import es.ehubio.dubase.bl.EnzymeBean;
import es.ehubio.dubase.bl.SuperfamilyBean;
import es.ehubio.dubase.bl.TreeBean;

@Named
@RequestScoped
public class TreeView {
	private MindmapNode root;
	@EJB
	private Database db;
	
	public TreeView() {
		root = new DefaultMindmapNode("DUBs", "Deubiquitinating enzymes", "CC0000", false);
	}
	
	@PostConstruct
	public void populate() {
		TreeBean tree = db.getTree();
		for(ClassBean clazz : tree.getClassess()) {
			MindmapNode classNode = new DefaultMindmapNode(clazz.getEntity().getName(), null, "00CC00", false);
			root.addNode(classNode);
			for( SuperfamilyBean family : clazz.getSuperfamilies() ) {
				MindmapNode familyNode = new DefaultMindmapNode(family.getEntity().getShortname(), family.getEntity().getName(), "0000CC", false);
				classNode.addNode(familyNode);
				for( EnzymeBean enzyme : family.getEnzymes() ) {
					MindmapNode enzymeNode = new DefaultMindmapNode(enzyme.getEntity().getGene(), enzyme.getEntity().getDescription(), "FFCC00", false);
					classNode.addNode(enzymeNode);
				}
			}
		}
	}
	
	public MindmapNode getRoot() {
		return root;
	}
	
	public void onNodeSelect(SelectEvent event) {
        MindmapNode node = (MindmapNode) event.getObject();
	}
}
