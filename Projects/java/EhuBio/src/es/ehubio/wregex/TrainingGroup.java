package es.ehubio.wregex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class TrainingGroup implements Collection<TrainingMotif> {
	private final double weight;
	
	public TrainingGroup( ResultGroup group, double weight ) {
		this.weight = weight;
		list = new ArrayList<>();
		for( Result r : group )
			list.add(new TrainingMotif(r, this));
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public Iterator<TrainingMotif> iterator() {
		return list.iterator();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean add(TrainingMotif e) {
		return list.add(e);
	}

	@Override
	public boolean remove(Object o) {
		if( o instanceof Result ) {
			Result result = (Result)o;
			for( TrainingMotif motif : this )
				if( motif.linked(result) )
					return remove(motif);
			return false;
		}
		return list.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends TrainingMotif> c) {
		return list.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public void clear() {
		list.clear();
	}

	public double getWeight() {
		return weight;
	}

	private List<TrainingMotif> list;
}
