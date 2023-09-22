package es.ehubio.db.elm;

public class ElmInstance {
	private String acc;
	private String type;
	private String cls;
	private String protName;
	private String protAcc;
	private String protAccs;
	private int start;
	private int end;
	private String refs;
	private String methods;
	private String logic;
	private String pdb;
	private String organism;
	
	public String getAcc() {
		return acc;
	}
	public void setAcc(String acc) {
		this.acc = acc;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCls() {
		return cls;
	}
	public void setCls(String cls) {
		this.cls = cls;
	}
	public String getProtName() {
		return protName;
	}
	public void setProtName(String protName) {
		this.protName = protName;
	}
	public String getProtAcc() {
		return protAcc;
	}
	public void setProtAcc(String protAcc) {
		this.protAcc = protAcc;
	}
	public String getProtAccs() {
		return protAccs;
	}
	public void setProtAccs(String protAccs) {
		this.protAccs = protAccs;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public String getRefs() {
		return refs;
	}
	public void setRefs(String refs) {
		this.refs = refs;
	}
	public String getMethods() {
		return methods;
	}
	public void setMethods(String methods) {
		this.methods = methods;
	}
	public String getLogic() {
		return logic;
	}
	public void setLogic(String logic) {
		this.logic = logic;
	}
	public String getPdb() {
		return pdb;
	}
	public void setPdb(String pdb) {
		this.pdb = pdb;
	}
	public String getOrganism() {
		return organism;
	}
	public void setOrganism(String organism) {
		this.organism = organism;
	}	
}
