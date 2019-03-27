package es.ehubio.dubase.pl;

public class OperationBean {
	private String name;
	private String desc;
	private String action;
	
	public OperationBean(String name, String action, String desc) {
		setName(name);
		setAction(action);
		setDesc(desc);
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
}
