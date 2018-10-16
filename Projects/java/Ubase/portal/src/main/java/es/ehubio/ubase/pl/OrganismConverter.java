package es.ehubio.ubase.pl;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import es.ehubio.ubase.dl.entities.Taxon;

@FacesConverter("organismConverter")
public class OrganismConverter implements Converter<Taxon>{
	@Inject
	private FeedView feedView;

	@Override
	public Taxon getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		try {
			int id = Integer.parseInt(arg2);
			for( Taxon taxon : feedView.getTaxons() )
				if( taxon.getId() == id )
					return taxon;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Taxon arg2) {
		return arg2.getId()+"";
	}

}
