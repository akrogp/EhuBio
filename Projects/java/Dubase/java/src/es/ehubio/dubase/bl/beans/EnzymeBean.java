package es.ehubio.dubase.bl.beans;

import java.util.ArrayList;
import java.util.List;

import es.ehubio.dubase.dl.Enzyme;

public class EnzymeBean {
	private final Enzyme entity;
	private final List<EvidenceBean> substrates = new ArrayList<>();
	
	public EnzymeBean(Enzyme enzyme) {
		this.entity = enzyme;
	}
	
	public Enzyme getEntity() {
		return entity;
	}
	
	public List<EvidenceBean> getSubstrates() {
		return substrates;
	}
}
