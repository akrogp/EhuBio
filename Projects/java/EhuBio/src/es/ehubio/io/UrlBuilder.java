package es.ehubio.io;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class UrlBuilder {
	private final StringBuilder url;
	private Map<String, String> query;
	
	public UrlBuilder(String url) {
		this.url = new StringBuilder(url);
	}
	
	public UrlBuilder(String protocol, String host, int port, String path) throws MalformedURLException {
		if( !path.startsWith("/") )
			path = "/" + path;
		url = new StringBuilder(new URL(protocol, host, port, path).toString());
	}
	
	public UrlBuilder(String protocol, String host, String path) throws MalformedURLException {
		if( !path.startsWith("/") )
			path = "/" + path;
		url = new StringBuilder(new URL(protocol, host, path).toString());
	}
	
	public UrlBuilder path(String path) {
		if( !path.startsWith("/") )
			url.append('/');
		url.append(path);
		return this;
	}
	
	public UrlBuilder pathf(String format, Object... args) {
		return path(String.format(format, args));
	}
	
	public UrlBuilder param(String name, Object value) {
		if( query == null )
			query = new HashMap<>();
		query.put(name,  value.toString());
		return this;
	}
	
	public String build() throws UnsupportedEncodingException {
		if( query == null || query.isEmpty() )
			return url.toString();
		StringBuilder str = new StringBuilder(url);
		str.append('?');
		boolean first = true;
		for( Entry<String, String> entry : query.entrySet() ) {
			if( !first )
				str.append('&');
			else
				first = false;
			str.append(entry.getKey());
			str.append('=');
			str.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}
		return str.toString();
	}
}
