package es.ehubio.proteomics;

import java.util.ArrayList;
import java.util.List;

/**
 * Peptide-Spectrum Match in a MS/MS proteomics experiment.
 * 
 * @author gorka
 *
 */
public class Psm extends DecoyBase {
	private static int idCount = 1;
	private final int id;
	private int charge;
	private Double calcMz;
	private Double expMz;
	private Double massError;
	private Double massPpm;
	private Integer rank;
	private Spectrum spectrum;
	private Peptide peptide;
	private List<FragmentIon> ions = new ArrayList<>();
	
	public Psm() {
		id = idCount++;
	}
	
	public int getId() {
		return id;
	}
	
	public int getCharge() {
		return charge;
	}
	
	public void setCharge(int charge) {
		this.charge = charge;
	}
	
	public Double getCalcMz() {
		if( calcMz != null )
			return calcMz;
		if( expMz == null )
			return null;
		Double error = getMassError();
		if( error == null )
			return null;
		return expMz-error;
	}

	public void setCalcMz(double calcMz) {
		this.calcMz = calcMz;
	}

	public Double getExpMz() {
		if( expMz != null )
			return expMz;
		if( calcMz == null )
			return null;
		Double error = getMassError();
		if( error == null )
			return null;
		return calcMz+error;
	}

	public void setExpMz(double expMz) {
		this.expMz = expMz;
	}
	
	public Double getMassError() {
		if( massError != null )
			return massError;
		if( massPpm != null && calcMz != null )
			return massPpm*calcMz/1000000;
		if( calcMz != null && expMz != null )
			return expMz-calcMz;
		return null;
	}

	public void setMassError(double massError) {
		this.massError = massError;
	}

	public Double getMassPpm() {
		if( massPpm != null )
			return massPpm;
		if( massError != null && calcMz != null )
			return massError/calcMz*1000000;
		if( calcMz != null && expMz != null )
			return (expMz-calcMz)/calcMz*1000000;
		return null;
	}

	public void setMassPpm(double massPpm) {
		this.massPpm = massPpm;
	}
	
	public Integer getRank() {
		return rank;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public Spectrum getSpectrum() {
		return spectrum;
	}
	
	public void linkSpectrum(Spectrum spectrum) {
		/*if( this.spectrum != null )
			this.spectrum.getPsms().remove(this);*/
		this.spectrum = spectrum;
		if( spectrum != null )
			this.spectrum.linkPsm(this);
	}
	
	public Peptide getPeptide() {
		return peptide;
	}

	public void linkPeptide(Peptide peptide) {
		/*if( this.peptide != null )
			this.peptide.getPsms().remove(this);*/
		this.peptide = peptide;
		if( peptide != null )
			peptide.linkPsm(this);
	}
	
	public void guessIons() {
		if( !getIons().isEmpty() )
			return;
		
	}
	
	@Override
	public Boolean getDecoy() {
		if( peptide == null )
			return null;
		return peptide.getDecoy();
	}
	
	@Override
	public void setDecoy(Boolean decoy) {
		if( peptide != null )
			peptide.setDecoy(decoy);
	}
	
	@Override
	public String toString() {
		return ""+getId();
	}
	
	@Override
	protected String buildUniqueString() {
		return toString();
	}

	public List<FragmentIon> getIons() {
		return ions;
	}

	public void setIons(List<FragmentIon> ions) {
		this.ions = ions;
	}	
}