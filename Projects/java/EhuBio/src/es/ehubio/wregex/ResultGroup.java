package es.ehubio.wregex;

import java.util.Iterator;

public final class ResultGroup implements Iterable<Result> {
	ResultGroup( Iterable<Result> list ) {
		this.list = list;
		int size = 0;
		for(@SuppressWarnings("unused") Result result : list )
			size++;
		this.size = size;
		this.representative = searchRespresentative();
		this.assay = -1.0;
	}
	
	void updateAssay() {		
		for( Result result : list )
			if( result.getAssay() > assay )
				assay = result.getAssay();
	}
	
	@Override
	public Iterator<Result> iterator() {
		return list.iterator();
	}
	
	public int getSize() {
		return size;
	}
	
	private Result searchRespresentative() {
		Result result = null;
		for( Result tmp : this ) {
			if( result == null ) {
				result = tmp;
				continue;
			}
			if( tmp.getScore() > result.getScore() ) {
				result = tmp;
				continue;
			}
			if( tmp.getScore() == result.getScore() && tmp.getMatch().length() > result.getMatch().length() )
				result = tmp;
		}
		return result;
	}

	public Result getRepresentative() {
		return representative;
	}
	
	public double getScore() {
		return representative.getScore();
	}

	public double getAssay() {
		return assay;
	}

	private final Iterable<Result> list;
	private final int size;
	private final Result representative;
	private double assay;
}
