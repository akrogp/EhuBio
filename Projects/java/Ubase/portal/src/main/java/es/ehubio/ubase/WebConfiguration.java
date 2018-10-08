package es.ehubio.ubase;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public class WebConfiguration implements Configuration {

	@Override
	public String getSubmissionPath() {
		return getParam("ubase.path.submission");
	}

	@Override
	public String getArchivePath() {
		return getParam("ubase.path.archive");
	}
	
	private String getParam(String key) {
		if( context == null )
			context = FacesContext.getCurrentInstance().getExternalContext();
		return context.getInitParameter(key);
	}

	private ExternalContext context;
}
