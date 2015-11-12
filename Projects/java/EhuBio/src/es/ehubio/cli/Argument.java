package es.ehubio.cli;

import java.util.LinkedHashSet;
import java.util.Set;

public class Argument {
	public Argument(int id, Character shortOption, String longOption) {
		this(id,shortOption,longOption,false);
	}
	
	public Argument(int id, Character shortOption, String longOption, boolean optional) {
		this.id = id;
		this.shortOption = shortOption;
		this.longOption = longOption;
		this.optional = optional;
	}
	@Override
	public String toString() {
		return String.format(
			"%s%s",
			getLongOption()==null ? getShortOption() : getLongOption(),
			getValue()==null ? "" : "="+getValue()
		);
	}
	public Character getShortOption() {
		return shortOption;
	}
	public String getLongOption() {
		return longOption;
	}
	public String getParamName() {
		return param;
	}
	public Set<String> getChoices() {
		return choices;
	}
	public String getValue() {
		return value;
	}
	public void setValue( String value ) {
		this.value = value;
	}
	public void setParamName(String param) {
		this.param = param;
	}
	public void setChoices(String... params) {
		choices = new LinkedHashSet<String>();
		for( String param : params )
			choices.add(param);
	}
	public void setBoolean() {
		setChoices(Boolean.TRUE.toString(), Boolean.FALSE.toString());
	}
	public boolean usesParam() {
		return getParamName() != null || getChoices() != null;
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
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(Object defaultValue) {
		if( defaultValue == null )
			this.defaultValue = null;
		else {
			this.defaultValue = defaultValue.toString();
			setOptional();
		}
	}
	public void setDefaultValue() {
		setDefaultValue(getParamName());
	}
	private final int id;
	private final Character shortOption;
	private final String longOption;
	private String param;
	private Set<String> choices;
	private String value;
	private String defaultValue;
	private String description;
	private boolean optional = false;
}
