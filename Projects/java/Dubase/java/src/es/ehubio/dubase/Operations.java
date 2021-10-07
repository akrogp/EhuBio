package es.ehubio.dubase;

public final class Operations {
	public static final double LOG2 = Math.log(2);
	
	private Operations() {}
	
	public static Double log2(Double num) {
		if( num == null )
			return null;
		return Math.log(num)/LOG2;
	}
	
	public static Double colog(Double num) {
		if( num == null )
			return null;
		return -Math.log10(num);
	}
	
	public static Double pow2(Double num) {
		if( num == null )
			return null;
		return Math.pow(2, num);
	}
	
	public static Double copow(Double num) {
		if( num == null )
			return null;
		return Math.pow(10, -num);
	}
	
	public static Integer toint(Double num) {
		if( num == null )
			return null;
		return num.intValue();
	}
	
	public static String null2str(Object obj) {
		return obj == null ? "" : obj.toString();
	}
}
