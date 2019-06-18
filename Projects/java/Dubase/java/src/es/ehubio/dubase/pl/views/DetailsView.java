package es.ehubio.dubase.pl.views;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.RepScore;
import es.ehubio.dubase.dl.input.ScoreType;
import es.ehubio.dubase.pl.beans.DetailsBean;
import es.ehubio.dubase.pl.beans.SearchBean;

@Named
@RequestScoped
public class DetailsView {
	private DetailsBean detailsBean;

	public void setResult(Evidence evBean, SearchBean searchBean) {
		detailsBean = new DetailsBean(searchBean);
		
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
		
		detailsBean.setCoverage(String.format("%.1f", evBean.getScore(ScoreType.SEQ_COVERAGE)));
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
}
