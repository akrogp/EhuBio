package es.ehubio;

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
}