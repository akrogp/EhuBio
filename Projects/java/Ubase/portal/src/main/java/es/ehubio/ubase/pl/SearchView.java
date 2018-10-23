package es.ehubio.ubase.pl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import es.ehubio.io.CsvUtils;
import es.ehubio.ubase.bl.Usearch;
import es.ehubio.ubase.bl.result.ModificationResult;
import es.ehubio.ubase.bl.result.PeptideResult;
import es.ehubio.ubase.bl.result.ProteinResult;

@Named
@SessionScoped
public class SearchView implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private Usearch ubase;
	private List<PeptideResult> pepResults;
	private String query;
	
	public void peptideSearch() {
		peptideSearch(query.trim());
	}
	
	public void peptideSearch(String seq) {
		query = seq;
		setPepResults(ubase.peptideSearch(seq));
	}
	
	public void textSearch() {
		textSearch(query.trim());
	}
	
	public void textSearch(String text) {
		query = text;
		setPepResults(ubase.textSearch(text));
	}
	
	public void expSearch() {
		expSearch(query.trim());
	}
	
	public void expSearch(String acc) {
		query = acc;
		setPepResults(ubase.expSearch(acc));
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	public void reset() {
		setQuery(null);
		setPepResults(null);
	}
	
	public String buildHtmlSeq(PeptideResult pep) {
		Set<Integer> positions = new HashSet<>();
		for( ModificationResult mod : pep.getMods() )
			positions.add(mod.getPosition());
		StringBuilder sb = new StringBuilder();
		if( pep.getPrev() != null )
			sb.append(pep.getPrev());
		sb.append('-');
		for( int i = 0; i < pep.getSequence().length(); i++ ) {
			boolean isMod = positions.contains(i+1);
			if( isMod )
				sb.append("<b>");
			sb.append(pep.getSequence().charAt(i));
			if( isMod )
				sb.append("</b>");
		}
		sb.append('-');
		if( pep.getAfter() != null )
			sb.append(pep.getAfter());
		return sb.toString();
	}
	
	public String buildProtPositions(PeptideResult pep, ProteinResult prot) {
		List<Integer> positions = new ArrayList<>();
		for( ModificationResult mod : pep.getMods() )
			positions.add(mod.getPosition()+prot.getPosition()-1);
		Collections.sort(positions);
		return CsvUtils.getCsv(", ", positions.toArray());
	}

	public List<PeptideResult> getPepResults() {
		return pepResults;
	}

	public void setPepResults(List<PeptideResult> pepResults) {
		this.pepResults = pepResults;
	}
}
