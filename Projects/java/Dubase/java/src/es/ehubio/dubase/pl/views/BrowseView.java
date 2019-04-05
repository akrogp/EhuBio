package es.ehubio.dubase.pl.views;

import java.io.UnsupportedEncodingException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import es.ehubio.dubase.Thresholds;
import es.ehubio.io.UrlBuilder;

@Named
@RequestScoped
public class BrowseView {
	@Inject
	private PrefView prefs;
	
	public String getDataUrl() {
		Thresholds th = prefs.getThresholds();
		try {
			return new UrlBuilder("rest/browse/flare.json")
				.param("xth", th.getLog2FoldChange())
				.param("yth", th.getLog10PValue())
				.build();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
