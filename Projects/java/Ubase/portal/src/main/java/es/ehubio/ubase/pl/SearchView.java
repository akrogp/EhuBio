package es.ehubio.ubase.pl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import es.ehubio.ubase.bl.Usearch;
import es.ehubio.ubase.bl.result.ModificationResult;
import es.ehubio.ubase.bl.result.PeptideResult;

@Named
@SessionScoped
public class SearchView implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private Usearch ubase;
	private List<PeptideResult> pepResults;
	private String query;
	
	public void peptideSearch() {
		setPepResults(ubase.peptideSearch(query));
	}
	
	public void textSearch() {
		setPepResults(ubase.textSearch(query));
	}
	
	public void expSearch() {
		setPepResults(ubase.expSearch(query));
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
		sb.append(pep.getAfter());
		return sb.toString();
	}

	public List<PeptideResult> getPepResults() {
		return pepResults;
	}

	public void setPepResults(List<PeptideResult> pepResults) {
		this.pepResults = pepResults;
	}
}
