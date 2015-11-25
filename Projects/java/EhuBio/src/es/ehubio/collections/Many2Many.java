package es.ehubio.collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Many2Many<L,R> {
	public void fwlink( L left, R right ) {
		fwlinkAux(left, right);
		revlinkAux(right, left);
	}
	
	public void revlink( R right, L left ) {
		revlinkAux(right, left);
		fwlinkAux(left, right);
	}
	
	public void lunlink( L left ) {
		Set<R> set = getRvalues(left);
		if( set != null )
			for( R right : set )
				getLvalues(right).remove(left);
		mapL.remove(left);
	}
	
	public void runlink( R right ) {
		Set<L> set = getLvalues(right);
		if( set != null )
			for( L left : set )
				getRvalues(left).remove(right);
		mapR.remove(right);
	}
	
	public Set<R> getRvalues( L left ) {
		return mapL.get(left);
	}
	
	public Set<R> getRvalues() {
		return mapR.keySet();
	}
	
	public Set<L> getLvalues( R right ) {
		return mapR.get(right);
	}
	
	public Set<L> getLvalues() {
		return mapL.keySet();
	}
	
	private void fwlinkAux( L left, R right ) {
		Set<R> set = mapL.get(left);
		if( set == null ) {
			set = new HashSet<>();
			mapL.put(left, set);
		}
		set.add(right);
	}
	
	private void revlinkAux( R right, L left ) {
		Set<L> set = mapR.get(right);
		if( set == null ) {
			set = new HashSet<>();
			mapR.put(right, set);
		}
		set.add(left);
	}

	private final Map<L, Set<R>> mapL = new HashMap<>();
	private final Map<R, Set<L>> mapR = new HashMap<>();
}
