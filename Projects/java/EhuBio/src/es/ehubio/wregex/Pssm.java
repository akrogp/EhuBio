package es.ehubio.wregex;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.io.Streams;
import es.ehubio.model.Aminoacid;
import es.ehubio.wregex.PssmBuilder.PssmBuilderException;

/** Inmutable class for PSSM */
public final class Pssm {	
	public class PssmException extends Exception {
		private static final long serialVersionUID = 1L;

		public PssmException(String msg) {
			super(msg);
		}
	}

	Pssm( Map<Aminoacid, List<Double>> pssm ) {
		this.pssm = new HashMap<>();
		int groups = 0;
		for( Aminoacid aa : pssm.keySet() ) {
			if( groups == 0 )
				groups = pssm.get(aa).size();
			else
				assert groups == pssm.get(aa).size();
			this.pssm.put(aa, new ArrayList<>(pssm.get(aa)));
		}
		this.groups = groups;
	}
	
	public int getGroups() {
		return groups;
	}
	
	public double getScore(Aminoacid aa, int pos) throws PssmException {
		List<Double> list = pssm.get(aa);
		//System.out.println(String.format("PSSM access: position=%d/%d, aminoacid=%c",pos+1,groups,aa.letter));
		if( pos < 0 || pos >= groups || list == null )
			throw new PssmException(String.format("Invalid PSSM access: position=%d/%d, aminoacid=%c",pos+1,groups,aa.letter));
		return list.get(pos);
	}
	
	public static Pssm load(String path, boolean doNormalization) throws IOException, PssmBuilderException{
		Reader reader = Streams.getTextReader(path);
		Pssm pssm = load(reader, doNormalization);
		reader.close();
		return pssm;
	}
	
	public static Pssm load(Reader reader, boolean doNormalization) throws IOException, PssmBuilderException{
		PssmBuilder builder = new PssmBuilder();
		builder.load(reader);
		if( doNormalization )
			builder.normalize();
		return builder.build();
	}
	
	public void save(Writer writer, String... header) {
		DecimalFormat df=new DecimalFormat("0");
		PrintWriter pw = new PrintWriter(writer);
		for( String str : header )
			pw.println("# " + str);
		for( Aminoacid aa : pssm.keySet() ) {
			pw.print(aa.letter);
			for( Double score : pssm.get(aa) )
				pw.print("\t" + df.format(score));
			pw.println();
		}
		pw.flush();
	}
	
	final Map<Aminoacid, List<Double>> pssm;
	final int groups;
}
