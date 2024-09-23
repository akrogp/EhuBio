package panalyzer.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Item {
	private String id;
	private List<Item> items = new ArrayList<>();
	private Map<String, String> props = new LinkedHashMap<>();
	private Object type;
	
	public Item() {		
	}
	
	public Item(String id) {
		setId(id);
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public List<Item> getItems() {
		return items;
	}
	
	public Map<String, String> getProps() {
		return props;
	}
	
	public Object getType() {
		return type;
	}
	
	public void setType(Object type) {
		this.type = type;
	}
}
