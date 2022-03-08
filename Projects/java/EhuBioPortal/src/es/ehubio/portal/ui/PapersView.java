package es.ehubio.portal.ui;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import es.ehubio.portal.bl.Orcid;
import es.ehubio.portal.bl.Work;

@Named
@RequestScoped
public class PapersView {
	private final Orcid orcid = new Orcid();
	private List<Work> works;

	public List<Work> getWorks() {
		if( works == null ) {
			works = new ArrayList<>();
			Integer year = null;
			for( Work work : orcid.getWorks() ) {
				if( work.getYear() < 2012 )
					continue;
				if( work.getJournal() != null )
					if( work.getJournal().contains("IEEE") || work.getJournal().contains("URSI") || work.getJournal().contains("Image Communication") || work.getJournal().contains("EuCAP") )
						continue;
				if( year == null || !year.equals(work.getYear()) ) {
					year = work.getYear();
					work.setFirst(true);
				}
				works.add(work);
			}
		}
		return works;
	}	
}
