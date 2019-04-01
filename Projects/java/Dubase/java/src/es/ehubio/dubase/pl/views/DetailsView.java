package es.ehubio.dubase.pl.views;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import es.ehubio.dubase.bl.Score;
import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.bl.beans.RepScoreBean;
import es.ehubio.dubase.bl.beans.ReplicateBean;
import es.ehubio.dubase.pl.beans.DetailsBean;
import es.ehubio.dubase.pl.beans.SearchBean;
import es.ehubio.dubase.pl.beans.DetailsBean.Sample;

@Named
@RequestScoped
public class DetailsView {
	private DetailsBean detailsBean;

	public void setResult(EvidenceBean evBean, SearchBean searchBean) {
		detailsBean = new DetailsBean(searchBean);
		
		DetailsBean.Sample sample = new DetailsBean.Sample();
		sample.setName("Sample");
		sample.setLfq(String.format("%.2f", meanLfq(evBean.getSamples())));
		sample.getLfqs().addAll(getLfqs(evBean.getSamples()));
		detailsBean.getSamples().add(sample);
		
		DetailsBean.Sample control = new DetailsBean.Sample();
		control.setName("Control");
		control.setLfq(String.format("%.2f", meanLfq(evBean.getControls())));
		control.getLfqs().addAll(getLfqs(evBean.getControls()));
		detailsBean.getSamples().add(control);
		
		detailsBean.setCoverage(String.format("%.1f", evBean.getMapScores().get(Score.SEQ_COVERAGE.ordinal())));
	}
	
	private double meanLfq(List<ReplicateBean> reps) {
		double lfq = 0;
		for( ReplicateBean rep : reps )
			lfq += rep.getMapScores().get(Score.LFQ_INTENSITY.ordinal()).getValue();
		return lfq/reps.size();
	}

	private List<String> getLfqs(List<ReplicateBean> reps) {
		List<String> lfqs = new ArrayList<>();
		for( ReplicateBean rep : reps )
			lfqs.add(parseLfq(rep.getMapScores().get(Score.LFQ_INTENSITY.ordinal())));
		return lfqs;
	}

	private String parseLfq(RepScoreBean score) {
		char tag = score.isImputed() ? 'i' : 'b';
		return String.format("<%c>%.2f</%c>", tag, score.getValue(), tag);
	}

	public DetailsBean getDetailsBean() {
		return detailsBean;
	}
}
