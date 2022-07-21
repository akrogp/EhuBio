package es.ehubio.wregex.data;

@SuppressWarnings("all")
public final class Versions {
	private Versions() {}
	
	public static final int MAJOR = 2;
	//public static final int MAJOR = 3;
	public static final int MINOR = 2;
	//public static final int MINOR = 0;
	//public static final int ALPHA = 0;
	public static final int ALPHA = 1;
	public static boolean DEV = ALPHA != 0;
	public static boolean PROD = !DEV;
	public static final String SIGN = String.format("Wregex (v%d.%d%s)", MAJOR, MINOR, DEV?"-alpha"+ALPHA:"");	
}
