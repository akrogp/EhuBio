package es.ehubio.ubase.pl;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;

import es.ehubio.bl.Ubase;
import es.ehubio.dl.input.Metadata;

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
}
