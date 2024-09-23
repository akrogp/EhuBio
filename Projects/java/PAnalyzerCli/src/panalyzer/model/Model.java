package panalyzer.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Model {
	private final Map<String, Item> peptides = new LinkedHashMap<>();
	private final Map<String, Item> proteins = new HashMap<>();
	
	public Map<String, Item> getPeptides() {
		return peptides;
	}
	
	public Map<String, Item> getProteins() {
		return proteins;
	}
}
