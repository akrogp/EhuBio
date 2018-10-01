package es.ehubio.ubase.pl;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class BaseView {
	public void showMessage(Severity type, String title, String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(type, title, message));
	}
	
	public void showInfo(String message) {
		showMessage(FacesMessage.SEVERITY_INFO, "Info", message);
	}
	
	public void showError(String message) {
		showMessage(FacesMessage.SEVERITY_ERROR, "Error!", message);
	}
	
	public void invalidateSession() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
	}
}
