package es.ehubio.mymrm.data;

public final class LatestNew {
	private final String date;
	private final String summary;
	
	public LatestNew( String date, String summary ) {
		this.date = date;
		this.summary = summary;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getSummary() {
		return summary;
	}
}
