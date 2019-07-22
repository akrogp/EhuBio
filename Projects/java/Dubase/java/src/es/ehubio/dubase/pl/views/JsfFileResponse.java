package es.ehubio.dubase.pl.views;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public class JsfFileResponse implements AutoCloseable {
	
	public OutputStream start(String mime, String name, int size) throws IOException {
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();
	    ec.responseReset();
	    ec.setResponseContentType(mime);
	    if( size > 0 )
	    	ec.setResponseContentLength(size);
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\""+name+"\"");
	    return ec.getResponseOutputStream();
	}
	
	public void sendFile(String mime, File file) throws IOException {
		try(OutputStream os = start(mime, file.getName(), (int)file.length())) {
			Files.copy(file.toPath(), os);
		}
	}

	@Override
	public void close() throws Exception {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.responseComplete();
	}	
}
