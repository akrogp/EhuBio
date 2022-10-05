package es.ehubio.db.go;

public class Term {
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public boolean isObsolete() {
		return obsolete;
	}
	public void setObsolete(boolean obsolete) {
		this.obsolete = obsolete;
	}
	private String id, name, namespace;
	private boolean obsolete;
}
