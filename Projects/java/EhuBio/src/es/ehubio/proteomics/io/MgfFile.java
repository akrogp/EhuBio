package es.ehubio.proteomics.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import es.ehubio.proteomics.Spectrum;

public class MgfFile {
	public static List<Spectrum> loadSpectra( String path ) throws FileNotFoundException, IOException {
		BufferedReader rd;
		if( path.endsWith("gz") )
			rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path))));
		else
			rd = new BufferedReader(new FileReader(path));
		 List<Spectrum> spectra = loadSpectra(rd);
		 rd.close();
		 return spectra;
	}
	
	public static List<Spectrum> loadSpectra( BufferedReader rd ) throws IOException {
		List<Spectrum> spectra = new ArrayList<>();
		String line;
		Spectrum spectrum = null;
		while( (line=rd.readLine()) != null ) {
			if( line.startsWith("BEGIN IONS") )
				spectrum = new Spectrum();
			else if ( spectrum != null ) {
				if( line.startsWith("TITLE") )
					spectrum.setScan(line.replaceAll(".*scan=","").replaceAll("\"",""));
				else if( line.startsWith("RTINSECONDS") )
					spectrum.setRt(Double.parseDouble(line.replaceAll(".*=","")));
				else if( line.startsWith("PEPMASS") ) {
					String[] fields = line.split("[ \\t]");
					if( fields.length >= 2 )
						spectrum.setIntensity(Double.parseDouble(fields[1]));
				} else if( line.length() > 0 && Character.isDigit(line.charAt(0)) ) {
					String[] fields = line.split("[ \\t]");
					spectrum.getPeaks().add(new Spectrum.Peak(Double.parseDouble(fields[0]),Double.parseDouble(fields[1])));
				} else if( line.startsWith("END IONS") ) {
					spectra.add(spectrum);
					spectrum = null;
				}
			}
		}
		return spectra;
	}
}
