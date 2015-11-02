package es.ehubio.mymrm.data;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the InstrumentType database table.
 * 
 */
@Entity
@NamedQuery(name="InstrumentType.findAll", query="SELECT i FROM InstrumentType i")
public class InstrumentType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String description;

	private String name;

	//bi-directional many-to-one association to Instrument
	@OneToMany(mappedBy="instrumentTypeBean")
	private List<Instrument> instruments;

	public InstrumentType() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	public List<Instrument> getInstruments() {
		return this.instruments;
	}

	public void setInstruments(List<Instrument> instruments) {
		this.instruments = instruments;
	}

	public Instrument addInstrument(Instrument instrument) {
		getInstruments().add(instrument);
		instrument.setInstrumentTypeBean(this);

		return instrument;
	}

	public Instrument removeInstrument(Instrument instrument) {
		getInstruments().remove(instrument);
		instrument.setInstrumentTypeBean(null);

		return instrument;
	}

}