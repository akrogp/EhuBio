package es.ehubio.wregex.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.ehubio.wregex.Result;
import es.ehubio.wregex.ResultGroup;
import es.ehubio.wregex.Wregex;

public class ResultGroupEx implements Iterable<ResultEx> {
	private final ResultGroup resultGroup;	
	private final Map<Result,ResultEx> map;
	private final Iterable<ResultEx> list;
	private String motif;
	private String motifUrl;
	
	public ResultGroupEx( ResultGroup resultGroup ) {
		this.resultGroup = resultGroup;		
		ResultEx resultEx;
		List<ResultEx> list = new ArrayList<>();
		Map<Result,ResultEx> map = new HashMap<>();
		for( Result result : resultGroup ) {
			resultEx = new ResultEx(result);
			list.add(resultEx);
			map.put(result, resultEx);
		}
		this.list = list;
		this.map = map;
	}

	public double getAssay() {
		return resultGroup.getAssay();
	}

	public ResultEx getRepresentative() {
		return map.get(resultGroup.getRepresentative());
	}

	public double getScore() {
		return resultGroup.getScore();
	}

	public int getSize() {
		return resultGroup.getSize();
	}
	
	public Iterator<ResultEx> iterator() {
		return list.iterator();
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
		for( ResultEx result : list )
			result.setMotif(motif);
	}

	public String getMotifUrl() {
		return motifUrl;
	}

	public void setMotifUrl(String motifUrl) {
		this.motifUrl = motifUrl;
		for( ResultEx result : list )
			result.setMotifUrl(motifUrl);
	}

	public Wregex getWregex() {
		return iterator().next().getWregex();
	}

	public void setWregex(Wregex wregex) {
		for( ResultEx result : this )
			result.setWregex(wregex);
	}
}