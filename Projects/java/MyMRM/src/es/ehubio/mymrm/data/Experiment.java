package es.ehubio.mymrm.data;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;


/**
 * The persistent class for the Experiment database table.
 * 
 */
@Entity
@NamedQuery(name="Experiment.findAll", query="SELECT e FROM Experiment e")
public class Experiment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String contact;

	private String description;

	private String name;

	//bi-directional many-to-one association to Chromatography
	@ManyToOne
	@JoinColumn(name="chromatography")
	private Chromatography chromatographyBean;

	//bi-directional many-to-one association to FragmentationType
	@ManyToOne
	@JoinColumn(name="fragmentationType")
	private FragmentationType fragmentationTypeBean;

	//bi-directional many-to-one association to Instrument
	@ManyToOne
	@JoinColumn(name="instrument")
	private Instrument instrumentBean;

	//bi-directional many-to-one association to IonizationType
	@ManyToOne
	@JoinColumn(name="ionizationType")
	private IonizationType ionizationTypeBean;

	//bi-directional many-to-one association to PeptideEvidence
	@OneToMany(mappedBy="experimentBean")
	private List<PeptideEvidence> peptideEvidences;
	
	//bi-directional many-to-one association to ExperimentFile
	@OneToMany(mappedBy="experimentBean")
	private List<ExperimentFile> experimentFiles;

	public Experiment() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Chromatography getChromatographyBean() {
		return this.chromatographyBean;
	}

	public void setChromatographyBean(Chromatography chromatographyBean) {
		this.chromatographyBean = chromatographyBean;
	}

	public FragmentationType getFragmentationTypeBean() {
		return this.fragmentationTypeBean;
	}

	public void setFragmentationTypeBean(FragmentationType fragmentationTypeBean) {
		this.fragmentationTypeBean = fragmentationTypeBean;
	}

	public Instrument getInstrumentBean() {
		return this.instrumentBean;
	}

	public void setInstrumentBean(Instrument instrumentBean) {
		this.instrumentBean = instrumentBean;
	}

	public IonizationType getIonizationTypeBean() {
		return this.ionizationTypeBean;
	}

	public void setIonizationTypeBean(IonizationType ionizationTypeBean) {
		this.ionizationTypeBean = ionizationTypeBean;
	}

	public List<PeptideEvidence> getPeptideEvidences() {
		return this.peptideEvidences;
	}

	public void setPeptideEvidences(List<PeptideEvidence> peptideEvidences) {
		this.peptideEvidences = peptideEvidences;
	}

	public PeptideEvidence addPeptideEvidence(PeptideEvidence peptideEvidence) {
		getPeptideEvidences().add(peptideEvidence);
		peptideEvidence.setExperimentBean(this);

		return peptideEvidence;
	}

	public PeptideEvidence removePeptideEvidence(PeptideEvidence peptideEvidence) {
		getPeptideEvidences().remove(peptideEvidence);
		peptideEvidence.setExperimentBean(null);

		return peptideEvidence;
	}

	public List<ExperimentFile> getExperimentFiles() {
		return this.experimentFiles;
	}

	public void setExperimentFiles(List<ExperimentFile> experimentFiles) {
		this.experimentFiles = experimentFiles;
	}

	public ExperimentFile addExperimentFile(ExperimentFile experimentFile) {
		getExperimentFiles().add(experimentFile);
		experimentFile.setExperimentBean(this);

		return experimentFile;
	}

	public ExperimentFile removeExperimentFile(ExperimentFile experimentFile) {
		getExperimentFiles().remove(experimentFile);
		experimentFile.setExperimentBean(null);

		return experimentFile;
	}
}