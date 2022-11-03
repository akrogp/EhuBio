package es.ehubio.wregex.view;

import java.util.Map;
import java.util.TreeMap;

public class CompatibilityBean {
	private String os;
	private String osVersion;
	private final Map<String, String> browser = new TreeMap<>();
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public Map<String, String> getBrowser() {
		return browser;
	}
}
