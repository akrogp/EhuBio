package es.ehubio.portal.bl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

public class Orcid {
	private static final String URL = "https://pub.orcid.org/v3.0/0000-0002-6433-8452/works";
	
	public List<Work> getWorks() {
		List<Work> works = new ArrayList<>();
		JSONObject json = requestWorks();
		JSONArray array = json.getJSONArray("group");
		for( int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			JSONObject summary = obj.getJSONArray("work-summary").getJSONObject(0);
			Work work = parseWork(summary);
			works.add(work);
		}
		return works;
	}

	private Work parseWork(JSONObject summary) {
		Work work = new Work();
		work.setId(summary.get("put-code").toString());
		work.setTitle(summary.getJSONObject("title").getJSONObject("title").getString("value"));
		work.setUrl(parseUrl(summary));		
		work.setType(summary.getString("type"));
		if( !summary.isNull("journal-title") )
			work.setJournal(summary.getJSONObject("journal-title").getString("value"));
		JSONObject date = summary.optJSONObject("publication-date");
		if( date != null ) {
			work.setYear(parseDate(date, "year"));
			work.setMonth(parseDate(date, "month"));
			work.setDay(parseDate(date, "day"));
		}
		return work;
	}

	private Integer parseDate(JSONObject date, String field) {
		JSONObject obj = date.optJSONObject(field);
		if( obj == null )
			return null;
		String value = obj.get("value").toString();
		return Integer.parseInt(value);
	}

	private String parseUrl(JSONObject summary) {
		if( !summary.isNull("url") )
			return summary.getJSONObject("url").getString("value");
		JSONArray extIds = summary.getJSONObject("external-ids").getJSONArray("external-id");
		for( int i = 0; i < extIds.length(); i++ ) {
			JSONObject extId = extIds.getJSONObject(i);
			if( extId.getString("external-id-type").equalsIgnoreCase("doi") )
				return "https://doi.org/" + extId.getString("external-id-value");
		}
		return null;
	}

	private JSONObject requestWorks() {
		String json = ClientBuilder.newClient().target(URL).request(MediaType.APPLICATION_JSON).get(String.class);
		return new JSONObject(json);
	}
}
