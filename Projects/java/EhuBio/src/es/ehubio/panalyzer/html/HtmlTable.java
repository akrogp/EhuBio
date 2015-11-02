package es.ehubio.panalyzer.html;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlTable {
	private String title;
	private List<String> header;
	private List<List<String>> rows = new ArrayList<>();
	private Map<Integer, String> colStyles = new HashMap<>();
	private String style;
	private boolean hold;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setStyle(String style) {
		this.style = style;
	}
	
	public void setBorder( int width ) {
		if( width == 0 )
			setStyle("border: none");
		else
			setStyle(String.format("border: %dpx", width));
	}
	
	public void setHeader( String... cells ) {
		header = Arrays.asList(cells);
	}
	
	public void setColStyle( int col, String style ) {
		colStyles.put(col, style);
	}
	
	public void setHold(boolean hold) {
		this.hold = hold;
	}
	
	public void addRow( String... cells ) {
		rows.add(Arrays.asList(cells));
	}
	
	public void addPropertyRow(Object o, String... props) {
		for( String prop : props )
			try {
				Method method = o.getClass().getMethod(String.format("get%c%s", Character.toUpperCase(prop.charAt(0)), prop.substring(1)));
				Object val = method.invoke(o);
				if( val != null )
					addRow(prop,val.toString());
			} catch( Exception e ) {
				e.printStackTrace();
			}
	}
		
	public String render() {
		return render(true,header==null);
	}

	public String render( boolean oddStart, boolean colHeader ) {
		StringWriter buffer = new StringWriter();
		PrintWriter pw = new PrintWriter(buffer);
		pw.println(style==null?"<table>":String.format("<table style=\"%s\">", style));
		if( title != null )
			pw.println(String.format("<caption>%s</caption>", title));		
		if( header != null && header.size() != 0 ) {
			pw.println(renderHeader(header,oddStart));
			if(!hold)
				oddStart=!oddStart;
		}
		for( List<String> row : rows ) {
			pw.println(renderRow(row,oddStart,colHeader));
			if(!hold)
				oddStart=!oddStart;
		}
		pw.println("</table>");
		pw.close();
		return buffer.toString();
	}
	
	private String renderRow( List<String> cells, boolean odd, boolean colHeader ) {
		return renderAux("td",cells,odd,colHeader);
	}
	
	private String renderHeader( List<String> cells, boolean odd ) {
		return renderAux("th",cells,odd,false);
	}
	
	private String renderAux( String tag, List<String> cells, boolean odd, boolean colHeader ) {
		StringBuilder builder = new StringBuilder(odd ? "<tr class=\"odd\">" : "<tr class=\"even\">");
		for( int i = 0; i < cells.size(); i++ ) {
			builder.append('<');
			builder.append(i==0&&colHeader?"th":tag);
			String style = colStyles.get(i);
			if( style != null )
				builder.append(String.format(" style=\"%s\"", style));
			builder.append('>');
			builder.append(cells.get(i));
			builder.append("</");
			builder.append(i==0&&colHeader?"th":tag);			
			builder.append('>');
		}
		builder.append("</tr>");
		return builder.toString();
	}	
}