package es.ehubio.dubase.bl;

import java.util.ArrayList;
import java.util.List;

import es.ehubio.dubase.dl.Clazz;

public class ClassBean {
	private final Clazz entity;
	private final List<SuperfamilyBean> superfamilies = new ArrayList<>();
	
	public ClassBean(Clazz clazz) {
		this.entity = clazz;
	}
	
	public Clazz getEntity() {
		return entity;
	}
	
	public List<SuperfamilyBean> getSuperfamilies() {
		return superfamilies;
	}
}
