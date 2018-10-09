package es.ehubio.ubase.pl;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;

import es.ehubio.ubase.bl.Ubase;
import es.ehubio.ubase.dl.input.Metadata;

@Named
@SessionScoped
public class AdminView implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private Ubase ubase;
	private List<Metadata> submissions;
	
	public List<Metadata> getSubmissions() throws Exception {
		if( submissions == null )
			refresh();
		return submissions;
	}
	
	public void refresh() throws Exception {
		submissions = ubase.getPendingSubmissions();
	}
	
	public void remove(Metadata metadata) throws Exception {
		FileUtils.deleteDirectory(metadata.getData());
		refresh();
	}
	
	public void publish(Metadata metadata) throws Exception {
		ubase.publish(metadata);
		refresh();
	}
}
