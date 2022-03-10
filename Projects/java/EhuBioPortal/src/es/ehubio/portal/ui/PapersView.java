package es.ehubio.portal.ui;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.json.JSONException;

import es.ehubio.portal.bl.EhuBio;
import es.ehubio.portal.dl.Work;

@Named
@RequestScoped
public class PapersView {
	@EJB
	private EhuBio model;
	private List<Work> works;

	public List<Work> getWorks() throws JSONException, IOException {
		if( works == null )
			works = model.getWorks();
		return works;
	}	
}
