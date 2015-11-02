package es.ehubio.mymrm.presentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.mymrm.data.Peptide;
import es.ehubio.mymrm.data.PeptideEvidence;

public class PeptideBean {
	private Peptide entity;
	private List<PrecursorBean> precursors = new ArrayList<>();

	public Peptide getEntity() {
		return entity;
	}

	public void setEntity(Peptide entity) {
		this.entity = entity;
		
		// Build precursor beans using peptide evidences
		precursors.clear();
		Map<Double, PrecursorBean> map = new HashMap<>();
		for( PeptideEvidence evidence : entity.getPeptideEvidences() ) {
			PrecursorBean bean = map.get(evidence.getPrecursorBean().getMz());
			if( bean == null ) {
				bean = new PrecursorBean();
				bean.setMz(evidence.getPrecursorBean().getMz());
				bean.setCharge(evidence.getPrecursorBean().getCharge());
				map.put(bean.getMz(), bean);
			}
			DetailsBean experimentBean = new DetailsBean();
			experimentBean.setExperiment(evidence.getExperimentBean());
			experimentBean.setPrecursor(evidence.getPrecursorBean());
			experimentBean.setEvidence(evidence);
			bean.getExperiments().add(experimentBean);
		}
		precursors.addAll(map.values());
	}
	
	public String getMassSequence() {
		return entity.getMassSequence();
	}

	public String getSequence() {
		return entity.getSequence();
	}

	public List<PrecursorBean> getPrecursors() {
		return precursors;
	}
}
