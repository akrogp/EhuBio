package es.ehubio.cli;

public class Argument {
	public Argument(int id, Character shortOption, String longOption) {
		this.id = id;
		this.shortOption = shortOption;
		this.longOption = longOption;
	}
	@Override
	public String toString() {
		return String.format(
			"%s%s",
			getLongOption()==null ? getShortOption() : getLongOption(),
			getParam()==null ? "" : "="+getParam()
		);
	}
	public Character getShortOption() {
		return shortOption;
	}
	public String getLongOption() {
		return longOption;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isOptional() {
		return optional;
	}
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	public void setOptional() {
		setOptional(true);
	}
	public int getId() {
		return id;
	}
	private final int id;
	private final Character shortOption;
	private final String longOption;
	private String param;
	private String description;
	private boolean optional = false;
}
