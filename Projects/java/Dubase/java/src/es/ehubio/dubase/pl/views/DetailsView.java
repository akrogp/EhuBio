package es.ehubio.dubase.pl.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import es.ehubio.dubase.dl.entities.Ambiguity;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.Modification;
import es.ehubio.dubase.dl.entities.RepScore;
import es.ehubio.dubase.dl.entities.Score;
import es.ehubio.dubase.dl.input.ScoreType;
import es.ehubio.dubase.pl.Formats;
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
		detailsBean.setScoreType(selectScore(evBean.getRepScores()));
		Ambiguity leading = evBean.getAmbiguities().get(0);
		Modification mod = leading.getModifications().isEmpty() ? null : leading.getModifications().get(0);
		if( detailsBean.getScoreType() != null ) {
			detailsBean.setReps(getReps(evBean.getRepScores()));
			fillEvScores();
		} else if( mod != null ) {
			detailsBean.setScoreType(selectScore(mod.getRepScores()));
			if( detailsBean.getScoreType() != null )
				detailsBean.setReps(getReps(mod.getRepScores()));
			fillModScores(leading);
		}
		return "details";
	}	

	private void fillEvScores() {
		Evidence evBean = detailsBean.getSearchBean().getEntity();
		es.ehubio.dubase.dl.entities.ScoreType scoreType = detailsBean.getScoreType();
		for( String cond : getCases(evBean.getRepScores()) ) {
			DetailsBean.Result sample = new DetailsBean.Result();
			sample.setName(cond);
			List<RepScore> scores = filterScores(evBean.getRepScores(), cond, scoreType);
			sample.setAvgScore(avgScore(scores));
			sample.getScores().addAll(parseScores(scores));
			detailsBean.getSamples().add(sample);
		}
	}
	
	private void fillModScores(Ambiguity leading) {
		for( String cond : getCases(leading.getModifications().get(0).getRepScores()) ) {
			es.ehubio.dubase.dl.entities.ScoreType scoreType = detailsBean.getScoreType();
			List<DetailsBean.Result> results = new ArrayList<>();
			detailsBean.getMods().put(cond, results);
			for( Modification mod : leading.getModifications() ) {
				DetailsBean.Result result = new DetailsBean.Result();
				result.setName("K"+mod.getPosition());
				List<RepScore> scores = filterScores(mod.getRepScores(), cond, scoreType);
				result.setAvgScore(avgScore(scores));
				result.setpValue(pValue(mod.getScores()));
				result.getScores().addAll(parseScores(scores));
				results.add(result);
			}
		}
	}	

	private es.ehubio.dubase.dl.entities.ScoreType selectScore(List<? extends RepScore> scores) {
		if( scores == null || scores.isEmpty() )
			return null;
		return scores.stream()
			.filter(s -> s.getScoreType().getId() == ScoreType.LFQ_INTENSITY_LOG2.ordinal() || s.getScoreType().getId() == ScoreType.FOLD_CHANGE.ordinal())
			.findFirst().get().getScoreType();
	}

	private List<String> getCases(List<? extends RepScore> scores) {
		return scores.stream()
			.map(s -> getCase(s))
			.distinct().collect(Collectors.toList());
	}
	
	private static String getCase(RepScore s) {
		if( s.getBasalBean() == null )
			return s.getReplicateBean().getConditionBean().getName();		
		return s.getReplicateBean().getConditionBean().getName() + " vs " + s.getBasalBean().getConditionBean().getName();
	}
	
	private static List<RepScore> filterScores(List<? extends RepScore> scores, String cond, es.ehubio.dubase.dl.entities.ScoreType scoreType) {
		return scores.stream()
			.filter(s->cond.equals(getCase(s)) && s.getScoreType().getId() == scoreType.getId())
			.collect(Collectors.toList());
	}

	private static String avgScore(List<RepScore> scores) {
		double avg = scores.stream().filter(s->s.getValue() != null).collect(Collectors.averagingDouble(s->s.getValue()));
		String score = Formats.decimal2(avg);
		boolean imputed = scores.stream().filter(s->s.getValue() == null || s.getImputed()).count() == scores.size();
		if( imputed )
			score = Formats.imputed(score);
		return Formats.total(score);
	}
	
	private String pValue(List<? extends Score> scores) {
		Score score = scores.stream().filter(s->s.getScoreType().getId() == ScoreType.P_VALUE.ordinal()).findFirst().get();
		String fmt;
		if( score == null )
			fmt = Formats.na();
		else
			fmt = Formats.exp10(score.getValue());
		return Formats.total(fmt);
	}

	private static List<String> parseScores(List<RepScore> scores) {
		return scores.stream().map(DetailsView::parseScore).collect(Collectors.toList());
	}
	
	private static List<String> getReps(List<? extends RepScore> scores) {
		String cond = getCase(scores.get(0));
		return scores.stream()
			.filter(s->cond.equals(getCase(s)))
			.map(s->s.getReplicateBean().getName())
			.collect(Collectors.toList());
	}

	private static String parseScore(RepScore score) {
		if( score.getValue() == null )
			return Formats.na();
		String fmt = Formats.decimal2(score.getValue());
		if( score.getImputed() )
			return Formats.imputed(fmt);
		return fmt;
	}

	public DetailsBean getDetailsBean() {
		return detailsBean;
	}

	public String getPhospho() {
		return detailsBean.getSearchBean().getEntity().getAmbiguities().stream().map(a->a.getProteinBean().getAccession()).collect(Collectors.joining("\n"));
	}
	
	public String getPvalueType() {
		String type = "p-value";
		if( detailsBean.getSearchBean().getEntity().getExperimentBean().getMethodBean().isAdjusted() )
			type = type + " (adjusted)";
		return type;
	}
}
