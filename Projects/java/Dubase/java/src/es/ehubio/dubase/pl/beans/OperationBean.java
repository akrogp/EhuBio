package es.ehubio.dubase.pl.beans;

public class OperationBean {
	private String name;
	private String desc;
	private String action;
	private String preview;
	
	public OperationBean(String name, String action, String desc, String preview) {
		setName(name);
		setAction(action);
		setDesc(desc);
		setPreview(preview);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getPreview() {
		return preview;
	}
	public void setPreview(String preview) {
		this.preview = preview;
	}
}
