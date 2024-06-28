package es.ehubio.wregex.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@RequestScoped
public class TutorialView implements Serializable {
	private static final long serialVersionUID = 1L;

	public void downloadFasta(String name) {
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();
	    ec.responseReset();
	    ec.setResponseContentType("text/plain"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(length);
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\""+name+"\"");
        try {            
            OutputStream outputStream = ec.getResponseOutputStream();
            try(InputStream inputStream = ec.getResourceAsStream("/resources/data/"+name)) {
	            byte[] buffer = new byte[1024];
	            int bytesRead;
	            while ((bytesRead = inputStream.read(buffer)) != -1) {
	                outputStream.write(buffer, 0, bytesRead);
	            }
	            outputStream.flush();
	            fc.responseComplete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
