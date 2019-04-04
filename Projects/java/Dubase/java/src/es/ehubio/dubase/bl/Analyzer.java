package es.ehubio.dubase.bl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import es.ehubio.dubase.bl.beans.EvidenceBean;
import es.ehubio.dubase.bl.beans.Scatter;
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
	public List<Scatter> getScatter(@PathParam("gene") String gene) {
		List<EvidenceBean> evidences = db.searchEnzyme(gene);
		List<Scatter> result = new ArrayList<>();
		for( EvidenceBean ev : evidences ) {
			Scatter scatter = new Scatter();
			scatter.setGene(CsvUtils.getCsv(';', ev.getGenes().toArray()));
			scatter.setDesc(CsvUtils.getCsv(';', ev.getDescriptions().toArray()));
			scatter.setFoldChange(ev.getMapScores().get(Score.FOLD_CHANGE.ordinal()));
			scatter.setpValue(ev.getMapScores().get(Score.P_VALUE.ordinal()));
			scatter.setpValue(Math.pow(10, -scatter.getpValue()));
			result.add(scatter);
		}
		return result;
	}

}
