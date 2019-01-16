package es.ehubio.dubase.pl;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.mindmap.DefaultMindmapNode;
import org.primefaces.model.mindmap.MindmapNode;

import es.ehubio.dubase.bl.ClassBean;
import es.ehubio.dubase.bl.Database;
import es.ehubio.dubase.bl.EnzymeBean;
import es.ehubio.dubase.bl.EvidenceBean;
import es.ehubio.dubase.bl.SuperfamilyBean;
import es.ehubio.dubase.bl.TreeBean;

@Named
@RequestScoped
public class TreeView {
	private MindmapNode root;
	@EJB
	private Database db;
	@Inject
	private FeedView feed;
	
	public TreeView() {
		root = new DefaultMindmapNode("DUBs", "Deubiquitinating enzymes", "FFCC00", false);
	}
	
	@PostConstruct
	public void populate() {
		TreeBean tree = db.getTree();
		for(ClassBean clazz : tree.getClassess()) {
			MindmapNode classNode = new DefaultMindmapNode(clazz.getEntity().getName(), null, "6E9EBF", true);
			root.addNode(classNode);
			for( SuperfamilyBean family : clazz.getSuperfamilies() ) {
				MindmapNode familyNode = new DefaultMindmapNode(family.getEntity().getShortname(), family.getEntity().getName(), "82C542", true);
				classNode.addNode(familyNode);
				for( EnzymeBean enzyme : family.getEnzymes() ) {
					MindmapNode enzymeNode = new DefaultMindmapNode(
							enzyme.getEntity().getGene(), enzyme.getEntity().getDescription(),
							enzyme.getSubstrates().isEmpty() ? "3399FF" : "FCE24F",
							!enzyme.getSubstrates().isEmpty());
					familyNode.addNode(enzymeNode);
					for( EvidenceBean substrate : enzyme.getSubstrates() ) {
						MindmapNode substrateNode = new DefaultMindmapNode(
								substrate.getGene(), substrate.getGene(),
								substrate.getFoldChange() > 0 ? "00FF00" : "FF0000",
								false);
						enzymeNode.addNode(substrateNode);
					}
				}
			}
		}
	}
	
	public MindmapNode getRoot() {
		return root;
	}
	
	public void onNodeSelect(SelectEvent event) {
        MindmapNode node = (MindmapNode) event.getObject();
        if( node.getLabel().equals("FEED") )
        	feed.saveExamples();
	}
}
