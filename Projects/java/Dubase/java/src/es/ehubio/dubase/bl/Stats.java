package es.ehubio.dubase.bl;

public class Stats {
	private final String dub;
	private final long substratesCount;
	private final long papersCount;
	
	public Stats(String dub, long substratesCount, long papersCount) {
		this.dub = dub;
		this.substratesCount = substratesCount;
		this.papersCount = papersCount;
	}
	
	public long getSubstratesCount() {
		return substratesCount;
	}
	
	public long getPapersCount() {
		return papersCount;
	}
	
	public String getDub() {
		return dub;
	}
}
