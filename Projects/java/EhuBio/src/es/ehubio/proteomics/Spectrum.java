package es.ehubio.proteomics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Basic spectral information in a MS/MS proteomics experiment.
 * 
 * @author gorka
 *
 */
public class Spectrum {	
	private static int idCount = 1;
	private final int id;
	private String fileName;
	private String fileId;
	private final Set<Psm> psms = new HashSet<>();
	private String title;
	private String scan;
	private Double rt;
	private Double intensity;
	private List<Peak> peaks = new ArrayList<>();
	private String repName;
	private String uniqueString;
	
	public Spectrum() {
		id = idCount++;
	}
	
	public int getId() {
		return id;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileId() {
		return fileId;
	}
	
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}		

	public Set<Psm> getPsms() {
		return psms;
	}
	
	public boolean linkPsm( Psm psm ) {
		if( psm.getSpectrum() != this )
			psm.linkSpectrum(this);
		return psms.add(psm);
	}
	
	@Override
	public String toString() {
		return String.format("%s@%s", getFileId(), getFileName());
	}
	
	public String getUniqueString() {
		return uniqueString == null ? toString() : uniqueString;
	}
	
	public void setUniqueString( String uniqueString ) {
		this.uniqueString = uniqueString;
	}
	
	public String getScan() {
		return scan;
	}

	public void setScan(String scan) {
		this.scan = scan;
	}

	public Double getRt() {
		return rt;
	}

	public void setRt(Double rt) {
		this.rt = rt;
	}

	public List<Peak> getPeaks() {
		return peaks;
	}
	
	public void setPeaks( List<Peak> peaks ) {
		this.peaks = peaks;
	}

	public Double getIntensity() {
		return intensity;
	}

	public void setIntensity(Double intensity) {
		this.intensity = intensity;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRepName() {
		return repName;
	}

	public void setRepName(String repName) {
		this.repName = repName;
	}

	public static class Peak {
		private double mz;
		private double intensity;
		private int charge;
		public Peak() {			
		}
		public Peak( double mz, double intensity ) {
			this.mz = mz;
			this.intensity = intensity;
		}
		public double getMz() {
			return mz;
		}
		public void setMz(double mz) {
			this.mz = mz;
		}
		public double getIntensity() {
			return intensity;
		}
		public void setIntensity(double intensity) {
			this.intensity = intensity;
		}
		public int getCharge() {
			return charge;
		}
		public void setCharge(int charge) {
			this.charge = charge;
		}
		@Override
		public String toString() {
			return String.format("%.4f(%.2f)", mz, intensity);
		}
	}
}