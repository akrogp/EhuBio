package es.ehubio.wregex;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.io.UnixCfgReader;
import es.ehubio.model.Aminoacid;

public class PssmBuilder {
	public class PssmBuilderException extends Exception {
		private static final long serialVersionUID = 1L;

		public PssmBuilderException(String msg) {
			super(msg);
		}
	}
	
	public PssmBuilder() {		
	}
	
	public PssmBuilder( Reader reader ) throws IOException, PssmBuilderException {
		load(reader);
	}
	
	public void load(Reader reader) throws IOException, PssmBuilderException {
		UnixCfgReader rd = new UnixCfgReader(reader);
		String str;
		String[] fields;
		int i;
		List<Double> scores = new ArrayList<>();
		while( (str=rd.readLine()) != null ) {
			fields = str.split("[ \t]");
			scores.clear();
			for( i = 1; i < fields.length; i++ )
				try {
					scores.add(Double.parseDouble(fields[i]));
				} catch( NumberFormatException e ) {
					throw new PssmBuilderException("A number was expected");
				}
			setScores(Aminoacid.parseLetter(fields[0].charAt(0)), scores);
		}
	}
	
	public void normalize() {
		double max;
		for( int i = 0; i < groups; i++ ) {
			max = -1000;
			for( Aminoacid aa : pssm.keySet() )
				if( pssm.get(aa).get(i) > max )
					max = pssm.get(aa).get(i);
			for( Aminoacid aa : pssm.keySet() )
				pssm.get(aa).set(i, pssm.get(aa).get(i)-max);
		}				
	}
	
	public void setScores( Aminoacid aa, Collection<Double> scores ) throws PssmBuilderException {
		if( pssm.isEmpty() )
			groups = scores.size();
		else if( scores.size() != groups )
			throw new PssmBuilderException("Group count does not match");
		List<Double> list = pssm.get(aa);
		if( list == null ) {
			list = new ArrayList<>(scores);
			pssm.put(aa, list);
		} else {
			list.clear();
			list.addAll(scores);
		}
	}		
	
	public int getGroups() {
		return groups;
	}
	
	public Pssm build() {
		return new Pssm(pssm);
	}
	
	private Map<Aminoacid, List<Double>> pssm = new HashMap<>();
	private int groups = 0;	
}
