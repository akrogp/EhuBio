package es.ehubio.wregex.data;

import java.util.ArrayList;
import java.util.List;

public class BubbleChartData {
	private String name = "";	
	private String description = "";
	private String discretion = "";
	private String result = "";
	private int size = 0;
	private List<BubbleChartData> children = new ArrayList<>();
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getTotalSize() {
		if( children == null || children.isEmpty() )
			return getSize();
		int totalSize = 0;
		for( BubbleChartData child : children )
			totalSize += child.getTotalSize();
		return totalSize;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<BubbleChartData> getChildren() {
		return children;
	}
	
	public void setChildren(List<BubbleChartData> children) {
		this.children = children;
	}
	
	public void addChild( BubbleChartData child ) {
		this.children.add(child);
	}
	
	public boolean constainsChild( String name ) {
		return getChild(name) != null;
	}
	
	public BubbleChartData getChild( String name ) {
		for( BubbleChartData child : children )
			if( child.name.equals(name) )
				return child;
		return null;
	}
	
	public String toString(StringBuilder stringBuilder) {
		StringBuilder sb = null;
		
		if (stringBuilder == null) {
			sb = new StringBuilder();
		} else {
			sb = stringBuilder;
		}
		sb.append("{").append("\n")
			.append("\"name\": \"").append(this.getName()).append("\",\n")
			.append("\"description\": \"").append(this.getDescription()).append("\",\n")
			.append("\"discretion\": \"").append(this.getDiscretion()).append("\",\n")
			.append("\"result\": \"").append(this.getResult()).append("\",\n");
		if( this.getChildren() != null ) {
			if( this.getChildren().size() > 0 ) {
				sb.append("\"children\": [\n");				
				for(int i=0; i<this.getChildren().size(); i++) {
					sb.append(this.getChildren().get(i).toString(null));
					if( i<this.getChildren().size()-1 )
						sb.append(",\n");
				}
				sb.append("]\n");
			} else
				sb.append("\"size\" : ").append(this.getSize()).append("\n");
		}
		sb.append("}");
		return sb.toString();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDiscretion() {
		return discretion;
	}

	public void setDiscretion(String discretion) {
		this.discretion = discretion;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String value) {
		this.result = value;
	}
	
	public int getChildsSize() {
		int size = 0;
		for( BubbleChartData child : children )
			size += child.getSize();
		return size;
	}
}
