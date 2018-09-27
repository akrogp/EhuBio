package es.ehubio.ubase.dl.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the GroupEvidence database table.
 * 
 */
@Entity
@NamedQuery(name="GroupEvidence.findAll", query="SELECT g FROM GroupEvidence g")
public class GroupEvidence implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private Boolean leading;
	private ProteinEvidence proteinEvidenceBean;
	private ProteinGroup proteinGroup;

	public GroupEvidence() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public Boolean getLeading() {
		return this.leading;
	}

	public void setLeading(Boolean leading) {
		this.leading = leading;
	}


	//uni-directional many-to-one association to ProteinEvidence
	@ManyToOne
	@JoinColumn(name="proteinEvidence")
	public ProteinEvidence getProteinEvidenceBean() {
		return this.proteinEvidenceBean;
	}

	public void setProteinEvidenceBean(ProteinEvidence proteinEvidenceBean) {
		this.proteinEvidenceBean = proteinEvidenceBean;
	}


	//uni-directional many-to-one association to ProteinGroup
	@ManyToOne
	@JoinColumn(name="group")
	public ProteinGroup getProteinGroup() {
		return this.proteinGroup;
	}

	public void setProteinGroup(ProteinGroup proteinGroup) {
		this.proteinGroup = proteinGroup;
	}

}