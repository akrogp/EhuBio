package es.ehubio.dubase.dl;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Evidence database table.
 * 
 */
@Entity
@NamedQuery(name="Evidence.findAll", query="SELECT e FROM Evidence e")
public class Evidence implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private double foldChange;
	private double pValue;
	private Experiment experimentBean;
	private Substrate substrateBean;

	public Evidence() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public double getFoldChange() {
		return this.foldChange;
	}

	public void setFoldChange(double foldChange) {
		this.foldChange = foldChange;
	}


	public double getPValue() {
		return this.pValue;
	}

	public void setPValue(double pValue) {
		this.pValue = pValue;
	}


	//uni-directional many-to-one association to Experiment
	@ManyToOne
	@JoinColumn(name="experiment")
	public Experiment getExperimentBean() {
		return this.experimentBean;
	}

	public void setExperimentBean(Experiment experimentBean) {
		this.experimentBean = experimentBean;
	}


	//uni-directional many-to-one association to Substrate
	@ManyToOne
	@JoinColumn(name="substrate")
	public Substrate getSubstrateBean() {
		return this.substrateBean;
	}

	public void setSubstrateBean(Substrate substrateBean) {
		this.substrateBean = substrateBean;
	}

}