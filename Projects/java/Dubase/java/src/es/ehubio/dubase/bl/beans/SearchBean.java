package es.ehubio.dubase.bl.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SearchBean {
	private String enzyme;
	private String substrate;
	private Map<Integer, Double> mapScores;
	
	public String getEnzyme() {
		return enzyme;
	}
	public void setEnzyme(String enzyme) {
		this.enzyme = enzyme;
	}
	public String getSubstrate() {
		return substrate;
	}
	public void setSubstrate(String substrate) {
		this.substrate = substrate;
	}
	public Map<Integer, Double> getMapScores() {
		if( mapScores == null )
			mapScores = new HashMap<>();
		return mapScores;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof SearchBean) )
			return super.equals(obj);
		SearchBean other = (SearchBean)obj;
		return enzyme.equals(other.enzyme) && substrate.equals(other.substrate);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(enzyme, substrate);
	}
}
