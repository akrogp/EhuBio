package es.ehubio.model;

public class ProteinModification {
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ProteinModificationType getType() {
		return type;
	}
	public void setType(ProteinModificationType type) {
		this.type = type;
	}
	public String getResidues() {
		return residues;
	}
	public Character getAminoacid() {
		if( residues == null || residues.length() != 1 )
			return null;
		return residues.charAt(0);
	}
	public void setResidues(String residues) {
		this.residues = residues;
	}
	public Integer getFrom() {
		return from;
	}
	public void setFrom(Integer from) {
		this.from = from;
	}
	public Integer getTo() {
		return to;
	}
	public void setTo(Integer to) {
		this.to = to;
	}
	public Integer getPosition() {
		return from;
	}
	public void setPosition( Integer pos ) {
		from=to=pos;
	}	
	private String name;
	private ProteinModificationType type;
	private String residues;
	private Integer from;
	private Integer to;
}