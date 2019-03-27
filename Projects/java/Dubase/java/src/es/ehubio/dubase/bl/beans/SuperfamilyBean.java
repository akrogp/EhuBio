package es.ehubio.dubase.bl.beans;

import java.util.ArrayList;
import java.util.List;

import es.ehubio.dubase.dl.entities.Superfamily;

public class SuperfamilyBean {
	private final Superfamily entity;
	private final List<EnzymeBean> enzymes = new ArrayList<>();
	
	public SuperfamilyBean(Superfamily superfamily) {
		this.entity = superfamily;
	}
	
	public Superfamily getEntity() {
		return entity;
	}
	
	public List<EnzymeBean> getEnzymes() {
		return enzymes;
	}
}
