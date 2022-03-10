package es.ehubio.portal.dl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Orcid {	
	public static List<Work> parseWorks(JSONObject json) {
		List<Work> works = new ArrayList<>();
		JSONArray array = json.getJSONArray("group");
		for( int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			JSONObject summary = obj.getJSONArray("work-summary").getJSONObject(0);
			Work work = parseWork(summary);
			works.add(work);
		}
		return works;
	}

	private static Work parseWork(JSONObject summary) {
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

	private static Integer parseDate(JSONObject date, String field) {
		JSONObject obj = date.optJSONObject(field);
		if( obj == null )
			return null;
		String value = obj.get("value").toString();
		return Integer.parseInt(value);
	}

	private static String parseUrl(JSONObject summary) {
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
}
