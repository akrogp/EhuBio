package es.ehubio.dubase.dl.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * The persistent class for the Method database table.
 * 
 */
@XmlRootElement
@Entity
@NamedQuery(name="Method.findAll", query="SELECT m FROM Method m")
public class Method implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String columnType;
	private String description;
	private Double foldThreshold;
	private String instrument;
	private Boolean proteasomeInhibition;
	private Double pvalueThreshold;
	private Boolean silencing;
	private MethodSubtype methodSubtype;
	private MethodType methodType;

	public Method() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getColumnType() {
		return this.columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}


	@Column(length = 65535, columnDefinition="TEXT")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public Double getFoldThreshold() {
		return this.foldThreshold;
	}

	public void setFoldThreshold(Double foldThreshold) {
		this.foldThreshold = foldThreshold;
	}


	public String getInstrument() {
		return this.instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}


	public Boolean getProteasomeInhibition() {
		return this.proteasomeInhibition;
	}

	public void setProteasomeInhibition(Boolean proteasomeInhibition) {
		this.proteasomeInhibition = proteasomeInhibition;
	}


	public Double getPvalueThreshold() {
		return this.pvalueThreshold;
	}

	public void setPvalueThreshold(Double pvalueThreshold) {
		this.pvalueThreshold = pvalueThreshold;
	}


	public Boolean getSilencing() {
		return this.silencing;
	}

	public void setSilencing(Boolean silencing) {
		this.silencing = silencing;
	}


	//uni-directional many-to-one association to MethodSubtype
	@ManyToOne
	@JoinColumn(name="subtype")
	public MethodSubtype getSubtype() {
		return this.methodSubtype;
	}

	public void setSubtype(MethodSubtype methodSubtype) {
		this.methodSubtype = methodSubtype;
	}


	//uni-directional many-to-one association to MethodType
	@ManyToOne
	@JoinColumn(name="type")
	public MethodType getType() {
		return this.methodType;
	}

	public void setType(MethodType methodType) {
		this.methodType = methodType;
	}

	@XmlTransient
	@Transient
	public boolean isManual() {
		return getType().getId() == es.ehubio.dubase.dl.input.MethodType.MANUAL.ordinal();
	}
	
	@XmlTransient
	@Transient
	public boolean isProteomics() {
		return getType().getId() == es.ehubio.dubase.dl.input.MethodType.PROTEOMICS.ordinal();
	}

	@XmlTransient
	@Transient
	public boolean isUbiquitomics() {
		return getType().getId() == es.ehubio.dubase.dl.input.MethodType.UBIQUITOMICS.ordinal();
	}
}