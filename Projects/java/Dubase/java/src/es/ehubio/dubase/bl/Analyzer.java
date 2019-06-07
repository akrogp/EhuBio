package es.ehubio.dubase.bl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import es.ehubio.dubase.Thresholds;
import es.ehubio.dubase.bl.beans.Scatter;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.input.ScoreType;
import es.ehubio.io.CsvUtils;

@LocalBean
@Stateless
@Path("/analyze")
public class Analyzer {
	@EJB
	private Searcher db;
	
	@Path("{gene}.json")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Scatter> getScatter(@PathParam("gene") String gene, @QueryParam("xth") Double xth, @QueryParam("yth") Double yth) {
		Thresholds th = new Thresholds();
		th.setDown(true); // for vulcano
		if( xth != null )
			th.setLog2FoldChange(xth);
		if( yth != null )
			th.setLog10PValue(yth);
		List<Evidence> evidences = db.searchEnzyme(gene, th);
		List<Scatter> result = new ArrayList<>();
		for( Evidence ev : evidences ) {
			Scatter scatter = new Scatter();
			scatter.setGene(CsvUtils.getCsv(';',ev.getAmbiguities().stream()
				.map(amb->amb.getProteinBean().getGeneBean().getName())
				.collect(Collectors.toList()).toArray()));
			scatter.setDesc(CsvUtils.getCsv(';', ev.getAmbiguities().stream()
				.map(amb->amb.getProteinBean().getDescription())
				.collect(Collectors.toList()).toArray()));
			scatter.setFoldChange(ev.getEvScores().stream()
				.filter(score->score.getScoreType().getId() == ScoreType.FOLD_CHANGE.ordinal())
				.findFirst().get().getValue());
			scatter.setpValue(ev.getEvScores().stream()
				.filter(score->score.getScoreType().getId() == ScoreType.P_VALUE.ordinal())
				.findFirst().get().getValue());
			scatter.setpValue(Math.pow(10, -scatter.getpValue()));
			result.add(scatter);
		}
		return result;
	}

}
