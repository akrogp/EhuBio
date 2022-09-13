package es.ehubio.wregex;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;

public final class InputGroup {	
	/**
	 * Motif positions are read if the last part of the header is in the form
	 * x-y;z-... If this pattern is not found, the whole sequence is interpreted
	 * as the motif
	 */
	public InputGroup(Fasta fasta) {
		mFasta = fasta;
		mId = fasta.getProteinName();
		loadMotifs();
		if (mMotifs.isEmpty())
			mMotifs.add(new InputMotif(mFasta, 1, mFasta.getSequence().length(), 100.0));
	}

	private void loadMotifs() {
		String[] fields = mFasta.getHeader().split("[ \t]");
		if (fields.length == 1)
			return;
		String str = fields[fields.length - 1];
		String valid = "0123456789;-@.";
		for (char c : str.toCharArray())
			if (valid.indexOf(c) == -1)
				return;
		int start, end;
		double w;
		String[] tmp;
		scores = true;
		for( String range : str.split(";") ) {
			fields = range.split("-");
			if (fields.length != 2)
				return;
			start = Integer.parseInt(fields[0]);
			tmp = fields[1].split("@");
			end = Integer.parseInt(tmp[0]);
			if( tmp.length != 2 ) {
				w = 100.0;
				scores = false;
			} else
				w = Double.parseDouble(tmp[1]);
			mMotifs.add(new InputMotif(mFasta, start, end, w));
		}
	}
	
	public Fasta getFasta() {
		return mFasta;
	}

	public String getSequence() {
		return mFasta.getSequence();
	}

	public String getHeader() {
		return mFasta.getHeader();
	}

	public List<InputMotif> getMotifs() {
		return mMotifs;
	}

	/** The first word of the fasta header */
	public String getId() {
		return mId;
	}

	public static List<InputGroup> readEntries(Reader rd) throws IOException,
			InvalidSequenceException {
		List<InputGroup> list = new ArrayList<InputGroup>();
		for (Fasta f : Fasta.readEntries(rd, SequenceType.PROTEIN))
			list.add(new InputGroup(f));
		return list;
	}

	public static void writeEntries(Writer wr, Iterable<InputGroup> list) {
		PrintWriter pw = new PrintWriter(wr);
		boolean first;
		for (InputGroup entry : list) {
			pw.print(">" + entry.getId() + " ");
			first = true;
			for (InputMotif motif : entry.getMotifs()) {
				if (first)
					first = false;
				else
					pw.print(';');
				pw.print(motif.getStart() + "-" + motif.getEnd() + "@" + motif.getWeight());
			}
			pw.println();
			pw.println(entry.getSequence());
		}
		pw.flush();
	}

	public boolean hasScores() {
		return scores;
	}

	private final Fasta mFasta;
	private final String mId;
	private final List<InputMotif> mMotifs = new ArrayList<InputMotif>();
	private boolean scores = false;
}