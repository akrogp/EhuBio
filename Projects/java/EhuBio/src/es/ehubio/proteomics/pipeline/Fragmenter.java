package es.ehubio.proteomics.pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.proteomics.FragmentIon;
import es.ehubio.proteomics.IonType;
import es.ehubio.proteomics.Masses;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Ptm;
import es.ehubio.proteomics.Spectrum.Peak;

public class Fragmenter {
	private static final Map<Character, Double> mapMasses = new HashMap<>();
	private List<FragmentIon> totalIons = new ArrayList<>();
	private final Psm psm;
	private final double[] partialMasses;
	
	static {
		for(Masses mass : Masses.values() )
			mapMasses.put(mass.getLetter(), mass.getMass());
	}
	
	public Fragmenter( Psm psm ) {
		this.psm = psm;
		partialMasses = getPartialMasses(psm.getPeptide());
	}

	private static double[] getPartialMasses( Peptide peptide ) {
		double[] masses = new double[peptide.getSequence().length()];
		double mass;
		for( int i = 0; i < masses.length; i++ ) {
			mass = 0.0;
			for( Ptm ptm : peptide.getPtms() )
				if( ptm.getPosition()-1 == i )
					mass += ptm.getMassDelta();
			mass += mapMasses.get(Character.toUpperCase(peptide.getSequence().charAt(i)));
			masses[i]=mass;
		}
		return masses;
	}
	
	private void addIons( double baseMass, int charge, int pos, IonType base, IonType h20, IonType nh3 ) {
		FragmentIon ion = new FragmentIon();
		ion.setCharge(charge);
		ion.setMzExp(baseMass/charge);
		ion.setMzError(0.0);
		ion.setIndex(pos);
		ion.setType(base);
		totalIons.add(ion);
		
		if( h20 != null ) {
			ion = new FragmentIon();
			ion.setCharge(charge);
			ion.setMzExp((baseMass-Masses.H2O)/charge);
			ion.setMzError(0.0);
			ion.setIndex(pos);
			ion.setType(h20);
			totalIons.add(ion);
		}
		
		if( nh3 != null ) {
			ion = new FragmentIon();
			ion.setCharge(charge);
			ion.setMzExp((baseMass-Masses.NH3)/charge);
			ion.setMzError(0.0);
			ion.setIndex(pos);
			ion.setType(nh3);
			totalIons.add(ion);
		}
	}
	
	public void addPrecursorIons( boolean h20, boolean nh3 ) {
		double mh = psm.getCalcMz()*psm.getCharge();
		for( int charge = 1; charge <= psm.getCharge(); charge++ )
			addIons(mh+(charge-1)*Masses.Hydrogen, charge, 0, IonType.PRECURSOR, h20 ? IonType.PRECURSOR_H2O : null, nh3 ? IonType.PRECURSOR_NH3 : null);
	}
	
	public void addAIons( boolean h20, boolean nh3 ) {
		throw new UnsupportedOperationException("a ions not supported");
	}
	
	public void addBIons( boolean h20, boolean nh3 ) {
		int len = psm.getPeptide().getSequence().length();
		for( int charge = 1; charge <= psm.getCharge(); charge++ )
			for( int i = 0; i < len; i++ ) {
				double mass = 0.0;                
                for( int j = 0; j <= i; j++ )
                	mass += partialMasses[j];
                addIons(mass+charge*Masses.Hydrogen, charge, i+1, IonType.B, h20 ? IonType.B_H2O : null, nh3 ? IonType.B_NH3 : null);
			}
	}
	
	public void addCIons() {
		throw new UnsupportedOperationException("c ions not supported");
	}
	
	public void addDIons() {
		throw new UnsupportedOperationException("d ions not supported");
	}
	
	public void addVIons() {
		throw new UnsupportedOperationException("v ions not supported");
	}
	
	public void addWIons() {
		throw new UnsupportedOperationException("w ions not supported");
	}
	
	public void addXIons() {
		throw new UnsupportedOperationException("x ions not supported");
	}
	
	public void addYIons( boolean h20, boolean nh3 ) {
		int len = psm.getPeptide().getSequence().length();
		for( int charge = 1; charge <= psm.getCharge(); charge++ )
			for( int i = 0; i < len; i++ ) {
				double mass = 0.0;                
                for( int j = 0; j <= i; j++ )
                	mass += partialMasses[len-1-j];
                mass += Masses.C_term+Masses.Hydrogen;
                addIons(mass+charge*Masses.Hydrogen, charge, i+1, IonType.Y, h20 ? IonType.Y_H2O : null, nh3 ? IonType.Y_NH3 : null);
			}
	}
	
	public void addZIons() {
		throw new UnsupportedOperationException("z ions not supported");
	}
	
	public List<FragmentIon> match( double mzError, boolean sameCharge ) {
		List<FragmentIon> results = new ArrayList<>();		
		for( FragmentIon ion : totalIons ) {
			double bestError = mzError*10.0;
			FragmentIon result = null;
			for( Peak peak : psm.getSpectrum().getPeaks() ) {
				if( sameCharge && peak.getCharge() != ion.getCharge() )
					continue;
				double error = peak.getMz()-ion.getMzCalc();
				double absError = Math.abs(error);
				if( absError <= mzError && absError <= bestError ) {
					result = new FragmentIon();
					result.setCharge(ion.getCharge());
					result.setIndex(ion.getIndex());
					result.setType(ion.getType());					
					result.setMzExp(peak.getMz());
					result.setMzError(error);
					result.setIntensity(peak.getIntensity());
					bestError = absError;
				}
			}
			if( result != null )
				results.add(result);
		}
		filter(results);
		return results;
	}
	
	private void filter( List<FragmentIon> ions ) {
		Map<Integer, FragmentIon> map = new HashMap<>();
		for( FragmentIon ion : ions ) {
			FragmentIon prev = map.get(ion.getIndex());
			if( prev == null || ion.getMzError() < prev.getMzError() )
				map.put(ion.getIndex(), ion);
		}
		ions.clear();
		ions.addAll(map.values());
	}
}