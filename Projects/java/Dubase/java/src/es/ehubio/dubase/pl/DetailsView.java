package es.ehubio.dubase.pl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import es.ehubio.dubase.bl.beans.EvidenceBean;

@Named
@RequestScoped
public class DetailsView {
	private EvidenceBean evBean;
	private DetailsBean detailsBean;

	public void setResult(EvidenceBean evBean, SearchBean searchBean) {
		this.evBean = evBean;
		this.detailsBean = new DetailsBean(searchBean);
	}
	
	public EvidenceBean getEvBean() {
		return evBean;
	}
	
	public DetailsBean getDetailsBean() {
		return detailsBean;
	}
}
