package es.ehubio;

import java.lang.reflect.Array;
import java.util.Map;

public final class Util {
	public static boolean compare( Object o1, Object o2 ) {
		if( o1 == null && o2 == null )
			return true;
		if( o1 == null && o2 != null )
			return false;
		if( o1 != null && o2 == null )
			return false;
		return o1.equals(o2);
	}
	
	public static int hashCode( Object o1, Object o2 ) {
		int hash = 7;
		if( o1 != null )
			hash = 29*hash + o1.hashCode();
		if( o2 != null )
			hash = 29*hash + o2.hashCode();
		return hash;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object o) {
		if( o == null )
			return true;
		if( o instanceof Number && ((Number)o).doubleValue() == 0.0 )
			return true;
		if( o.getClass().isArray() ) {
			int len = Array.getLength(o);
			if( len == 0 )
				return true;
		}
		if( o instanceof CharSequence && ((CharSequence)o).length() == 0 )
			return true;
		if( o instanceof Iterable && !((Iterable)o).iterator().hasNext() )
			return true;
		if( o instanceof Map && ((Map)o).isEmpty() )
			return true;
		return false;
	}
}