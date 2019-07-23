package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.RepScore;
import es.ehubio.dubase.dl.input.ScoreType;
import es.ehubio.dubase.pl.beans.DetailsBean;
import es.ehubio.dubase.pl.beans.SearchBean;

@Named
@SessionScoped
public class DetailsView implements Serializable {
	private static final long serialVersionUID = 1L;
	private DetailsBean detailsBean;
	
	public String showDetails(SearchBean searchBean) {
		detailsBean = new DetailsBean(searchBean);
		Evidence evBean = searchBean.getEntity();
		
		DetailsBean.Sample sample = new DetailsBean.Sample();
		sample.setName("Sample");
		sample.setLfq(String.format("%.2f", meanLfq(evBean, false)));
		sample.getLfqs().addAll(getLfqs(evBean, false));
		detailsBean.getSamples().add(sample);
		
		DetailsBean.Sample control = new DetailsBean.Sample();
		control.setName("Control");
		control.setLfq(String.format("%.2f", meanLfq(evBean, true)));
		control.getLfqs().addAll(getLfqs(evBean, true));
		detailsBean.getSamples().add(control);
		
		return "details";
	}

	public void setResult(Evidence evBean) {
		
		DetailsBean.Sample sample = new DetailsBean.Sample();
		sample.setName("Sample");
		sample.setLfq(String.format("%.2f", meanLfq(evBean, false)));
		sample.getLfqs().addAll(getLfqs(evBean, false));
		detailsBean.getSamples().add(sample);
		
		DetailsBean.Sample control = new DetailsBean.Sample();
		control.setName("Control");
		control.setLfq(String.format("%.2f", meanLfq(evBean, true)));
		control.getLfqs().addAll(getLfqs(evBean, true));
		detailsBean.getSamples().add(control);
	}
	
	private static double meanLfq(Evidence ev, boolean control) {
		return ev.getRepScores().stream()
			.filter(s->s.getReplicateBean().getConditionBean().getControl()==control && s.getScoreType().getId() == ScoreType.LFQ_INTENSITY.ordinal())
			.collect(Collectors.averagingDouble(RepScore::getValue));
	}

	private static List<String> getLfqs(Evidence ev, boolean control) {
		return ev.getRepScores().stream()
			.filter(s->s.getReplicateBean().getConditionBean().getControl()==control && s.getScoreType().getId() == ScoreType.LFQ_INTENSITY.ordinal())
			.map(DetailsView::parseLfq)
			.collect(Collectors.toList());
	}

	private static String parseLfq(RepScore score) {
		char tag = score.getImputed() ? 'i' : 'b';
		return String.format("<%c>%.2f</%c>", tag, score.getValue(), tag);
	}

	public DetailsBean getDetailsBean() {
		return detailsBean;
	}

	public String getPhospho() {
		return detailsBean.getSearchBean().getEntity().getAmbiguities().stream().map(a->a.getProteinBean().getAccession()).collect(Collectors.joining("\n"));
	}
}
