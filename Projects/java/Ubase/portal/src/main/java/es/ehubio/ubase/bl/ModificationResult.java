package es.ehubio.ubase.bl;

public class ModificationResult extends SearchResult {
	private String name;
	private String description;
	private Double deltaMass;
	private int position;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getDeltaMass() {
		return deltaMass;
	}
	public void setDeltaMass(Double deltaMass) {
		this.deltaMass = deltaMass;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
}
