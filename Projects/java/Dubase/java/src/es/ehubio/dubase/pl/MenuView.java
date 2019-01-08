package es.ehubio.dubase.pl;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class MenuView implements Serializable {
	private static final long serialVersionUID = 1L;
	private int tab;
	
	public int getTab() {
		return tab;
	}
	
	public void setTab(int tab) {
		this.tab = tab;
	}
}
